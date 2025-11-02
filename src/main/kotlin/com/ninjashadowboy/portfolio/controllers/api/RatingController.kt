package com.ninjashadowboy.portfolio.controllers.api

import com.ninjashadowboy.portfolio.controllers.docs.RatingApiDocs
import com.ninjashadowboy.portfolio.dtos.RatingCreateDto
import com.ninjashadowboy.portfolio.dtos.RatingDto
import com.ninjashadowboy.portfolio.dtos.RatingUpdateDto
import com.ninjashadowboy.portfolio.services.RatingService
import com.ninjashadowboy.portfolio.utils.ResponseUtils
import com.ninjashadowboy.portfolio.utils.SecurityUtils
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

/**
 * REST API controller for project rating management.
 */
@RestController
@RequestMapping("/api/v1/ratings")
class RatingController(
    private val ratingService: RatingService
) : BaseController(), RatingApiDocs {

    @Operation(
        summary = "Get all ratings",
        description = "Retrieves all ratings in the system. Typically used for admin purposes.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Ratings retrieved successfully",
                content = [Content(array = ArraySchema(schema = Schema(implementation = RatingDto::class)))]
            )
        ]
    )
    @GetMapping
    fun getAllRatings(): ResponseEntity<List<RatingDto>> {
        logRequest("getAllRatings")
        val ratings = ratingService.getAllRatings()
        return ResponseUtils.ok(ratings)
    }

    @Operation(
        summary = "Get rating by ID",
        description = "Retrieves a specific rating by its unique identifier.",
        security = []
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Rating retrieved successfully",
                content = [Content(schema = Schema(implementation = RatingDto::class))]
            ),
            ApiResponse(responseCode = "404", description = "Rating not found")
        ]
    )
    @GetMapping("/{id}")
    fun getRatingById(@PathVariable id: Long): ResponseEntity<RatingDto> {
        logRequest("getRatingById", mapOf("id" to id))
        val rating = ratingService.getRatingById(id)
        return ResponseUtils.ok(rating)
    }

    @Operation(
        summary = "Get ratings for a specific project",
        description = "Retrieves all ratings for a given project, sorted by creation date (newest first).",
        security = []
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
    @GetMapping("/project/{projectId}")
    fun getRatingsByProject(@PathVariable projectId: Long): ResponseEntity<List<RatingDto>> {
        logRequest("getRatingsByProject", mapOf("projectId" to projectId))
        val ratings = ratingService.getRatingsByProjectId(projectId)
        return ResponseUtils.ok(ratings)
    }

    @Operation(
        summary = "Get ratings by current user",
        description = "Retrieves all ratings created by the currently authenticated user.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Ratings retrieved successfully",
                content = [Content(array = ArraySchema(schema = Schema(implementation = RatingDto::class)))]
            ),
            ApiResponse(responseCode = "401", description = "Unauthorized")
        ]
    )
    @GetMapping("/my-ratings")
    fun getMyRatings(authentication: Authentication): ResponseEntity<List<RatingDto>> {
        val userId = SecurityUtils.getUserIdFromAuth(authentication)
        logRequest("getMyRatings", mapOf("userId" to userId))
        val ratings = ratingService.getRatingsByUserId(userId)
        return ResponseUtils.ok(ratings)
    }

    @Operation(
        summary = "Get rating distribution for a project",
        description = "Gets the distribution of ratings for a project (how many ratings for each star value 1-5).",
        security = []
    )
    @GetMapping("/project/{projectId}/distribution")
    fun getRatingDistribution(@PathVariable projectId: Long): ResponseEntity<Map<Int, Int>> {
        logRequest("getRatingDistribution", mapOf("projectId" to projectId))
        val distribution = ratingService.getRatingDistributionByProjectId(projectId)
        return ResponseUtils.ok(distribution)
    }

    @Operation(
        summary = "Get average rating for a project",
        description = "Calculates and returns the average rating for a project (0.0 to 5.0).",
        security = []
    )
    @GetMapping("/project/{projectId}/average")
    fun getAverageRating(@PathVariable projectId: Long): ResponseEntity<Map<String, Float>> {
        logRequest("getAverageRating", mapOf("projectId" to projectId))
        val average = ratingService.getAverageRatingByProjectId(projectId)
        return ResponseUtils.ok(ResponseUtils.singleValue("averageRating", average))
    }

    @Operation(
        summary = "Get rating count for a project",
        description = "Returns the total number of ratings for a project.",
        security = []
    )
    @GetMapping("/project/{projectId}/count")
    fun getRatingCount(@PathVariable projectId: Long): ResponseEntity<Map<String, Int>> {
        logRequest("getRatingCount", mapOf("projectId" to projectId))
        val count = ratingService.getRatingCountByProjectId(projectId)
        return ResponseUtils.ok(ResponseUtils.singleValue("count", count))
    }

    @Operation(
        summary = "Check if user has rated a project",
        description = "Checks whether the current user has already rated a specific project.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @GetMapping("/project/{projectId}/has-rated")
    fun hasUserRatedProject(
        @PathVariable projectId: Long,
        authentication: Authentication
    ): ResponseEntity<Map<String, Boolean>> {
        val userId = SecurityUtils.getUserIdFromAuth(authentication)
        logRequest("hasUserRatedProject", mapOf("projectId" to projectId, "userId" to userId))
        val hasRated = ratingService.hasUserRatedProject(userId, projectId)
        return ResponseUtils.ok(ResponseUtils.singleValue("hasRated", hasRated))
    }

    @Operation(
        summary = "Create a new rating",
        description = "Creates a new rating for a project. Users can only rate each project once.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Rating created successfully",
                content = [Content(schema = Schema(implementation = RatingDto::class))]
            ),
            ApiResponse(responseCode = "400", description = "Invalid data or user already rated this project"),
            ApiResponse(responseCode = "404", description = "Project not found"),
            ApiResponse(responseCode = "401", description = "Unauthorized")
        ]
    )
    @PostMapping
    fun createRating(
        @RequestBody request: RatingCreateDto,
        authentication: Authentication
    ): ResponseEntity<RatingDto> {
        val userId = SecurityUtils.getUserIdFromAuth(authentication)
        logRequest("createRating", mapOf("projectId" to request.projectId, "userId" to userId))
        val rating = ratingService.createRating(request, userId)
        logResponse("createRating", "Created rating with ID: ${rating.id}")
        return ResponseUtils.created(rating)
    }

    @Operation(
        summary = "Update an existing rating",
        description = "Updates a rating that was previously created by the current user.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Rating updated successfully",
                content = [Content(schema = Schema(implementation = RatingDto::class))]
            ),
            ApiResponse(responseCode = "400", description = "Invalid data"),
            ApiResponse(responseCode = "404", description = "Rating not found"),
            ApiResponse(responseCode = "401", description = "Unauthorized"),
            ApiResponse(responseCode = "403", description = "Forbidden - Can only update own ratings")
        ]
    )
    @PutMapping("/{id}")
    fun updateRating(
        @PathVariable id: Long,
        @RequestBody request: RatingUpdateDto,
        authentication: Authentication
    ): ResponseEntity<RatingDto> {
        val userId = SecurityUtils.getUserIdFromAuth(authentication)
        logRequest("updateRating", mapOf("id" to id, "userId" to userId))
        val rating = ratingService.updateRating(id, request, userId)
        logResponse("updateRating")
        return ResponseUtils.ok(rating)
    }

    @Operation(
        summary = "Delete a rating",
        description = "Deletes a rating. Users can only delete their own ratings (unless admin).",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Rating deleted successfully"),
            ApiResponse(responseCode = "404", description = "Rating not found"),
            ApiResponse(responseCode = "401", description = "Unauthorized"),
            ApiResponse(responseCode = "403", description = "Forbidden - Can only delete own ratings")
        ]
    )
    @DeleteMapping("/{id}")
    fun deleteRating(
        @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<Unit> {
        val userId = SecurityUtils.getUserIdFromAuth(authentication)
        logRequest("deleteRating", mapOf("id" to id, "userId" to userId))
        ratingService.deleteRating(id, userId)
        logResponse("deleteRating")
        return ResponseUtils.noContent()
    }

    @Operation(
        summary = "Reset all ratings for a project",
        description = "Deletes all ratings for a specific project. Admin-only operation.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "All ratings deleted successfully"),
            ApiResponse(responseCode = "404", description = "Project not found"),
            ApiResponse(responseCode = "401", description = "Unauthorized"),
            ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
        ]
    )
    @DeleteMapping("/project/{projectId}")
    fun deleteAllRatingsForProject(@PathVariable projectId: Long): ResponseEntity<Unit> {
        logRequest("deleteAllRatingsForProject", mapOf("projectId" to projectId))
        ratingService.deleteAllRatingsByProjectId(projectId)
        logResponse("deleteAllRatingsForProject")
        return ResponseUtils.noContent()
    }
}
