package com.ninjashadowboy.portfolio.services.impl

import com.ninjashadowboy.portfolio.dtos.PhotoDto
import com.ninjashadowboy.portfolio.entities.Photo
import com.ninjashadowboy.portfolio.repositories.PhotoRepo
import com.ninjashadowboy.portfolio.repositories.ProjectRepo
import com.ninjashadowboy.portfolio.services.PhotoService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URI
import java.net.URISyntaxException

@Service
class PhotoServiceImpl(
    private val photoRepo: PhotoRepo,
    private val projectRepo: ProjectRepo
) : PhotoService {

    override fun deletePhotoFromDB(photo: Photo) {
        photoRepo.delete(photo)
    }

    override fun deletePhotoFromDB(photoId: Int) {
        val photo = photoRepo.findById(photoId.toLong()).orElse(null)
        if (null == photo) throw NoSuchElementException("No photo with id $photoId")
        deletePhotoFromDB(photo)
    }

    @Transactional
    override fun saveProjectPhoto(photoUrl: String, projectId: Int): PhotoDto {
        val normalizedUrl = normalizePhotoUrl(photoUrl)
        val project =
            projectRepo.findById(projectId.toLong()).orElseThrow { NoSuchElementException("Project not found") }

        val photo = photoRepo.save(
            Photo(
                project = project,
                photoUrl = normalizedUrl
            )
        )

        return toDto(photo)
    }

    @Transactional
    override fun saveProfilePhoto(photoUrl: String): PhotoDto {
        val normalizedUrl = normalizePhotoUrl(photoUrl)

        // Keep only the most recent profile photo record since Cloudinary hosts the asset
        val orphanedPhotos = photoRepo.findAllByProjectIsNull()
        if (orphanedPhotos.isNotEmpty()) {
            photoRepo.deleteAll(orphanedPhotos)
        }

        val photo = photoRepo.save(Photo(photoUrl = normalizedUrl))

        return toDto(photo)
    }

    private fun normalizePhotoUrl(photoUrl: String): String {
        val trimmed = photoUrl.trim()
        if (trimmed.isEmpty()) {
            throw IllegalArgumentException("Photo URL must not be blank")
        }

        try {
            val uri = URI(trimmed)
            if (uri.scheme.isNullOrBlank() || uri.host.isNullOrBlank()) {
                throw IllegalArgumentException("Photo URL must include a valid scheme and host")
            }
            if (uri.scheme!!.lowercase() !in setOf("http", "https")) {
                throw IllegalArgumentException("Photo URL must use HTTP or HTTPS")
            }
        } catch (ex: URISyntaxException) {
            throw IllegalArgumentException("Photo URL format is invalid", ex)
        }

        return trimmed
    }

    fun toDto(photo: Photo): PhotoDto {
        return PhotoDto(
            id = photo.id,
            photoUrl = photo.photoUrl,
            projectId = photo.project?.id ?: 0
        )
    }
}