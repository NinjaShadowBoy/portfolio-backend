package com.ninjashadowboy.portfolio.controllers.api

import com.ninjashadowboy.portfolio.controllers.docs.PhotoApiDocs
import com.ninjashadowboy.portfolio.dtos.PhotoDto
import com.ninjashadowboy.portfolio.dtos.PhotoCreateDto
import com.ninjashadowboy.portfolio.services.PhotoService
import com.ninjashadowboy.portfolio.utils.ResponseUtils
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST API controller for managing photo operations.
 * Photos are stored in Cloudinary and URLs are persisted in the database.
 */
@RestController
@RequestMapping("/api/v1/photos")
class PhotoController(
    private val photoService: PhotoService
) : BaseController(), PhotoApiDocs {

    @Operation(
        summary = "Save project photo URL",
        description = "Saves a photo URL (from Cloudinary) and associates it with a specific portfolio project.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Photo URL saved successfully",
                content = [Content(schema = Schema(implementation = PhotoDto::class))]
            ),
            ApiResponse(responseCode = "400", description = "Invalid URL or parameters"),
            ApiResponse(responseCode = "401", description = "Unauthorized"),
            ApiResponse(responseCode = "404", description = "Project not found")
        ]
    )
    @PostMapping(
        "/{projectId}",
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun saveProjectPhoto(
        @Parameter(description = "Photo data containing Cloudinary URL", required = true)
        @RequestBody photoRequest: PhotoCreateDto,
        
        @Parameter(description = "ID of the project to associate the photo with", required = true)
        @PathVariable("projectId") projectId: Int
    ): ResponseEntity<PhotoDto> {
        logRequest("saveProjectPhoto", mapOf("projectId" to projectId, "photoUrl" to photoRequest.photoUrl))
        
        val photo = photoService.saveProjectPhoto(photoRequest.photoUrl, projectId)
        
        logResponse("saveProjectPhoto", "Photo saved with ID: ${photo.id}")
        return ResponseUtils.created(photo)
    }

    @Operation(
        summary = "Save profile photo URL",
        description = "Saves or updates the authenticated user's profile photo URL (from Cloudinary).",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Profile photo URL saved successfully",
                content = [Content(schema = Schema(implementation = PhotoDto::class))]
            ),
            ApiResponse(responseCode = "400", description = "Invalid URL"),
            ApiResponse(responseCode = "401", description = "Unauthorized")
        ]
    )
    @PostMapping(
        "/profile",
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun saveProfilePhoto(
        @Parameter(description = "Profile photo URL from Cloudinary", required = true)
        @RequestBody photoRequest: PhotoCreateDto
    ): ResponseEntity<PhotoDto> {
        logRequest("saveProfilePhoto", mapOf("photoUrl" to photoRequest.photoUrl))
        
        val photo = photoService.saveProfilePhoto(photoRequest.photoUrl)
        
        logResponse("saveProfilePhoto", "Profile photo saved with ID: ${photo.id}")
        return ResponseUtils.created(photo)
    }

    @Operation(
        summary = "Delete photo",
        description = "Permanently deletes a photo record from the database. Note: The actual image remains in Cloudinary.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Photo deleted successfully"),
            ApiResponse(responseCode = "401", description = "Unauthorized"),
            ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
            ApiResponse(responseCode = "404", description = "Photo not found")
        ]
    )
    @DeleteMapping("/{photoId}")
    fun deletePhoto(
        @Parameter(description = "ID of the photo to delete", required = true)
        @PathVariable("photoId") photoId: Int
    ): ResponseEntity<Unit> {
        logRequest("deletePhoto", mapOf("photoId" to photoId))
        
        photoService.deletePhotoFromDB(photoId)
        
        logResponse("deletePhoto")
        return ResponseUtils.noContent()
    }
}
