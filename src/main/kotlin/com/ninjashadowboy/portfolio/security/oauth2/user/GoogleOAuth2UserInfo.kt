package com.ninjashadowboy.portfolio.security.oauth2.user

/**
 * Google OAuth2 User Information.
 * 
 * Extracts user information from Google's OAuth2 response attributes.
 * 
 * Google provides attributes like:
 * - sub: User's unique identifier
 * - name: Full name
 * - email: Email address
 * - picture: Profile picture URL
 * - email_verified: Whether email is verified
 */
class GoogleOAuth2UserInfo(
    attributes: Map<String, Any>
) : OAuth2UserInfo(attributes) {

    override fun getId(): String {
        return attributes["sub"] as String
    }

    override fun getName(): String {
        return attributes["name"] as String
    }

    override fun getEmail(): String {
        return attributes["email"] as String
    }

    override fun getImageUrl(): String? {
        return attributes["picture"] as String?
    }
}
