package com.ninjashadowboy.portfolio.services.impl

import com.ninjashadowboy.portfolio.dtos.ProjectCreateDto
import com.ninjashadowboy.portfolio.dtos.ProjectDto
import com.ninjashadowboy.portfolio.dtos.ProjectUpdateDto
import com.ninjashadowboy.portfolio.dtos.RatingDto
import com.ninjashadowboy.portfolio.dtos.toProject
import com.ninjashadowboy.portfolio.dtos.toProjectDto
import com.ninjashadowboy.portfolio.entities.Project
import com.ninjashadowboy.portfolio.exceptions.ResourceNotFoundException
import com.ninjashadowboy.portfolio.repositories.ProjectRepo
import com.ninjashadowboy.portfolio.services.ProjectService
import com.ninjashadowboy.portfolio.services.RatingService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ProjectServiceImpl(
    private val projectRepo: ProjectRepo,
    private val ratingService: RatingService
) : ProjectService {
    
    override fun getAllProjects(): List<ProjectDto> {
        return projectRepo.findAll().map { it.toProjectDto() }
    }

    override fun getProjectById(id: Long): ProjectDto {
        val project = projectRepo.findByIdOrNull(id)
            ?: throw ResourceNotFoundException("Project", id)
        return project.toProjectDto()
    }

    override fun createProject(projectDto: ProjectCreateDto): ProjectDto {
        val newProject = projectRepo.save(projectDto.toProject())
        return newProject.toProjectDto()
    }

    override fun updateProject(id: Long, projectDto: ProjectUpdateDto): ProjectDto {
        val existingProject = projectRepo.findByIdOrNull(id)
            ?: throw ResourceNotFoundException("Project", id)

        // Update fields directly on the existing entity instead of using copy()
        // This prevents Hibernate's "don't change the reference to a collection with delete-orphan" error
        projectDto.name?.let { existingProject.name = it }
        projectDto.description?.let { existingProject.description = it }
        projectDto.technologies?.let { existingProject.technologies = it }
        projectDto.githubLink?.let { existingProject.githubLink = it }
        projectDto.challenges?.let { existingProject.challenges = it }
        projectDto.whatILearned?.let { existingProject.whatILearned = it }
        projectDto.featured?.let { existingProject.featured = it }

        return projectRepo.save(existingProject).toProjectDto()
    }

    override fun deleteProject(id: Long) {
        if (!projectRepo.existsById(id)) {
            throw ResourceNotFoundException("Project", id)
        }
        // Delete all ratings associated with this project first
        ratingService.deleteAllRatingsByProjectId(id)
        projectRepo.deleteById(id)
    }
    
    // Rating-related methods delegated to RatingService
    override fun getProjectRatings(projectId: Long): List<RatingDto> {
        return ratingService.getRatingsByProjectId(projectId)
    }
    
    override fun getProjectAverageRating(projectId: Long): Float {
        return ratingService.getAverageRatingByProjectId(projectId)
    }
    
    override fun getProjectRatingCount(projectId: Long): Int {
        return ratingService.getRatingCountByProjectId(projectId)
    }
    
    override fun getProjectRatingDistribution(projectId: Long): Map<Int, Int> {
        return ratingService.getRatingDistributionByProjectId(projectId)
    }
    
    override fun resetProjectRatings(projectId: Long) {
        // Verify project exists first
        if (!projectRepo.existsById(projectId)) {
            throw ResourceNotFoundException("Project", projectId)
        }
        ratingService.deleteAllRatingsByProjectId(projectId)
    }
}