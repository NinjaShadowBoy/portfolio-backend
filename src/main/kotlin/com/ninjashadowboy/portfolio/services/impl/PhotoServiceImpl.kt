package com.ninjashadowboy.portfolio.services.impl

import com.ninjashadowboy.portfolio.dtos.PhotoDto
import com.ninjashadowboy.portfolio.entities.Photo
import com.ninjashadowboy.portfolio.repositories.PhotoRepo
import com.ninjashadowboy.portfolio.repositories.ProjectRepo
import com.ninjashadowboy.portfolio.services.PhotoService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Service
class PhotoServiceImpl(
    private val photoRepo: PhotoRepo,
    @Value("\${photo.upload.dir}") private val photoDir: String,
    @Value("\${profile-photo.upload.dir}") private val profilePhotoDir: String,
    private val projectRepo: ProjectRepo
) : PhotoService {

    override fun deletePhotoFromDBAndDisk(photo: Photo) {
        val projectRoot = Paths.get("").toAbsolutePath().toString()
        val file = Paths.get(projectRoot, photo.photoUrl).toFile()
        val isDeleted = if (file.exists()) {
            file.delete()
        } else {
            true
        }
        if (isDeleted) {
            photoRepo.delete(photo)
        } else {
            throw IOException("Failed to delete photo ${photo.photoUrl} from DB and disk")
        }
    }

    override fun deletePhotoFromDBAndDisk(photoId: Int) {
        val photo = photoRepo.findById(photoId.toLong()).orElse(null)
        if (null == photo) throw NoSuchElementException("No photo with id $photoId")
        deletePhotoFromDBAndDisk(photo)
    }

    override fun saveProjectPhoto(file: MultipartFile, projectId: Int): PhotoDto {
        val project =
            projectRepo.findById(projectId.toLong()).orElseThrow { NoSuchElementException("Project not found") }

        // Create placeholder photo first
        var photo = Photo(
            project = project, photoUrl = "",
        )
        photo = photoRepo.save(photo)

        val projectName = getTrimmedName(project.name)
        val extension = getFileExtension(file.originalFilename)
        val fileName = "${projectName}-${photo.id}.$extension"

        val filePath = Paths.get(Paths.get("").toAbsolutePath().toString(), photoDir, fileName)
        Files.createDirectories(filePath.parent)
        Files.copy(file.inputStream, filePath, StandardCopyOption.REPLACE_EXISTING)

        // Set the correct URL
        photo.photoUrl = "/$photoDir/$fileName"
        photo = photoRepo.save(photo)

        return toDto(photo)
    }


    override fun saveProfilePhoto(file: MultipartFile): PhotoDto {
        val projectRoot = Paths.get("").toAbsolutePath().toString()
        val file1 = Paths.get(projectRoot, profilePhotoDir).toFile()
        if (file1.exists()) {
            file1.deleteRecursively()
        } else {
            true
        }
        var photo = Photo()
        photo = photoRepo.save(photo)

        val extension = getFileExtension(file.originalFilename)
        val fileName = "profile-${photo.id}.$extension"

        val filePath = Paths.get(projectRoot, profilePhotoDir, fileName)
        Files.createDirectories(filePath.parent)
        Files.copy(file.inputStream, filePath, StandardCopyOption.REPLACE_EXISTING)

        photo.photoUrl = "/$profilePhotoDir/$fileName"
        photo = photoRepo.save(photo)
        return toDto(photo)
    }

    private fun getTrimmedName(name: String?): String {
        return name?.substring(0, minOf(20, name.length)) ?: ""
    }

    private fun getFileExtension(fileName: String?): String {
        return fileName?.substringAfterLast(".", "") ?: ""
    }

    fun toDto(photo: Photo): PhotoDto {
        return PhotoDto(
            id = photo.id, photoUrl = photo.photoUrl, projectId = photo.project?.id ?: 0
        )
    }

}