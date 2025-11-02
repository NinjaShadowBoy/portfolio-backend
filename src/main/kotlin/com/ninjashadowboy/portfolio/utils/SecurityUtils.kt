package com.ninjashadowboy.portfolio.utils

import com.ninjashadowboy.portfolio.entities.User
import org.springframework.security.core.Authentication

/**
 * Utility functions for security-related operations.
 */
object SecurityUtils {
    
    /**
     * Extracts the user ID from the authentication context.
     * 
     * @throws IllegalStateException if authentication principal is not a User
     */
    fun getUserIdFromAuth(authentication: Authentication): Long {
        val principal = authentication.principal
        require(principal is User) { 
            "Authentication principal must be a User instance" 
        }
        return principal.id
    }
    
    /**
     * Extracts the User entity from the authentication context.
     * 
     * @throws IllegalStateException if authentication principal is not a User
     */
    fun getUserFromAuth(authentication: Authentication): User {
        val principal = authentication.principal
        require(principal is User) { 
            "Authentication principal must be a User instance" 
        }
        return principal
    }
    
    /**
     * Checks if the current user owns the resource.
     */
    fun verifyOwnership(authentication: Authentication, ownerId: Long, operation: String) {
        val currentUserId = getUserIdFromAuth(authentication)
        if (currentUserId != ownerId) {
            throw com.ninjashadowboy.portfolio.exceptions.InsufficientPermissionException(operation)
        }
    }
}
