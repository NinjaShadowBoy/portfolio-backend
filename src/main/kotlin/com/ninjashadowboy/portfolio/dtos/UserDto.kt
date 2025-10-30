package com.ninjashadowboy.portfolio.dtos

import com.ninjashadowboy.portfolio.entities.Role
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

/**
 * Data Transfer Object representing user information.
 * 
 * This DTO contains public user information returned in API responses.
 * Sensitive data like passwords are excluded.
 * 
 * @property id Unique user identifier
 * @property email User's email address
 * @property name User's full name
 * @property role User's role in the system
 * @property createdAt Account creation timestamp
 * @property lastLoginAt Last login timestamp (nullable)
 */
@Schema(
    description = "User profile information (excludes sensitive data like passwords)"
)
data class UserDto(
    @Schema(
        description = "Unique identifier for the user",
        example = "1",
        required = true
    )
    val id: Long,
    
    @Schema(
        description = "User's email address",
        example = "john.doe@example.com",
        required = true,
        format = "email"
    )
    val email: String,
    
    @Schema(
        description = "User's full name",
        example = "John Doe",
        required = true
    )
    val name: String,
    
    @Schema(
        description = "User's role in the system",
        example = "USER",
        required = true,
        allowableValues = ["USER", "ADMIN"]
    )
    val role: Role,
    
    @Schema(
        description = "Timestamp when the user account was created",
        example = "2025-01-15T10:30:00",
        required = true,
        format = "date-time"
    )
    val createdAt: LocalDateTime,
    
    @Schema(
        description = "Timestamp of the user's last login (null if never logged in)",
        example = "2025-10-30T14:25:30",
        required = false,
        nullable = true,
        format = "date-time"
    )
    val lastLoginAt: LocalDateTime?
)

/**
 * Data Transfer Object for user registration.
 * 
 * Contains the required information to create a new user account.
 * All fields are validated to ensure they are not blank.
 * 
 * @property email New user's email address (must be unique and valid)
 * @property password User's chosen password (should meet security requirements)
 * @property name User's full name
 */
@Schema(
    description = "Request payload for registering a new user account"
)
data class UserRegistrationDto(
    @Schema(
        description = "Email address for the new account (must be unique)",
        example = "newuser@example.com",
        required = true,
        format = "email",
        minLength = 5,
        maxLength = 100
    )
    val email: String,
    
    @Schema(
        description = "Password for the new account (minimum 8 characters recommended, should include letters, numbers, and special characters)",
        example = "SecurePass123!",
        required = true,
        format = "password",
        minLength = 8
    )
    val password: String,
    
    @Schema(
        description = "Full name of the user",
        example = "Jane Smith",
        required = true,
        minLength = 2,
        maxLength = 100
    )
    val name: String
) {
    init {
        require(email.isNotBlank()) { "Email cannot be blank" }
        require(password.isNotBlank()) { "Password cannot be blank" }
        require(name.isNotBlank()) { "Name cannot be blank" }
    }
}

/**
 * Data Transfer Object for updating user information.
 * 
 * All fields are optional, allowing partial updates.
 * Only non-null fields will be updated.
 * 
 * @property name Updated user name (optional)
 * @property email Updated email address (optional, must be unique)
 */
@Schema(
    description = "Request payload for updating user profile information (all fields optional)"
)
data class UserUpdateDto(
    @Schema(
        description = "Updated full name",
        example = "John Updated Doe",
        required = false,
        nullable = true
    )
    val name: String? = null,
    
    @Schema(
        description = "Updated email address (must be unique if provided)",
        example = "updated.email@example.com",
        required = false,
        nullable = true,
        format = "email"
    )
    val email: String? = null
) 