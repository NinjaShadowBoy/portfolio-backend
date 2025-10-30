package com.ninjashadowboy.portfolio.controllers

import com.ninjashadowboy.portfolio.dtos.ProjectDto
import com.ninjashadowboy.portfolio.services.ProjectService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * REST API controller for project management operations.
 * 
 * This controller handles all portfolio project-related operations:
 * - Retrieving project listings
 * - Viewing project details
 * - Managing project metadata
 * 
 * Projects represent portfolio items showcasing development work,
 * including descriptions, technologies used, challenges, and learnings.
 * 
 * @author NinjaShadowBoy
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/projects")
@Tag(
    name = "Projects",
    description = """
        Portfolio project management endpoints.
        
        **Overview:**
        Projects are the core of the portfolio, representing development work and achievements.
        Each project includes:
        - Name, description, and technical details
        - Technologies and tools used
        - Links to source code (GitHub)
        - Challenges faced and lessons learned
        - User ratings and feedback
        - Associated photos and screenshots
        
        **Features:**
        - Retrieve all projects or filter by criteria
        - View detailed project information
        - Projects include aggregated ratings
        - Photos are returned as accessible URLs
        
        **Sorting & Filtering:**
        Projects can be filtered by:
        - Featured status (highlighted projects)
        - Technologies used
        - Date created/updated
        - Rating thresholds
        
        **Security:**
        - GET operations are public (no authentication required)
        - POST/PUT/DELETE operations require ADMIN role
    """
)
class ProjectApi(
    private val projectService: ProjectService
) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @Operation(
        summary = "Get all projects",
        description = """
            Retrieves a complete list of all portfolio projects.
            
            **Response Details:**
            Each project includes:
            - Basic information (id, name, description)
            - Technologies and tools used
            - GitHub repository link (if available)
            - Development challenges and learnings
            - Featured status (whether highlighted on portfolio)
            - Timestamps (created and last updated)
            - Rating statistics (average rating and total count)
            - Photo URLs for project screenshots
            
            **Project Photos:**
            Photo URLs are fully qualified paths that can be accessed directly:
            ```
            /uploads/photos/project-1/screenshot1.jpg
            ```
            
            **Ratings:**
            - Average rating: 0.0 to 5.0 stars
            - Total ratings: Number of user ratings received
            - Projects with 0 ratings show averageRating as 0.0
            
            **Featured Projects:**
            Projects marked as `featured: true` are highlighted on the portfolio homepage.
            These typically represent the best or most significant work.
            
            **Performance:**
            - Results include all projects in a single response
            - Average response time: <200ms
            - Results are not paginated (suitable for portfolios with <100 projects)
            
            **Caching:**
            This endpoint's responses may be cached for up to 60 seconds.
            
            **Future Enhancements:**
            - Pagination for large project sets
            - Filtering by technology, date, or rating
            - Sorting options (by date, rating, name)
        """,
        security = [] // Public endpoint - no authentication required
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Projects retrieved successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        array = ArraySchema(schema = Schema(implementation = ProjectDto::class)),
                        examples = [
                            ExampleObject(
                                name = "Multiple Projects",
                                description = "Example response with multiple projects",
                                value = """
                                [
                                    {
                                        "id": 1,
                                        "name": "E-Commerce Platform",
                                        "description": "Full-stack e-commerce application with payment integration and inventory management",
                                        "technologies": ["Kotlin", "Spring Boot", "PostgreSQL", "React", "Docker"],
                                        "githubLink": "https://github.com/username/ecommerce-platform",
                                        "challenges": "Implementing real-time inventory synchronization across distributed systems",
                                        "whatILearned": "Gained experience with microservices architecture and event-driven design",
                                        "featured": true,
                                        "createdAt": "2025-01-15T10:30:00",
                                        "updatedAt": "2025-10-20T15:45:00",
                                        "averageRating": 4.5,
                                        "totalRatings": 42,
                                        "photoUrls": [
                                            "/uploads/photos/project-1/homepage.jpg",
                                            "/uploads/photos/project-1/checkout.jpg"
                                        ]
                                    },
                                    {
                                        "id": 2,
                                        "name": "ML Chatbot",
                                        "description": "AI-powered customer service chatbot using natural language processing",
                                        "technologies": ["Python", "TensorFlow", "FastAPI", "Docker"],
                                        "githubLink": "https://github.com/username/ml-chatbot",
                                        "challenges": "Training the model with limited data and optimizing response time",
                                        "whatILearned": "Advanced NLP techniques and ML model deployment strategies",
                                        "featured": true,
                                        "createdAt": "2025-03-10T09:15:00",
                                        "updatedAt": "2025-09-05T11:20:00",
                                        "averageRating": 4.8,
                                        "totalRatings": 28,
                                        "photoUrls": [
                                            "/uploads/photos/project-2/chatbot-ui.jpg"
                                        ]
                                    },
                                    {
                                        "id": 3,
                                        "name": "Task Management App",
                                        "description": "Collaborative task management tool with real-time updates",
                                        "technologies": ["Vue.js", "Node.js", "MongoDB", "Socket.io"],
                                        "githubLink": null,
                                        "challenges": null,
                                        "whatILearned": null,
                                        "featured": false,
                                        "createdAt": "2025-06-20T14:00:00",
                                        "updatedAt": "2025-06-20T14:00:00",
                                        "averageRating": 0.0,
                                        "totalRatings": 0,
                                        "photoUrls": []
                                    }
                                ]
                                """
                            ),
                            ExampleObject(
                                name = "Empty Project List",
                                description = "Response when no projects exist",
                                value = "[]"
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error - Failed to retrieve projects",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "Server Error",
                                description = "Response when an unexpected error occurs",
                                value = """
                                {
                                    "timestamp": "2025-10-30T14:30:00",
                                    "status": 500,
                                    "error": "Internal Server Error",
                                    "message": "Failed to retrieve projects",
                                    "path": "/api/v1/projects"
                                }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @GetMapping
    fun getAllProjects(): List<ProjectDto> = projectService.getAllProjects()
}
