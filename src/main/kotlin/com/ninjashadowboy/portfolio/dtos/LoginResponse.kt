package com.ninjashadowboy.portfolio.dtos

import io.swagger.v3.oas.annotations.media.Schema

/**
 * Data Transfer Object for successful login responses.
 * 
 * Contains the JWT authentication token, user information, and token expiration time.
 * This response is returned after successful user authentication.
 * 
 * @property token JWT authentication token to be used for subsequent API requests
 * @property user Authenticated user's information
 * @property expiresIn Token expiration time in milliseconds
 */
@Schema(
    description = "Response payload returned after successful authentication"
)
data class LoginResponse(
    @field:Schema(
        description = "JWT Bearer token for authenticating subsequent requests",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
        required = true
    )
    val token: String,
    
    @field:Schema(
        description = "Authenticated user's profile information",
        required = true
    )
    val user: UserDto,
    
    @field:Schema(
        description = "Token expiration time in milliseconds (typically 24 hours = 86400000ms)",
        example = "86400000",
        required = true
    )
    val expiresIn: Long
)

/**
 * Generic authentication response for operations like registration.
 * 
 * @property message Status or error message
 * @property user Optional user information (included on success)
 */
@Schema(
    description = "Generic authentication response with optional user data"
)
data class AuthResponse(
    @field:Schema(
        description = "Response message describing the result of the operation",
        example = "User registered successfully",
        required = true
    )
    val message: String,
    
    @field:Schema(
        description = "User information (only included on successful operations)",
        required = false,
        nullable = true
    )
    val user: UserDto? = null
)