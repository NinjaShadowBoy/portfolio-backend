package com.ninjashadowboy.portfolio.security.oauth2.user

/**
 * OAuth2 User Information interface.
 * 
 * This interface defines the contract for extracting user information
 * from different OAuth2 providers (Google, GitHub, Facebook, etc.)
 */
abstract class OAuth2UserInfo(
    protected val attributes: Map<String, Any>
) {
    abstract fun getId(): String
    abstract fun getName(): String
    abstract fun getEmail(): String
    abstract fun getImageUrl(): String?
}
