package com.ninjashadowboy.portfolio.controllers.docs

import io.swagger.v3.oas.annotations.tags.Tag

/**
 * Swagger documentation configuration for Ratings API.
 */
@Tag(
    name = "Ratings",
    description = """
        Project rating management endpoints.
        
        **Overview:**
        Ratings allow users to provide feedback on portfolio projects.
        Each rating includes a rating value (1-5 stars) and an optional comment.
        
        **Features:**
        - Users can rate each project once
        - Users can update their own ratings
        - Users can delete their own ratings
        - Get rating statistics per project
        - Get rating distribution (histogram)
        
        **Security:**
        - Creating ratings requires authentication
        - Users can only update/delete their own ratings
        - Admins can delete any rating
    """
)
interface RatingApiDocs
