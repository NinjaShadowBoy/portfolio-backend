package com.ninjashadowboy.portfolio.security.oauth2.user

/**
 * Facebook OAuth2 User Information.
 * 
 * Extracts user information from Facebook's OAuth2 response attributes.
 * 
 * Facebook provides attributes like:
 * - id: User's unique identifier
 * - name: Full name
 * - email: Email address
 * - picture: Profile picture object with data.url
 */
class FacebookOAuth2UserInfo(
    attributes: Map<String, Any>
) : OAuth2UserInfo(attributes) {

    override fun getId(): String {
        return attributes["id"] as String
    }

    override fun getName(): String {
        return attributes["name"] as String
    }

    override fun getEmail(): String {
        return attributes["email"] as String
    }

    override fun getImageUrl(): String? {
        if (attributes.containsKey("picture")) {
            val pictureObj = attributes["picture"] as Map<*, *>
            if (pictureObj.containsKey("data")) {
                val dataObj = pictureObj["data"] as Map<*, *>
                if (dataObj.containsKey("url")) {
                    return dataObj["url"] as String
                }
            }
        }
        return null
    }
}
