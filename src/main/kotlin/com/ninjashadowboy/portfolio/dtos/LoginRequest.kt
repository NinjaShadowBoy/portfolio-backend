package com.ninjashadowboy.portfolio.dtos

import io.swagger.v3.oas.annotations.media.Schema
import kotlin.text.isNotBlank

/**
 * Data Transfer Object for user login requests.
 * 
 * This DTO is used to authenticate users via email and password.
 * Upon successful authentication, a JWT token is returned.
 * 
 * @property email User's email address (required, must not be blank)
 * @property password User's password (required, must not be blank)
 */
@Schema(
    description = "Request payload for user authentication",
    example = """
        {
            "email": "user@example.com",
            "password": "SecurePassword123!"
        }
    """
)
data class LoginRequest(
    @field:Schema(
        description = "User's email address used for authentication",
        example = "user@example.com",
        required = true,
        minLength = 5,
        maxLength = 100,
        format = "email"
    )
    val email: String,

    @field:Schema(
        description = "User's password (minimum 8 characters recommended)",
        example = "SecurePassword123!",
        required = true,
        minLength = 1,
        format = "password"
    )
    val password: String
) {
    init {
        require(email.isNotBlank()) { "Email cannot be blank" }
        require(password.isNotBlank()) { "Password cannot be blank" }
    }
}