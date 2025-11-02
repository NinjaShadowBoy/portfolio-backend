package com.ninjashadowboy.portfolio.security.oauth2

import com.ninjashadowboy.portfolio.entities.AuthProvider
import com.ninjashadowboy.portfolio.entities.Role
import com.ninjashadowboy.portfolio.entities.User
import com.ninjashadowboy.portfolio.repositories.UserRepo
import com.ninjashadowboy.portfolio.security.oauth2.user.OAuth2UserInfo
import com.ninjashadowboy.portfolio.security.oauth2.user.OAuth2UserInfoFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.time.LocalDateTime

/**
 * Custom OAuth2 User Service.
 * 
 * This service handles the OAuth2 authentication flow:
 * 1. Receives OAuth2 user information from the provider
 * 2. Checks if the user already exists in the database
 * 3. If user exists, updates their information
 * 4. If user doesn't exist, registers a new user
 * 5. Returns the authenticated OAuth2User
 */
@Service
class CustomOAuth2UserService(
    private val userRepo: UserRepo
) : DefaultOAuth2UserService() {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    override fun loadUser(oAuth2UserRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(oAuth2UserRequest)

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User)
        } catch (ex: Exception) {
            log.error("Error processing OAuth2 user", ex)
            throw InternalAuthenticationServiceException(ex.message, ex.cause)
        }
    }

    private fun processOAuth2User(oAuth2UserRequest: OAuth2UserRequest, oAuth2User: OAuth2User): OAuth2User {
        val registrationId = oAuth2UserRequest.clientRegistration.registrationId
        val oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.attributes)

        if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw IllegalArgumentException("Email not found from OAuth2 provider")
        }

        val existingUser = userRepo.findUserByEmail(oAuth2UserInfo.getEmail())
        val user: User

        if (existingUser != null) {
            // User already exists
            if (existingUser.provider != AuthProvider.valueOf(registrationId.uppercase())) {
                throw IllegalArgumentException(
                    "Looks like you're signed up with ${existingUser.provider} account. " +
                            "Please use your ${existingUser.provider} account to login."
                )
            }
            user = updateExistingUser(existingUser, oAuth2UserInfo)
        } else {
            // Register new user
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo)
        }

        return UserPrincipal.create(user, oAuth2User.attributes)
    }

    private fun registerNewUser(oAuth2UserRequest: OAuth2UserRequest, oAuth2UserInfo: OAuth2UserInfo): User {
        val provider = AuthProvider.valueOf(oAuth2UserRequest.clientRegistration.registrationId.uppercase())
        
        val user = User(
            email = oAuth2UserInfo.getEmail(),
            name = oAuth2UserInfo.getName(),
            provider = provider,
            providerId = oAuth2UserInfo.getId(),
            imageUrl = oAuth2UserInfo.getImageUrl(),
            emailVerified = true,
            role = Role.USER,
            lastLoginAt = LocalDateTime.now()
        )

        log.info("Registering new OAuth2 user: ${user.email} via ${user.provider}")
        return userRepo.save(user)
    }

    private fun updateExistingUser(existingUser: User, oAuth2UserInfo: OAuth2UserInfo): User {
        // Update fields directly on the existing entity instead of using copy()
        // This prevents Hibernate's "don't change the reference to a collection with delete-orphan" error
        existingUser.name = oAuth2UserInfo.getName()
        existingUser.imageUrl = oAuth2UserInfo.getImageUrl()
        existingUser.lastLoginAt = LocalDateTime.now()

        log.info("Updating existing OAuth2 user: ${existingUser.email}")
        return userRepo.save(existingUser)
    }
}
