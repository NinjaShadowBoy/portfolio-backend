package com.ninjashadowboy.portfolio.services

import com.ninjashadowboy.portfolio.dtos.ProjectDto

interface ProjectService {
    fun getAllProjects(): List<ProjectDto>
}
