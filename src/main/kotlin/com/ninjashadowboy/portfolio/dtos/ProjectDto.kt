package com.ninjashadowboy.portfolio.dtos

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

/**
 * Data Transfer Object representing a complete project.
 * 
 * Contains all project information, including metadata, ratings, and associated photos.
 * Used in responses when retrieving projects.
 * 
 * @property id Unique project identifier
 * @property name Project name/title
 * @property description Detailed project description
 * @property technologies Set of technologies/tools used in the project
 * @property githubLink Optional link to the project's GitHub repository
 * @property challenges Optional description of challenges faced during development
 * @property whatILearned Optional description of lessons learned
 * @property featured Whether this project is featured on the portfolio
 * @property createdAt Project creation timestamp
 * @property updatedAt Last update timestamp
 * @property averageRating Average user rating (0-5 stars)
 * @property totalRatings Total number of ratings received
 * @property photoUrls List of photo URLs associated with this project
 */
@Schema(
    description = "Complete project information including metadata, ratings, and photos"
)
data class ProjectDto(
    @field:Schema(
        description = "Unique identifier for the project",
        example = "1",
        required = true
    )
    val id: Long?,
    
    @field:Schema(
        description = "Project name or title",
        example = "E-Commerce Platform",
        required = true
    )
    val name: String,
    
    @field:Schema(
        description = "Detailed description of the project, its purpose, and features",
        example = "A full-stack e-commerce platform with payment integration, inventory management, and user authentication",
        required = true
    )
    val description: String,
    
    @field:Schema(
        description = "Set of technologies, frameworks, and tools used in the project",
        example = "[\"Kotlin\", \"Spring Boot\", \"PostgreSQL\", \"React\", \"Docker\"]",
        required = true
    )
    val technologies: Set<String>,
    
    @field:Schema(
        description = "URL to the project's GitHub repository",
        example = "https://github.com/username/ecommerce-platform",
        required = false,
        nullable = true
    )
    val githubLink: String?,
    
    @field:Schema(
        description = "Description of technical or design challenges faced during development",
        example = "Implementing real-time inventory synchronization across distributed systems",
        required = false,
        nullable = true
    )
    val challenges: String?,
    
    @field:Schema(
        description = "Key lessons and skills learned from this project",
        example = "Gained experience with microservices architecture and event-driven design",
        required = false,
        nullable = true
    )
    val whatILearned: String?,
    
    @field:Schema(
        description = "Whether this project is featured/highlighted on the portfolio",
        example = "true",
        required = true,
        defaultValue = "false"
    )
    val featured: Boolean,
    
    @field:Schema(
        description = "Timestamp when the project was created",
        example = "2025-01-15T10:30:00",
        required = true,
        format = "date-time"
    )
    val createdAt: LocalDateTime,
    
    @field:Schema(
        description = "Timestamp when the project was last updated",
        example = "2025-10-20T15:45:00",
        required = true,
        format = "date-time"
    )
    val updatedAt: LocalDateTime,
    
    @field:Schema(
        description = "Average rating from users (0.0 to 5.0 stars)",
        example = "4.5",
        required = true,
        minimum = "0",
        maximum = "5"
    )
    val averageRating: Float,
    
    @field:Schema(
        description = "Total number of ratings this project has received",
        example = "42",
        required = true,
        minimum = "0"
    )
    val totalRatings: Int,
    
    @field:Schema(
        description = "List of Cloudinary (or CDN) URLs associated with this project",
        example = "[\"https://res.cloudinary.com/demo/image/upload/v123/project/screenshot1.jpg\", \"https://res.cloudinary.com/demo/image/upload/v123/project/screenshot2.jpg\"]",
        required = true
    )
    val photoUrls: List<String>,
    
    @field:Schema(
        description = "List of photo objects with IDs and URLs for this project",
        required = true
    )
    val photos: List<PhotoDto> = emptyList()
)

/**
 * Data Transfer Object for creating a new project.
 * 
 * Contains the minimum required information to create a project.
 * Optional fields can be added later through updates.
 * 
 * @property name Project name/title
 * @property description Project description
 * @property technologies Set of technologies used
 * @property githubLink Optional GitHub repository URL
 * @property challenges Optional challenges description
 * @property whatILearned Optional lessons learned
 * @property featured Whether to feature this project (default: false)
 */
@Schema(
    description = "Request payload for creating a new project"
)
data class ProjectCreateDto(
    @field:Schema(
        description = "Project name or title",
        example = "Machine Learning Chatbot",
        required = true,
        minLength = 3,
        maxLength = 200
    )
    val name: String,

    @field:Schema(
        description = "Detailed description of the project",
        example = "An AI-powered chatbot using natural language processing to answer customer queries",
        required = true,
        minLength = 10,
        maxLength = 5000
    )
    val description: String,

    @field:Schema(
        description = "Technologies and tools used (at least one required)",
        example = "[\"Python\", \"TensorFlow\", \"FastAPI\", \"Docker\"]",
        required = true,
    )
    val technologies: Set<String>,

    @field:Schema(
        description = "GitHub repository URL (optional)",
        example = "https://github.com/username/ml-chatbot",
        required = false,
        nullable = true
    )
    val githubLink: String? = null,

    @field:Schema(
        description = "Technical challenges faced (optional)",
        example = "Training the model with limited data and optimizing response time",
        required = false,
        nullable = true
    )
    val challenges: String? = null,

    @field:Schema(
        description = "Key learnings from the project (optional)",
        example = "Learned advanced NLP techniques and deployment strategies for ML models",
        required = false,
        nullable = true
    )
    val whatILearned: String? = null,

    @field:Schema(
        description = "Whether to feature this project on the portfolio homepage",
        example = "false",
        required = false,
        defaultValue = "false"
    )
    val featured: Boolean = false
)

/**
 * Data Transfer Object for updating an existing project.
 * 
 * All fields are optional, allowing partial updates.
 * Only provided (non-null) fields will be updated.
 * 
 * @property name Updated project name
 * @property description Updated description
 * @property technologies Updated technology set
 * @property githubLink Updated GitHub link
 * @property challenges Updated challenges description
 * @property whatILearned Updated lessons learned
 * @property featured Updated featured status
 */
@Schema(
    description = "Request payload for updating project information (all fields optional)"
)
data class ProjectUpdateDto(
    @field:Schema(
        description = "Updated project name",
        example = "Advanced ML Chatbot v2",
        required = false,
        nullable = true
    )
    val name: String? = null,
    
    @field:Schema(
        description = "Updated project description",
        example = "Enhanced version with multi-language support and context awareness",
        required = false,
        nullable = true
    )
    val description: String? = null,
    
    @field:Schema(
        description = "Updated set of technologies",
        example = "[\"Python\", \"PyTorch\", \"FastAPI\", \"Redis\", \"Docker\"]",
        required = false,
        nullable = true
    )
    val technologies: Set<String>? = null,
    
    @field:Schema(
        description = "Updated GitHub repository URL",
        example = "https://github.com/username/ml-chatbot-v2",
        required = false,
        nullable = true
    )
    val githubLink: String? = null,
    
    @field:Schema(
        description = "Updated challenges description",
        example = "Implementing multi-language support while maintaining performance",
        required = false,
        nullable = true
    )
    val challenges: String? = null,
    
    @field:Schema(
        description = "Updated lessons learned",
        example = "Mastered transformer models and production ML deployment",
        required = false,
        nullable = true
    )
    val whatILearned: String? = null,
    
    @field:Schema(
        description = "Updated featured status",
        example = "true",
        required = false,
        nullable = true
    )
    val featured: Boolean? = null
)