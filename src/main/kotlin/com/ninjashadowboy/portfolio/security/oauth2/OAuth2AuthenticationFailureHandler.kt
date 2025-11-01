package com.ninjashadowboy.portfolio.security.oauth2

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.io.IOException

/**
 * OAuth2 Authentication Failure Handler.
 * 
 * This handler is invoked when OAuth2 authentication fails.
 * It redirects the user to the frontend with an error message.
 */
@Component
class OAuth2AuthenticationFailureHandler(
    @Value("\${app.oauth2.redirect-uri:http://localhost:4200/oauth2/redirect}")
    private val redirectUri: String
) : SimpleUrlAuthenticationFailureHandler() {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @Throws(IOException::class)
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        val targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
            .queryParam("error", exception.localizedMessage)
            .build()
            .toUriString()

        log.error("OAuth2 authentication failed: ${exception.message}")

        redirectStrategy.sendRedirect(request, response, targetUrl)
    }
}
