package com.ninjashadowboy.portfolio.security.oauth2.user

/**
 * GitHub OAuth2 User Information.
 * 
 * Extracts user information from GitHub's OAuth2 response attributes.
 * 
 * GitHub provides attributes like:
 * - id: User's unique identifier
 * - name: Full name (can be null)
 * - email: Email address (can be null if not public)
 * - avatar_url: Profile picture URL
 * - login: GitHub username
 */
class GithubOAuth2UserInfo(
    attributes: Map<String, Any>
) : OAuth2UserInfo(attributes) {

    override fun getId(): String {
        return (attributes["id"] as Int).toString()
    }

    override fun getName(): String {
        return (attributes["name"] as String?) ?: (attributes["login"] as String)
    }

    override fun getEmail(): String {
        return attributes["email"] as String
    }

    override fun getImageUrl(): String? {
        return attributes["avatar_url"] as String?
    }
}
