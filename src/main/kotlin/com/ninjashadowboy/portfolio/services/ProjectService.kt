package com.ninjashadowboy.portfolio.services

import com.ninjashadowboy.portfolio.dtos.ProjectCreateDto
import com.ninjashadowboy.portfolio.dtos.ProjectDto
import com.ninjashadowboy.portfolio.dtos.ProjectUpdateDto
import com.ninjashadowboy.portfolio.dtos.RatingDto

interface ProjectService {
    fun getAllProjects(): List<ProjectDto>
    fun getProjectById(id: Long): ProjectDto
    fun createProject(projectDto: ProjectCreateDto): ProjectDto
    fun updateProject(id: Long, projectDto: ProjectUpdateDto): ProjectDto
    fun deleteProject(id: Long)
    
    // Rating-related methods delegated to RatingService
    fun getProjectRatings(projectId: Long): List<RatingDto>
    fun getProjectAverageRating(projectId: Long): Float
    fun getProjectRatingCount(projectId: Long): Int
    fun getProjectRatingDistribution(projectId: Long): Map<Int, Int>
    fun resetProjectRatings(projectId: Long)
}
