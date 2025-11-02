package com.ninjashadowboy.portfolio.controllers.docs

import io.swagger.v3.oas.annotations.tags.Tag

/**
 * Swagger documentation configuration for Photos API.
 */
@Tag(
    name = "Photos",
    description = """
        Photo management endpoints for projects and user profiles.
        
        **Features:**
        - Upload photos for portfolio projects
        - Upload/update profile photos
        - Delete photos from system
        
        **File Requirements:**
        - **Supported formats:** JPEG, PNG, GIF
        - **Maximum file size:** 10MB per image
        - **Maximum request size:** 15MB total
        
        **Security:**
        - All upload endpoints require authentication
        - Delete operations require ADMIN role or ownership
    """
)
interface PhotoApiDocs
