package com.ninjashadowboy.portfolio.controllers.docs

import io.swagger.v3.oas.annotations.tags.Tag

/**
 * Swagger documentation configuration for Photos API.
 */
@Tag(
    name = "Photos",
    description = """
        Photo management endpoints for projects and user profiles.
        
        **How it works now:**
        - The frontend uploads images to Cloudinary.
        - Cloudinary returns a secure URL.
        - The frontend forwards that URL to these endpoints so the backend only stores metadata.
        
        **Features:**
        - Persist Cloudinary URLs for project galleries
        - Update the profile photo reference without handling binary files
        - Delete photo metadata when no longer needed (the actual asset remains on Cloudinary)
        
        **Security:**
        - All photo write endpoints require authentication
        - Delete operations require ADMIN role or ownership
    """
)
interface PhotoApiDocs
