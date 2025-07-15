package com.ninjashadowboy.portfolio.controllers

import com.ninjashadowboy.portfolio.dtos.ProjectDto
import com.ninjashadowboy.portfolio.services.ProjectService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/projects")
class ProjectApi(
    private val projectService: ProjectService
) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "Get all projects")
    @ApiResponse(responseCode = "200", description = "Projects retrieved successfully")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping
    fun getAllProjects(): List<ProjectDto> = projectService.getAllProjects()
}
