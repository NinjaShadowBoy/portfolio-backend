package com.ninjashadowboy.portfolio.repositories

import com.ninjashadowboy.portfolio.entities.Photo
import com.ninjashadowboy.portfolio.entities.Project
import org.springframework.data.jpa.repository.JpaRepository

interface PhotoRepo : JpaRepository<Photo, Long> {
    fun findPhotosByProject(project: Project): List<Photo>
    fun findAllByProjectIsNull(): List<Photo>
}