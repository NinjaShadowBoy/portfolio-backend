package com.ninjashadowboy.portfolio.security.oauth2.user

import com.ninjashadowboy.portfolio.entities.AuthProvider

/**
 * Factory for creating OAuth2UserInfo instances based on the provider.
 */
object OAuth2UserInfoFactory {

    fun getOAuth2UserInfo(registrationId: String, attributes: Map<String, Any>): OAuth2UserInfo {
        return when (registrationId.lowercase()) {
            AuthProvider.GOOGLE.name.lowercase() -> GoogleOAuth2UserInfo(attributes)
            AuthProvider.GITHUB.name.lowercase() -> GithubOAuth2UserInfo(attributes)
            AuthProvider.FACEBOOK.name.lowercase() -> FacebookOAuth2UserInfo(attributes)
            else -> throw IllegalArgumentException("Sorry! Login with $registrationId is not supported yet.")
        }
    }
}
