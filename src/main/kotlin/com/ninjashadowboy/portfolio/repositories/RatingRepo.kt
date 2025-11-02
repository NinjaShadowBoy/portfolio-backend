package com.ninjashadowboy.portfolio.repositories

import com.ninjashadowboy.portfolio.entities.Project
import com.ninjashadowboy.portfolio.entities.Rating
import com.ninjashadowboy.portfolio.entities.User
import org.springframework.data.jpa.repository.JpaRepository

interface RatingRepo : JpaRepository<Rating, Long> {
    fun findRatingsByProject(project: Project): List<Rating>
    fun findRatingsByProjectId(projectId: Long): List<Rating>
    fun findRatingsByUserId(userId: Long): List<Rating>
    fun findRatingsByUserAndProject(user: User, project: Project): Rating?
    fun existsByUserIdAndProjectId(userId: Long, projectId: Long): Boolean
    fun deleteAllByProjectId(projectId: Long)
}