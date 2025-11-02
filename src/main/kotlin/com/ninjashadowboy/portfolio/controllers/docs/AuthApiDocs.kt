package com.ninjashadowboy.portfolio.controllers.docs

import io.swagger.v3.oas.annotations.tags.Tag

/**
 * Swagger documentation configuration for Authentication API.
 */
@Tag(
    name = "Authentication",
    description = """
        Authentication and user management endpoints.
        
        **Features:**
        - User login with JWT token generation
        - New user registration
        - Secure password handling
        - Token-based authentication
        
        **Security:**
        - Passwords are hashed using BCrypt
        - JWT tokens expire after 24 hours
        
        **Note:** These endpoints do not require authentication (they are publicly accessible).
    """
)
interface AuthApiDocs
