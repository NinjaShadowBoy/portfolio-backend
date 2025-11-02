package com.ninjashadowboy.portfolio.controllers.api

import com.ninjashadowboy.portfolio.controllers.docs.ProjectApiDocs
import com.ninjashadowboy.portfolio.dtos.ProjectCreateDto
import com.ninjashadowboy.portfolio.dtos.ProjectDto
import com.ninjashadowboy.portfolio.dtos.ProjectUpdateDto
import com.ninjashadowboy.portfolio.dtos.RatingDto
import com.ninjashadowboy.portfolio.services.ProjectService
import com.ninjashadowboy.portfolio.utils.ResponseUtils
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST API controller for project management operations.
 */
@RestController
@RequestMapping("/api/v1/projects")
class ProjectController(
    private val projectService: ProjectService
) : BaseController(), ProjectApiDocs {

    @Operation(
        summary = "Get all projects",
        description = "Retrieves a complete list of all portfolio projects with ratings and photos."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Projects retrieved successfully",
                content = [Content(
                    mediaType = "application/json",
                    array = ArraySchema(schema = Schema(implementation = ProjectDto::class))
                )]
            ),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    @GetMapping
    fun getAllProjects(): ResponseEntity<List<ProjectDto>> {
        logRequest("getAllProjects")
        val projects = projectService.getAllProjects()
        logResponse("getAllProjects", "Found ${projects.size} projects")
        return ResponseUtils.ok(projects)
    }

    @Operation(
        summary = "Get project by ID",
        description = "Retrieves a single project by its unique identifier."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Project retrieved successfully",
                content = [Content(schema = Schema(implementation = ProjectDto::class))]
            ),
            ApiResponse(responseCode = "404", description = "Project not found")
        ]
    )
    @GetMapping("/{id}")
    fun getProjectById(@PathVariable id: Long): ResponseEntity<ProjectDto> {
        logRequest("getProjectById", mapOf("id" to id))
        val project = projectService.getProjectById(id)
        logResponse("getProjectById")
        return ResponseUtils.ok(project)
    }

    @Operation(
        summary = "Create a new project",
        description = "Creates a new portfolio project. Requires ADMIN role.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Project created successfully",
                content = [Content(schema = Schema(implementation = ProjectDto::class))]
            ),
            ApiResponse(responseCode = "400", description = "Invalid request data"),
            ApiResponse(responseCode = "401", description = "Unauthorized"),
            ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required")
        ]
    )
    @PostMapping
    fun createProject(@RequestBody request: ProjectCreateDto): ResponseEntity<ProjectDto> {
        logRequest("createProject", mapOf("name" to request.name))
        val project = projectService.createProject(request)
        logResponse("createProject", "Created project with ID: ${project.id}")
        return ResponseUtils.created(project)
    }

    @Operation(
        summary = "Update an existing project",
        description = "Updates an existing portfolio project. Requires ADMIN role.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Project updated successfully",
                content = [Content(schema = Schema(implementation = ProjectDto::class))]
            ),
            ApiResponse(responseCode = "400", description = "Invalid request data"),
            ApiResponse(responseCode = "404", description = "Project not found"),
            ApiResponse(responseCode = "401", description = "Unauthorized"),
            ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required")
        ]
    )
    @PutMapping("/{id}")
    fun updateProject(
        @PathVariable id: Long,
        @RequestBody request: ProjectUpdateDto
    ): ResponseEntity<ProjectDto> {
        logRequest("updateProject", mapOf("id" to id))
        val project = projectService.updateProject(id, request)
        logResponse("updateProject")
        return ResponseUtils.ok(project)
    }

    @Operation(
        summary = "Delete a project",
        description = "Deletes a portfolio project by its ID. Requires ADMIN role.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Project deleted successfully"),
            ApiResponse(responseCode = "404", description = "Project not found"),
            ApiResponse(responseCode = "401", description = "Unauthorized"),
            ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required")
        ]
    )
    @DeleteMapping("/{id}")
    fun deleteProject(@PathVariable id: Long): ResponseEntity<Unit> {
        logRequest("deleteProject", mapOf("id" to id))
        projectService.deleteProject(id)
        logResponse("deleteProject")
        return ResponseUtils.noContent()
    }

    // ────────────────────────────────────────────────────────────────────────────────
    // Rating-related endpoints
    // ────────────────────────────────────────────────────────────────────────────────

    @Operation(
        summary = "Get all ratings for a project",
        description = "Retrieves all ratings for a specific project."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Ratings retrieved successfully",
                content = [Content(array = ArraySchema(schema = Schema(implementation = RatingDto::class)))]
            ),
            ApiResponse(responseCode = "404", description = "Project not found")
        ]
    )
    @GetMapping("/{id}/ratings")
    fun getProjectRatings(@PathVariable id: Long): ResponseEntity<List<RatingDto>> {
        logRequest("getProjectRatings", mapOf("projectId" to id))
        val ratings = projectService.getProjectRatings(id)
        return ResponseUtils.ok(ratings)
    }

    @Operation(
        summary = "Get average rating for a project",
        description = "Returns the average rating value for a project (0.0 to 5.0)."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Average rating retrieved successfully"),
            ApiResponse(responseCode = "404", description = "Project not found")
        ]
    )
    @GetMapping("/{id}/ratings/average")
    fun getProjectAverageRating(@PathVariable id: Long): ResponseEntity<Map<String, Float>> {
        logRequest("getProjectAverageRating", mapOf("projectId" to id))
        val average = projectService.getProjectAverageRating(id)
        return ResponseUtils.ok(ResponseUtils.singleValue("averageRating", average))
    }

    @Operation(
        summary = "Get rating count for a project",
        description = "Returns the total number of ratings for a project."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Rating count retrieved successfully"),
            ApiResponse(responseCode = "404", description = "Project not found")
        ]
    )
    @GetMapping("/{id}/ratings/count")
    fun getProjectRatingCount(@PathVariable id: Long): ResponseEntity<Map<String, Int>> {
        logRequest("getProjectRatingCount", mapOf("projectId" to id))
        val count = projectService.getProjectRatingCount(id)
        return ResponseUtils.ok(ResponseUtils.singleValue("count", count))
    }

    @Operation(
        summary = "Get rating distribution for a project",
        description = "Returns the distribution of ratings (how many 1-star, 2-star, etc.)."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Rating distribution retrieved successfully"),
            ApiResponse(responseCode = "404", description = "Project not found")
        ]
    )
    @GetMapping("/{id}/ratings/distribution")
    fun getProjectRatingDistribution(@PathVariable id: Long): ResponseEntity<Map<Int, Int>> {
        logRequest("getProjectRatingDistribution", mapOf("projectId" to id))
        val distribution = projectService.getProjectRatingDistribution(id)
        return ResponseUtils.ok(distribution)
    }

    @Operation(
        summary = "Reset all ratings for a project",
        description = "Deletes all ratings for a specific project. Requires ADMIN role.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Ratings reset successfully"),
            ApiResponse(responseCode = "404", description = "Project not found"),
            ApiResponse(responseCode = "401", description = "Unauthorized"),
            ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required")
        ]
    )
    @DeleteMapping("/{id}/ratings")
    fun resetProjectRatings(@PathVariable id: Long): ResponseEntity<Unit> {
        logRequest("resetProjectRatings", mapOf("projectId" to id))
        projectService.resetProjectRatings(id)
        logResponse("resetProjectRatings")
        return ResponseUtils.noContent()
    }
}
