package com.ninjashadowboy.portfolio.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjashadowboy.portfolio.dtos.LoginResponse
import com.ninjashadowboy.portfolio.dtos.toUserDto
import com.ninjashadowboy.portfolio.entities.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

/**
 * JWT Service for token generation, validation, and management.
 *
 * This service handles all JWT-related operations including token generation, parsing, validation,
 * and claim extraction. It provides secure token handling with proper error management and logging.
 *
 * Features:
 * - Secure token generation with configurable expiration
 * - Comprehensive token validation
 * - Detailed error handling and logging
 * - Performance optimizations
 * - Security best practices
 *
 * @author NinjaShadowBoy
 * @version 2.0
 * @since 1.0
 */
@Service
class JwtService(private val objectMapper: ObjectMapper) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @Value("\${jwt.secret}")
    private lateinit var secretKey: String

    @Value("\${jwt.expiration:86400000}") // Default: 24 hours
    private var tokenExpirationMs: Long = 24 * 60 * 60 * 1000

    @Value("\${jwt.issuer:PortfolioApp}")
    private var tokenIssuer: String = "PortfolioApp"

    companion object {
        private const val ROLES_CLAIM = "roles"
        private const val USER_ID_CLAIM = "userId"
        private const val TOKEN_TYPE_CLAIM = "type"
        private const val ACCESS_TOKEN_TYPE = "ACCESS"
    }

    /**
     * Generates a JWT token for the given user.
     *
     * @param user The user details
     * @param extraClaims Additional claims to include in the token
     * @param expiryMs Token expiration time in milliseconds
     * @return The generated JWT token
     */
    fun generateToken(
        user: UserDetails, extraClaims: Map<String, Any> = emptyMap(), expiryMs: Long = tokenExpirationMs
    ): String {
        log.debug("Generating JWT token for user: {}", user.username)

        val now = Date()
        val expiration = Date(System.currentTimeMillis() + expiryMs)

        val token = Jwts.builder().id(UUID.randomUUID().toString()) // JTI: unique token identifier
            .issuer(tokenIssuer).subject(user.username).issuedAt(now).expiration(expiration)
            .claim(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE).claim(ROLES_CLAIM, user.authorities.map { it.authority })
            .claims(extraClaims).signWith(getSignInKey()).compact()

        log.debug("JWT token generated successfully for user: {}", user.username)
        return token
    }

    fun generateLoginResponse(
        user: User, extraClaims: Map<String, Any> = emptyMap(), expiryMs: Long = tokenExpirationMs
    ): LoginResponse {
        return LoginResponse(
            token = generateToken(user, extraClaims, expiryMs),
            user = user.toUserDto(),
            expiresIn = tokenExpirationMs,
        )
    }

    /**
     * Generates a JWT token with user ID claim.
     *
     * @param user The user details
     * @param userId The user ID to include in the token
     * @param expiryMs Token expiration time in milliseconds
     * @return The generated JWT token
     */
    fun generateTokenWithUserId(
        user: UserDetails, userId: Long, expiryMs: Long = tokenExpirationMs
    ): String {
        val extraClaims = mapOf(USER_ID_CLAIM to userId)
        return generateToken(user, extraClaims, expiryMs)
    }

    /**
     * Extracts a specific claim from a JWT token.
     *
     * @param token The JWT token
     * @param resolver Function to extract the specific claim
     * @return The extracted claim value, or null if not found
     */
    fun <T> extractClaim(token: String, resolver: (Claims) -> T?): T? {
        return try {
            val claims = parseClaims(token)
            resolver(claims)
        } catch (ex: Exception) {
            log.warn("Failed to extract claim from token: {}", ex.message)
            null
        }
    }

    /**
     * Extracts the username (subject) from a JWT token.
     *
     * @param token The JWT token
     * @return The username, or null if not found
     */
    fun extractUsername(token: String): String? {
        return extractClaim(token) { it.subject }
    }

    /**
     * Extracts the user ID from a JWT token.
     *
     * @param token The JWT token
     * @return The user ID, or null if not found
     */
    fun extractUserId(token: String): Long? {
        return extractClaim(token) { it[USER_ID_CLAIM] as? Long }
    }

    /**
     * Extracts the roles from a JWT token.
     *
     * @param token The JWT token
     * @return List of roles, or empty list if not found
     */
    fun extractRoles(token: String): List<String> {
        return extractClaim(token) { claims ->
            @Suppress("UNCHECKED_CAST") (claims[ROLES_CLAIM] as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
        } ?: emptyList()
    }

    /**
     * Extracts the token expiration date from a JWT token.
     *
     * @param token The JWT token
     * @return The expiration date, or null if not found
     */
    fun extractExpiration(token: String): Date? {
        return extractClaim(token) { it.expiration }
    }

    /**
     * Checks if a JWT token is valid for the given user.
     *
     * @param token The JWT token to validate
     * @param user The user details to validate against
     * @return true if the token is valid, false otherwise
     */
    fun isTokenValid(token: String, user: UserDetails): Boolean {
        return try {
            val username = extractUsername(token)
            val expired = isTokenExpired(token)
            val valid = username == user.username && !expired

            log.debug(
                "Token validation for {}: subject={}, expired={}, valid={}", user.username, username, expired, valid
            )

            valid
        } catch (ex: Exception) {
            log.warn("Token validation error for {}: {}", user.username, ex.message)
            false
        }
    }

    /**
     * Checks if a JWT token has expired.
     *
     * @param token The JWT token to check
     * @return true if the token has expired, false otherwise
     */
    fun isTokenExpired(token: String): Boolean {
        return try {
            val expiration = extractExpiration(token)
            expiration?.before(Date()) ?: true
        } catch (ex: Exception) {
            log.warn("Error checking token expiration: {}", ex.message)
            true // Consider expired if we can't determine
        }
    }

    /**
     * Gets the time until token expiration in milliseconds.
     *
     * @param token The JWT token
     * @return Time until expiration in milliseconds, or null if expired/error
     */
    fun getTimeUntilExpiration(token: String): Long? {
        return try {
            val expiration = extractExpiration(token)
            expiration?.let { exp ->
                val timeUntilExpiration = exp.time - System.currentTimeMillis()
                if (timeUntilExpiration > 0) timeUntilExpiration else null
            }
        } catch (ex: Exception) {
            log.warn("Error calculating time until expiration: {}", ex.message)
            null
        }
    }

    /**
     * Parses and validates JWT claims.
     *
     * @param token The JWT token to parse
     * @return The parsed claims
     * @throws JwtException if the token is invalid
     */
    private fun parseClaims(token: String): Claims {
        return try {
            val claims = Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).payload

            log.debug("JWT claims parsed successfully")
            claims
        } catch (ex: ExpiredJwtException) {
            log.warn("JWT token has expired")
            throw ex
        } catch (ex: UnsupportedJwtException) {
            log.warn("JWT token is unsupported: {}", ex.message)
            throw ex
        } catch (ex: MalformedJwtException) {
            log.warn("JWT token is malformed: {}", ex.message)
            throw ex
        } catch (ex: SignatureException) {
            log.warn("JWT token signature is invalid: {}", ex.message)
            throw ex
        } catch (ex: IllegalArgumentException) {
            log.warn("JWT token is empty or null: {}", ex.message)
            throw ex
        } catch (ex: Exception) {
            log.error("Unexpected error parsing JWT token: {}", ex.message, ex)
            throw ex
        }
    }

    /**
     * Gets the signing key for JWT operations.
     *
     * @return The secret key
     */
    private val signingKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(secretKey.toByteArray())
    }

    private fun getSignInKey(): SecretKey = signingKey

}
