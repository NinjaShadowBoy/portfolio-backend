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
    @Schema(
        description = "Unique identifier for the photo",
        example = "1",
        required = true
    )
    val id: Long,
    
    @Schema(
        description = "URL or file path to access the photo",
        example = "/uploads/photos/project-1/photo-abc123.jpg",
        required = true
    )
    val photoUrl: String,
    
    @Schema(
        description = "ID of the project this photo belongs to",
        example = "5",
        required = true
    )
    val projectId: Long
)

/**
 * Data Transfer Object for creating a new photo.
 * 
 * Used when associating an uploaded photo with a project.
 * 
 * @property photoUrl URL or path where the photo is stored
 * @property projectId ID of the project to associate the photo with
 */
@Schema(
    description = "Request payload for creating a new photo record"
)
data class PhotoCreateDto(
    @Schema(
        description = "URL or file path where the photo is stored",
        example = "/uploads/photos/project-1/photo-xyz789.jpg",
        required = true
    )
    val photoUrl: String,
    
    @Schema(
        description = "ID of the project to associate this photo with",
        example = "5",
        required = true
    )
    val projectId: Long
)