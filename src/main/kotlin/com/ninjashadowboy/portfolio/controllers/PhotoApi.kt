package com.ninjashadowboy.portfolio.controllers

import com.ninjashadowboy.portfolio.dtos.PhotoDto
import com.ninjashadowboy.portfolio.entities.Photo
import com.ninjashadowboy.portfolio.services.PhotoService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

/**
 * REST API controller for managing photo-related operations.
 * This controller provides endpoints for uploading and managing photos for
 * hostels and rooms.
 *
 * <p>
 * This controller handles all photo-related operations, including uploading
 * photos for hostels and room types, as well as deleting photos from the
 * system.
 * It works with the PhotoService to manage file storage and database
 * operations.
 * </p>
 *
 * @author NinjaShadowBoy
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1")
class PhotoApi(private val photoService: PhotoService) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * Uploads a photo for a specific hostel.
     *
     * <p>
     * This endpoint allows uploading a photo file and associating it with
     * a specific hostel. The photo will be stored on the server, and its
     * metadata will be saved in the database.
     * </p>
     *
     * <p>
     * <strong>Supported file formats:</strong> Common image formats (JPEG, PNG,
     * GIF)
     * </p>
     *
     * <p>
     * <strong>File storage:</strong> Photos are stored in the
     * uploads/hostel-photos/
     * directory with an organized folder structure.
     * </p>
     *
     * @param file               The photo file to be uploaded
     * @param projectId            The ID of the project to associate the photo with
     *
     * @return ResponseEntity containing:
     *         <ul>
     *         <li>201 CREATED with a Photo object on successful upload</li>
     *         <li>500 Internal Server Error with an error message if upload fails</li>
     *         </ul>
     * @throws IOException if there's an error during file processing
     *
     * @see Photo
     */
    @PutMapping("/photos/{projectId}")
    fun uploadProjectPhoto(
        @RequestParam("photo") file: MultipartFile,
        @PathVariable("projectId") projectId: Int
    ): ResponseEntity<PhotoDto> {
        val p = photoService.saveProjectPhoto(file, projectId)
        return ResponseEntity.status(HttpStatus.CREATED).body(p)
    }

    @PutMapping("/photos/profile")
    fun uploadProfilePhoto(
        @RequestPart("photo") file: MultipartFile,
    ): ResponseEntity<PhotoDto> {
        val p = photoService.saveProfilePhoto(file)
        return ResponseEntity.status(HttpStatus.CREATED).body(p)
    }

    /**
     * Deletes a photo from the system.
     *
     * <p>
     * This endpoint permanently removes a photo from both the database
     * and the file system. The operation is irreversible.
     * </p>
     *
     * <p>
     * <strong>Deletion process:</strong>
     * </p>
     * <ul>
     * <li>Removes the photo record from the database</li>
     * <li>Deletes the physical file from the server</li>
     * <li>Cleans up any associated metadata</li>
     * </ul>
     *
     * @param photoId The ID of the photo to be deleted
     * @return ResponseEntity containing:
     *         <ul>
     *         <li>200 OK with a success message on successful deletion</li>
     *         <li>404 Not Found if the photo doesn't exist</li>
     *         <li>500 Internal Server Error with an error message if deletion
     *         fails</li>
     *         </ul>
     *
     * @see Photo
     */
    @DeleteMapping("/photos/{photoId}")
    fun deletePhoto(@PathVariable("photoId") photoId: Int): ResponseEntity<Unit> {
        return ResponseEntity.ok(photoService.deletePhotoFromDBAndDisk(photoId))
    }
}