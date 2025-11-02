package com.ninjashadowboy.portfolio.controllers.api

import com.ninjashadowboy.portfolio.controllers.docs.PhotoApiDocs
import com.ninjashadowboy.portfolio.dtos.PhotoDto
import com.ninjashadowboy.portfolio.services.PhotoService
import com.ninjashadowboy.portfolio.utils.FileUtils
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
import org.springframework.web.multipart.MultipartFile

/**
 * REST API controller for managing photo operations.
 */
@RestController
@RequestMapping("/api/v1/photos")
class PhotoController(
    private val photoService: PhotoService
) : BaseController(), PhotoApiDocs {

    @Operation(
        summary = "Upload project photo",
        description = "Uploads a photo file and associates it with a specific portfolio project.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Photo uploaded successfully",
                content = [Content(schema = Schema(implementation = PhotoDto::class))]
            ),
            ApiResponse(responseCode = "400", description = "Invalid file or parameters"),
            ApiResponse(responseCode = "401", description = "Unauthorized"),
            ApiResponse(responseCode = "404", description = "Project not found"),
            ApiResponse(responseCode = "413", description = "File too large")
        ]
    )
    @PutMapping(
        "/{projectId}",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun uploadProjectPhoto(
        @Parameter(description = "Photo file to upload (JPEG, PNG, or GIF)", required = true)
        @RequestParam("photo") file: MultipartFile,
        
        @Parameter(description = "ID of the project to associate the photo with", required = true)
        @PathVariable("projectId") projectId: Int
    ): ResponseEntity<PhotoDto> {
        logRequest("uploadProjectPhoto", mapOf("projectId" to projectId, "filename" to (file.originalFilename ?: "unknown")))
        
        FileUtils.validateImageFile(file)
        val photo = photoService.saveProjectPhoto(file, projectId)
        
        logResponse("uploadProjectPhoto", "Photo uploaded with ID: ${photo.id}")
        return ResponseUtils.created(photo)
    }

    @Operation(
        summary = "Upload profile photo",
        description = "Uploads or updates the authenticated user's profile photo.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Profile photo uploaded successfully",
                content = [Content(schema = Schema(implementation = PhotoDto::class))]
            ),
            ApiResponse(responseCode = "400", description = "Invalid file"),
            ApiResponse(responseCode = "401", description = "Unauthorized")
        ]
    )
    @PutMapping(
        "/profile",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun uploadProfilePhoto(
        @Parameter(description = "Profile photo file (square images recommended)", required = true)
        @RequestPart("photo") file: MultipartFile
    ): ResponseEntity<PhotoDto> {
        logRequest("uploadProfilePhoto", mapOf("filename" to (file.originalFilename ?: "unknown")))
        
        FileUtils.validateImageFile(file)
        val photo = photoService.saveProfilePhoto(file)
        
        logResponse("uploadProfilePhoto", "Profile photo uploaded with ID: ${photo.id}")
        return ResponseUtils.created(photo)
    }

    @Operation(
        summary = "Delete photo",
        description = "Permanently deletes a photo from both the database and file system.",
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
        
        photoService.deletePhotoFromDBAndDisk(photoId)
        
        logResponse("deletePhoto")
        return ResponseUtils.noContent()
    }
}
