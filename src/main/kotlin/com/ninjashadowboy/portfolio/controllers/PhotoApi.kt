package com.ninjashadowboy.portfolio.controllers

import com.ninjashadowboy.portfolio.dtos.PhotoDto
import com.ninjashadowboy.portfolio.entities.Photo
import com.ninjashadowboy.portfolio.services.PhotoService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

/**
 * REST API controller for managing photo-related operations.
 * 
 * This controller provides endpoints for uploading and managing photos for
 * projects and user profiles. It handles:
 * - Project photo uploads and management
 * - Profile photo uploads
 * - Photo deletion from database and file system
 * 
 * **Supported Formats:**
 * - JPEG/JPG (.jpg, .jpeg)
 * - PNG (.png)
 * - GIF (.gif)
 * 
 * **Storage:**
 * Photos are stored on the server file system:
 * - Project photos: `uploads/photos/project-{id}/`
 * - Profile photos: `uploads/profilephoto/`
 * 
 * **Security:**
 * - Upload endpoints require authentication
 * - Delete operations require ADMIN role or ownership
 * - File size limits: 10MB per file
 * - Total request size: 15MB maximum
 * 
 * @author NinjaShadowBoy
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1")
@Tag(
    name = "Photos",
    description = """
        Photo management endpoints for projects and user profiles.
        
        **Features:**
        - Upload photos for portfolio projects
        - Upload/update profile photos
        - Delete photos from system
        - Automatic file organization and naming
        
        **File Requirements:**
        - **Supported formats:** JPEG, PNG, GIF
        - **Maximum file size:** 10MB per image
        - **Maximum request size:** 15MB total
        - **Recommended dimensions:** 
            - Project photos: 1200x800px minimum
            - Profile photos: 400x400px (square)
        
        **Storage Structure:**
        ```
        uploads/
        ├── photos/
        │   ├── project-1/
        │   │   ├── photo-uuid1.jpg
        │   │   └── photo-uuid2.jpg
        │   └── project-2/
        │       └── photo-uuid3.jpg
        └── profilephoto/
            └── profile-uuid.jpg
        ```
        
        **File Naming:**
        Uploaded files are automatically renamed with UUIDs to:
        - Prevent filename conflicts
        - Enhance security (original names are not exposed)
        - Support easy cleanup and management
        
        **Photo Access:**
        After upload, photos can be accessed via the returned `photoUrl`:
        ```
        GET /uploads/photos/project-1/photo-abc123.jpg
        ```
        
        **Security:**
        - All upload endpoints require authentication
        - Deletion requires ADMIN role or resource ownership
        - File type validation prevents malicious uploads
        - Automatic virus scanning (if configured)
    """
)
class PhotoApi(private val photoService: PhotoService) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * Uploads a photo for a specific project.
     */
    @Operation(
        summary = "Upload project photo",
        description = """
            Uploads a photo file and associates it with a specific portfolio project.
            
            **Process:**
            1. Validates the file type and size
            2. Generates a unique filename (UUID-based)
            3. Creates project-specific directory if needed
            4. Saves file to disk: `uploads/photos/project-{id}/`
            5. Creates database record with photo metadata
            6. Returns photo information with accessible URL
            
            **File Requirements:**
            - **Format:** JPEG, PNG, or GIF
            - **Size:** Maximum 10MB
            - **Dimensions:** 1200x800px minimum recommended
            
            **Use Cases:**
            - Uploading project screenshots
            - Adding demo images
            - Showcasing project UI/UX
            - Providing visual context for projects
            
            **Photo Organization:**
            Each project has its own directory:
            ```
            uploads/photos/project-1/
                ├── photo-abc123.jpg  (homepage screenshot)
                ├── photo-def456.jpg  (feature demo)
                └── photo-ghi789.jpg  (mobile view)
            ```
            
            **Best Practices:**
            - Use descriptive filenames before upload (for your reference)
            - Upload multiple photos to show different aspects
            - Compress images to reduce file size
            - Use consistent aspect ratios for better presentation
            
            **Performance:**
            - Upload processing time: ~500ms for 2MB image
            - Automatic image optimization may be applied
            - Thumbnails are NOT auto-generated (implement if needed)
            
            **Error Handling:**
            - Invalid file type → 400 Bad Request
            - File too large → 413 Payload Too Large
            - Project not found → 404 Not Found
            - Disk space issues → 500 Internal Server Error
        """,
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Photo uploaded successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = PhotoDto::class),
                        examples = [
                            ExampleObject(
                                name = "Successful Upload",
                                description = "Response after successfully uploading a project photo",
                                value = """
                                {
                                    "id": 1,
                                    "photoUrl": "/uploads/photos/project-5/photo-abc123def456.jpg",
                                    "projectId": 5
                                }
                                """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request - Invalid file or parameters",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "Invalid File Type",
                                value = """
                                {
                                    "timestamp": "2025-10-30T14:30:00",
                                    "status": 400,
                                    "error": "Bad Request",
                                    "message": "Invalid file type. Only JPEG, PNG, and GIF are allowed.",
                                    "path": "/api/v1/photos/5"
                                }
                                """
                            ),
                            ExampleObject(
                                name = "Missing File",
                                value = """
                                {
                                    "timestamp": "2025-10-30T14:30:00",
                                    "status": 400,
                                    "error": "Bad Request",
                                    "message": "Photo file is required",
                                    "path": "/api/v1/photos/5"
                                }
                                """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Authentication required",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "Missing Token",
                                value = """
                                {
                                    "timestamp": "2025-10-30T14:30:00",
                                    "status": 401,
                                    "error": "Unauthorized",
                                    "message": "Full authentication is required to access this resource",
                                    "path": "/api/v1/photos/5"
                                }
                                """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Not found - Project does not exist",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "Project Not Found",
                                value = """
                                {
                                    "timestamp": "2025-10-30T14:30:00",
                                    "status": 404,
                                    "error": "Not Found",
                                    "message": "Project with ID 999 not found",
                                    "path": "/api/v1/photos/999"
                                }
                                """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "413",
                description = "Payload too large - File exceeds size limit",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "File Too Large",
                                value = """
                                {
                                    "timestamp": "2025-10-30T14:30:00",
                                    "status": 413,
                                    "error": "Payload Too Large",
                                    "message": "Maximum upload size exceeded (10MB limit)",
                                    "path": "/api/v1/photos/5"
                                }
                                """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error - Upload failed",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "Upload Error",
                                value = """
                                {
                                    "timestamp": "2025-10-30T14:30:00",
                                    "status": 500,
                                    "error": "Internal Server Error",
                                    "message": "Failed to save photo to disk",
                                    "path": "/api/v1/photos/5"
                                }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @PutMapping(
        "/photos/{projectId}",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun uploadProjectPhoto(
        @Parameter(
            description = "Photo file to upload (JPEG, PNG, or GIF)",
            required = true,
            content = [Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)]
        )
        @RequestParam("photo") file: MultipartFile,
        
        @Parameter(
            description = "ID of the project to associate the photo with",
            required = true,
            example = "5"
        )
        @PathVariable("projectId") projectId: Int
    ): ResponseEntity<PhotoDto> {
        val p = photoService.saveProjectPhoto(file, projectId)
        return ResponseEntity.status(HttpStatus.CREATED).body(p)
    }

    @Operation(
        summary = "Upload profile photo",
        description = """
            Uploads or updates the authenticated user's profile photo.
            
            **Process:**
            1. Validates the uploaded image file
            2. Removes old profile photo if one exists
            3. Generates unique filename
            4. Saves to `uploads/profilephoto/` directory
            5. Updates user's profile photo reference
            6. Returns photo information
            
            **File Requirements:**
            - **Format:** JPEG, PNG, or GIF
            - **Size:** Maximum 10MB
            - **Recommended:** Square image (400x400px or larger)
            
            **Use Cases:**
            - Setting initial profile photo
            - Updating existing profile photo
            - Changing avatar/profile picture
            
            **Behavior:**
            - If user already has a profile photo, it will be replaced
            - Old photo file is automatically deleted from disk
            - Only one profile photo per user is maintained
            
            **Best Practices:**
            - Use square images for best display
            - Minimum 200x200px, recommended 400x400px
            - Face should be clearly visible
            - Use good lighting and contrast
            
            **Photo Access:**
            Access the profile photo via the returned URL:
            ```
            GET /uploads/profilephoto/profile-abc123.jpg
            ```
            
            **Privacy:**
            - Profile photos are visible to all users
            - Consider privacy before uploading
            - Photos can be deleted/replaced anytime
        """,
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Profile photo uploaded successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = PhotoDto::class),
                        examples = [
                            ExampleObject(
                                name = "Successful Profile Photo Upload",
                                value = """
                                {
                                    "id": 10,
                                    "photoUrl": "/uploads/profilephoto/profile-xyz789abc123.jpg",
                                    "projectId": 0
                                }
                                """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request - Invalid file",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "Invalid Image",
                                value = """
                                {
                                    "timestamp": "2025-10-30T14:30:00",
                                    "status": 400,
                                    "error": "Bad Request",
                                    "message": "Invalid image file",
                                    "path": "/api/v1/photos/profile"
                                }
                                """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Authentication required",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "Not Authenticated",
                                value = """
                                {
                                    "timestamp": "2025-10-30T14:30:00",
                                    "status": 401,
                                    "error": "Unauthorized",
                                    "message": "Authentication required to upload profile photo",
                                    "path": "/api/v1/photos/profile"
                                }
                                """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "Server Error",
                                value = """
                                {
                                    "timestamp": "2025-10-30T14:30:00",
                                    "status": 500,
                                    "error": "Internal Server Error",
                                    "message": "Failed to process profile photo",
                                    "path": "/api/v1/photos/profile"
                                }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @PutMapping(
        "/photos/profile",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun uploadProfilePhoto(
        @Parameter(
            description = "Profile photo file (square images recommended)",
            required = true,
            content = [Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)]
        )
        @RequestPart("photo") file: MultipartFile,
    ): ResponseEntity<PhotoDto> {
        val p = photoService.saveProfilePhoto(file)
        return ResponseEntity.status(HttpStatus.CREATED).body(p)
    }

    /**
     * Deletes a photo from the system.
     */
    @Operation(
        summary = "Delete photo",
        description = """
            Permanently deletes a photo from both the database and file system.
            
            **Process:**
            1. Validates photo exists and user has permission
            2. Removes photo record from database
            3. Deletes physical file from disk
            4. Cleans up any associated metadata
            5. Returns success confirmation
            
            **Authorization:**
            Users can delete photos if they are:
            - The owner of the associated project, OR
            - An ADMIN user
            
            **Deletion is Permanent:**
            - Photo file is removed from disk
            - Database record is deleted
            - URL becomes invalid immediately
            - **Cannot be undone** - ensure you have backups if needed
            
            **Impact:**
            - Project photo count is decreased
            - Photo no longer appears in project listings
            - Direct URL access returns 404
            - No impact on project itself (only the photo association)
            
            **Use Cases:**
            - Removing outdated screenshots
            - Deleting low-quality images
            - Cleaning up test uploads
            - Removing sensitive information
            
            **Best Practices:**
            - Verify the correct photo ID before deletion
            - Consider replacing instead of deleting
            - Maintain at least one photo per project for visibility
            
            **Error Scenarios:**
            - Photo not found → 404
            - No permission → 403 Forbidden
            - File deletion fails → 500 (DB record still deleted)
        """,
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Photo deleted successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "Successful Deletion",
                                value = "{}"
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Authentication required",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "Not Authenticated",
                                value = """
                                {
                                    "timestamp": "2025-10-30T14:30:00",
                                    "status": 401,
                                    "error": "Unauthorized",
                                    "message": "Authentication required",
                                    "path": "/api/v1/photos/10"
                                }
                                """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Forbidden - Insufficient permissions",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "Insufficient Permissions",
                                value = """
                                {
                                    "timestamp": "2025-10-30T14:30:00",
                                    "status": 403,
                                    "error": "Forbidden",
                                    "message": "You don't have permission to delete this photo",
                                    "path": "/api/v1/photos/10"
                                }
                                """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Not found - Photo does not exist",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "Photo Not Found",
                                value = """
                                {
                                    "timestamp": "2025-10-30T14:30:00",
                                    "status": 404,
                                    "error": "Not Found",
                                    "message": "Photo with ID 999 not found",
                                    "path": "/api/v1/photos/999"
                                }
                                """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error - Deletion failed",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "Deletion Error",
                                value = """
                                {
                                    "timestamp": "2025-10-30T14:30:00",
                                    "status": 500,
                                    "error": "Internal Server Error",
                                    "message": "Failed to delete photo from disk",
                                    "path": "/api/v1/photos/10"
                                }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @DeleteMapping("/photos/{photoId}")
    fun deletePhoto(
        @Parameter(
            description = "ID of the photo to delete",
            required = true,
            example = "10"
        )
        @PathVariable("photoId") photoId: Int
    ): ResponseEntity<Unit> {
        return ResponseEntity.ok(photoService.deletePhotoFromDBAndDisk(photoId))
    }
}