package com.ninjashadowboy.portfolio.security.oauth2

import com.ninjashadowboy.portfolio.config.JwtService
import com.ninjashadowboy.portfolio.entities.User
import com.ninjashadowboy.portfolio.repositories.UserRepo
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder

/**
 * OAuth2 Authentication Success Handler.
 * 
 * This handler is invoked when OAuth2 authentication succeeds.
 * It generates a JWT token and redirects the user to the frontend
 * with the token as a query parameter.
 */
@Component
class OAuth2AuthenticationSuccessHandler(
    private val jwtService: JwtService,
    private val userRepo: UserRepo,
    @Value("\${app.oauth2.redirect-uri:http://localhost:4200/oauth2/redirect}") 
    private val redirectUri: String
) : SimpleUrlAuthenticationSuccessHandler() {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val targetUrl = determineTargetUrl(request, response, authentication)

        if (response.isCommitted) {
            log.debug("Response has already been committed. Unable to redirect to $targetUrl")
            return
        }

        clearAuthenticationAttributes(request)
        redirectStrategy.sendRedirect(request, response, targetUrl)
    }

    override fun determineTargetUrl(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ): String {
        // Get the user from the principal
        val user: User = when (val principal = authentication.principal) {
            is UserPrincipal -> {
                // OAuth2 authentication - fetch the full user from the database
                userRepo.findUserByEmail(principal.email)
                    ?: throw IllegalStateException("User not found: ${principal.email}")
            }
            is User -> principal
            else -> throw IllegalStateException("Unexpected principal type: ${principal.javaClass}")
        }

        // Generate JWT token
        val token = jwtService.generateToken(user)

        log.info("OAuth2 authentication successful for user: ${user.email}")

        return UriComponentsBuilder.fromUriString(redirectUri)
            .queryParam("token", token)
            .build()
            .toUriString()
    }
}
