package com.ninjashadowboy.portfolio.services

import com.ninjashadowboy.portfolio.dtos.PhotoDto
import com.ninjashadowboy.portfolio.entities.Photo

interface PhotoService {
    fun deletePhotoFromDB(photoId: Int)
    fun deletePhotoFromDB(photo: Photo)
    fun saveProjectPhoto(photoUrl: String, projectId: Int): PhotoDto
    fun saveProfilePhoto(photoUrl: String): PhotoDto
}