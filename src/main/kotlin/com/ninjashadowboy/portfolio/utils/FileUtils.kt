package com.ninjashadowboy.portfolio.utils

import org.springframework.web.multipart.MultipartFile

/**
 * Utility class for file operations.
 */
object FileUtils {
    
    private val ALLOWED_IMAGE_EXTENSIONS = setOf("jpg", "jpeg", "png", "gif")
    private const val MAX_FILE_SIZE = 10 * 1024 * 1024 // 10MB
    
    /**
     * Validates that a file is an image and within size limits.
     * 
     * @throws com.ninjashadowboy.portfolio.exceptions.ValidationException if validation fails
     */
    fun validateImageFile(file: MultipartFile) {
        if (file.isEmpty) {
            throw com.ninjashadowboy.portfolio.exceptions.ValidationException(
                "File cannot be empty"
            )
        }
        
        val originalFilename = file.originalFilename
            ?: throw com.ninjashadowboy.portfolio.exceptions.ValidationException(
                "Filename cannot be null"
            )
        
        val extension = getFileExtension(originalFilename)
        if (extension !in ALLOWED_IMAGE_EXTENSIONS) {
            throw com.ninjashadowboy.portfolio.exceptions.ValidationException(
                "Invalid file type. Allowed types: ${ALLOWED_IMAGE_EXTENSIONS.joinToString(", ")}"
            )
        }
        
        if (file.size > MAX_FILE_SIZE) {
            throw com.ninjashadowboy.portfolio.exceptions.ValidationException(
                "File size exceeds maximum allowed size of ${MAX_FILE_SIZE / (1024 * 1024)}MB"
            )
        }
    }
    
    /**
     * Extracts file extension from filename.
     */
    fun getFileExtension(filename: String): String {
        return filename.substringAfterLast('.', "").lowercase()
    }
    
    /**
     * Checks if file is an image based on extension.
     */
    fun isImageFile(filename: String): Boolean {
        val extension = getFileExtension(filename)
        return extension in ALLOWED_IMAGE_EXTENSIONS
    }
}
