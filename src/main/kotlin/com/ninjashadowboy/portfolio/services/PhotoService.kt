package com.ninjashadowboy.portfolio.services

import com.ninjashadowboy.portfolio.dtos.PhotoDto
import com.ninjashadowboy.portfolio.entities.Photo
import org.springframework.web.multipart.MultipartFile

interface PhotoService {
    fun deletePhotoFromDBAndDisk(photoId: Int)
    fun deletePhotoFromDBAndDisk(photo: Photo)
    fun saveProjectPhoto(file: MultipartFile, projectId: Int): PhotoDto
    fun saveProfilePhoto(file: MultipartFile): PhotoDto
}