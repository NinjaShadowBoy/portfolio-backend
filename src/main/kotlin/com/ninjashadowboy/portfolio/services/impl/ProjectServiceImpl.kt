package com.ninjashadowboy.portfolio.services.impl

import com.ninjashadowboy.portfolio.dtos.ProjectDto
import com.ninjashadowboy.portfolio.dtos.toProjectDto
import com.ninjashadowboy.portfolio.entities.Project
import com.ninjashadowboy.portfolio.repositories.ProjectRepo
import com.ninjashadowboy.portfolio.services.ProjectService
import org.springframework.stereotype.Service

@Service
class ProjectServiceImpl(
    private val projectRepo: ProjectRepo,
) : ProjectService {
    override fun getAllProjects(): List<ProjectDto> {
        val projects: List<Project> = projectRepo.findAll()

        return projects.map { it.toProjectDto() }
    }
}