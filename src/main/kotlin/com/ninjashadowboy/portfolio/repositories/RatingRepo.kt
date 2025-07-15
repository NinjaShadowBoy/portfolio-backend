package com.ninjashadowboy.portfolio.repositories

import com.ninjashadowboy.portfolio.entities.Project
import com.ninjashadowboy.portfolio.entities.Rating
import org.springframework.data.jpa.repository.JpaRepository

interface RatingRepo : JpaRepository<Rating, Long> {
    fun findRatingsByProject(project: Project): List<Rating>
}