package com.ninjashadowboy.portfolio.entities

/**
 * OAuth2 Authentication Provider enumeration.
 * 
 * Represents the different authentication providers supported by the application.
 * - LOCAL: Traditional email/password authentication
 * - GOOGLE: Google OAuth2 authentication
 * - GITHUB: GitHub OAuth2 authentication
 * - FACEBOOK: Facebook OAuth2 authentication
 */
enum class AuthProvider {
    LOCAL,
    GOOGLE,
    GITHUB,
    FACEBOOK
}
