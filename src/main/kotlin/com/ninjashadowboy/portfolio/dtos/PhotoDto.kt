package com.ninjashadowboy.portfolio.dtos

import io.swagger.v3.oas.annotations.media.Schema

/**
 * Data Transfer Object representing a photo entity.
 * 
 * Contains photo information including its URL and associated project.
 * Used in responses when retrieving or creating photos.
 * 
 * @property id Unique photo identifier
 * @property photoUrl URL or path to access the photo
 * @property projectId ID of the project this photo belongs to
 */
@Schema(
    description = "Photo information including URL and project association"
)
data class PhotoDto(
    @field:Schema(
        description = "Unique identifier for the photo",
        example = "1",
        required = true
    )
    val id: Long,
    
    @field:Schema(
        description = "Public URL (e.g., Cloudinary) to access the photo",
        example = "https://res.cloudinary.com/demo/image/upload/v1234567890/projects/5/photo-abc123.jpg",
        required = true
    )
    val photoUrl: String,
    
    @field:Schema(
        description = "ID of the project this photo belongs to",
        example = "5",
        required = true
    )
    val projectId: Long
)

/**
 * Data Transfer Object for creating a new photo.
 * 
 * Used when saving a Cloudinary photo URL to the database.
 * 
 * @property photoUrl Cloudinary URL where the photo is stored
 * @property projectId ID of the project to associate the photo with (optional for profile photos)
 */
@Schema(
    description = "Request payload for creating a new photo record"
)
data class PhotoCreateDto(
    @field:Schema(
        description = "Cloudinary URL where the photo is stored",
        example = "https://res.cloudinary.com/demo/image/upload/v1234567890/portfolio/projects/5/photo.jpg",
        required = true
    )
    val photoUrl: String,
    
    @field:Schema(
        description = "ID of the project to associate this photo with (not required for profile photos)",
        example = "5",
        required = false
    )
    val projectId: Long? = null
)