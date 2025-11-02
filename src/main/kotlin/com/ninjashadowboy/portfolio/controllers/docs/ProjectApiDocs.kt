package com.ninjashadowboy.portfolio.controllers.docs

import io.swagger.v3.oas.annotations.tags.Tag

/**
 * Swagger documentation configuration for Projects API.
 */
@Tag(
    name = "Projects",
    description = """
        Portfolio project management endpoints.
        
        **Overview:**
        Projects represent portfolio items showcasing development work, including descriptions, 
        technologies used, challenges, and learnings.
        
        **Features:**
        - Retrieve all projects or by ID
        - Create, update, and delete projects (ADMIN only)
        - View project ratings and statistics
        - Filter featured projects
        
        **Security:**
        - GET operations are public
        - POST/PUT/DELETE require ADMIN role
    """
)
interface ProjectApiDocs
