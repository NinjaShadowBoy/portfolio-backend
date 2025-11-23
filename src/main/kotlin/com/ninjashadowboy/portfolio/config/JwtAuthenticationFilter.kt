package com.ninjashadowboy.portfolio.config

import com.ninjashadowboy.portfolio.services.UserService
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.lang.NonNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

/**
 * JWT Authentication Filter for the Portfolio API.
 *
 * This filter handles JWT-based authentication for incoming HTTP requests. It extracts JWT tokens
 * from the Authorization header, validates them, and sets up the Spring Security context for
 * authenticated users.
 *
 * Features:
 * - Secure token extraction from Authorization header
 * - Comprehensive token validation
 * - Proper error handling and logging
 * - Performance optimizations
 * - Security best practices
 *
 * @author NinjaShadowBoy
 * @version 2.0
 * @since 1.0
 */
@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userService: UserService
) : OncePerRequestFilter() {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val BEARER_PREFIX = "Bearer "
        private const val BEARER_PREFIX_LENGTH = 7
        private const val MAX_TOKEN_LENGTH = 10000 // Reasonable limit for JWT tokens
    }

    /**
     * Main filter method that processes each HTTP request.
     *
     * @param request The HTTP request
     * @param response The HTTP response
     * @param filterChain The filter chain to continue processing
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        @NonNull request: HttpServletRequest,
        @NonNull response: HttpServletResponse,
        @NonNull filterChain: FilterChain
    ) {
        try {
            // Skip authentication for OPTIONS requests (CORS preflight)
            if (request.method == "OPTIONS") {
                filterChain.doFilter(request, response)
                return
            }

            val jwt = extractJwtFromRequest(request)

            if (jwt != null && SecurityContextHolder.getContext().authentication == null) {
                processJwtToken(jwt, request, response)
            }
        } catch (ex: Exception) {
            log.error("JWT authentication filter error: {}", ex.message, ex)
            handleAuthenticationError(response, "Authentication failed")
        }

        filterChain.doFilter(request, response)
    }

    /**
     * Processes a JWT token and sets up authentication if valid.
     *
     * @param jwt The JWT token to process
     * @param request The HTTP request
     * @param response The HTTP response
     */
    private fun processJwtToken(
        jwt: String,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        try {
            // Extract username from token
            val start = System.nanoTime()
            val username = jwtService.extractUsername(jwt)
            val end = System.nanoTime()
            log.info("JWT parsing took ${(end - start) / 1_000_000} ms")


            if (!StringUtils.hasText(username)) {
                log.warn("JWT token contains no username")
                handleAuthenticationError(response, "Invalid token format")
                return
            }

            // Load user details
            val userDetails = userService.loadUserByUsername(username)
            if (userDetails == null) {
                log.warn("User not found for username: {}", username)
                handleAuthenticationError(response, "User not found")
                return
            }

            // Validate token
            if (!jwtService.isTokenValid(jwt, userDetails)) {
                log.warn("Invalid JWT token for user: {}", username)
                handleAuthenticationError(response, "Invalid or expired token")
                return
            }

            // Set up authentication context
            setupAuthenticationContext(userDetails, request)
            log.debug("Authentication successful for user: {}", username)
        } catch (ex: Exception) {
            log.error("Error processing JWT token: {}", ex.message, ex)
            handleAuthenticationError(response, "Token processing error")
        }
    }

    /**
     * Sets up the Spring Security authentication context.
     *
     * @param userDetails The authenticated user details
     * @param request The HTTP request
     */
    private fun setupAuthenticationContext(userDetails: UserDetails, request: HttpServletRequest) {
        val authentication =
            UsernamePasswordAuthenticationToken(
                userDetails,
                null, // credentials are null for JWT authentication
                userDetails.authorities
            )

        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authentication
    }

    /**
     * Extracts JWT token from the HTTP request.
     *
     * Priority order:
     * 1. Authorization header (Bearer token)
     * 2. X-Auth-Token header (fallback)
     *
     * @param request The HTTP request
     * @return The JWT token if found, null otherwise
     */
    private fun extractJwtFromRequest(request: HttpServletRequest): String? {
        // Try Authorization header first (standard approach)
        val authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
            val token = authHeader.substring(BEARER_PREFIX_LENGTH).trim()
            if (isValidTokenFormat(token)) {
                log.debug("JWT token extracted from Authorization header")
                return token
            }
        }

        // Fallback to X-Auth-Token header
        val xAuthToken = request.getHeader("X-Auth-Token")
        if (StringUtils.hasText(xAuthToken) && isValidTokenFormat(xAuthToken)) {
            log.debug("JWT token extracted from X-Auth-Token header")
            return xAuthToken.trim()
        }

        log.debug("No valid JWT token found in request headers")
        return null
    }

    /**
     * Validates the basic format of a JWT token.
     *
     * @param token The token to validate
     * @return true if the token format is valid, false otherwise
     */
    private fun isValidTokenFormat(token: String): Boolean {
        return token.isNotBlank() &&
                token.length <= MAX_TOKEN_LENGTH &&
                token.matches(Regex("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$"))
    }

    /**
     * Handles authentication errors by setting appropriate HTTP status and response.
     *
     * @param response The HTTP response
     * @param message The error message
     */
    private fun handleAuthenticationError(response: HttpServletResponse, message: String) {
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = "application/json"

        val errorResponse =
            """
            {
                "status": 401,
                "error": "Unauthorized",
                "code": "AUTHENTICATION_FAILED",
                "message": "$message",
                "timestamp": "${java.time.Instant.now()}"
            }
        """.trimIndent()

        try {
            response.writer.write(errorResponse)
        } catch (ex: IOException) {
            log.error("Failed to write error response: {}", ex.message, ex)
        }
    }

    /**
     * Determines if the filter should be applied to the given request.
     *
     * @param request The HTTP request
     * @return true if the filter should process the request, false otherwise
     */
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI

        // Skip authentication for public endpoints
        return path.startsWith("/api/v1/auth/") ||
            path.startsWith("/css/") ||
            path.startsWith("/js/") ||
            path.startsWith("/images/") ||
            path.startsWith("/swagger-ui") ||
                path.startsWith("/api-docs") ||
                path == "/favicon.ico" ||
                request.method == "OPTIONS"
    }
}
