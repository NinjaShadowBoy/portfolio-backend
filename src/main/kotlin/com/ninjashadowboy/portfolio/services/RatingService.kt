package com.ninjashadowboy.portfolio.services

import com.ninjashadowboy.portfolio.dtos.RatingCreateDto
import com.ninjashadowboy.portfolio.dtos.RatingDto
import com.ninjashadowboy.portfolio.dtos.RatingUpdateDto

/**
 * Service interface for managing project ratings.
 * 
 * Provides operations for creating, retrieving, updating, and deleting ratings.
 * Also includes methods for calculating rating statistics and retrieving ratings by project.
 */
interface RatingService {
    /**
     * Retrieves all ratings in the system.
     * 
     * @return List of all ratings as DTOs
     */
    fun getAllRatings(): List<RatingDto>
    
    /**
     * Retrieves a specific rating by its ID.
     * 
     * @param id The rating ID
     * @return The rating DTO
     * @throws ResourceNotFoundException if rating not found
     */
    fun getRatingById(id: Long): RatingDto
    
    /**
     * Retrieves all ratings for a specific project.
     * 
     * @param projectId The project ID
     * @return List of ratings for the project
     * @throws ResourceNotFoundException if project not found
     */
    fun getRatingsByProjectId(projectId: Long): List<RatingDto>
    
    /**
     * Retrieves all ratings made by a specific user.
     * 
     * @param userId The user ID
     * @return List of ratings by the user
     */
    fun getRatingsByUserId(userId: Long): List<RatingDto>
    
    /**
     * Creates a new rating for a project.
     * 
     * @param ratingDto The rating creation data
     * @param userId The ID of the user creating the rating
     * @return The created rating DTO
     * @throws ResourceNotFoundException if project or user not found
     * @throws IllegalStateException if user already rated this project
     */
    fun createRating(ratingDto: RatingCreateDto, userId: Long): RatingDto
    
    /**
     * Updates an existing rating.
     * Only the user who created the rating can update it.
     * 
     * @param id The rating ID
     * @param ratingDto The update data
     * @param userId The ID of the user attempting the update
     * @return The updated rating DTO
     * @throws ResourceNotFoundException if rating not found
     * @throws IllegalStateException if user is not the rating owner
     */
    fun updateRating(id: Long, ratingDto: RatingUpdateDto, userId: Long): RatingDto
    
    /**
     * Deletes a rating.
     * Only the user who created the rating or an admin can delete it.
     * 
     * @param id The rating ID
     * @param userId The ID of the user attempting the deletion
     * @throws ResourceNotFoundException if rating not found
     * @throws IllegalStateException if user is not authorized to delete
     */
    fun deleteRating(id: Long, userId: Long)
    
    /**
     * Calculates the average rating for a specific project.
     * 
     * @param projectId The project ID
     * @return The average rating (0.0 to 5.0)
     */
    fun getAverageRatingByProjectId(projectId: Long): Float
    
    /**
     * Gets the total count of ratings for a specific project.
     * 
     * @param projectId The project ID
     * @return The total number of ratings
     */
    fun getRatingCountByProjectId(projectId: Long): Int
    
    /**
     * Checks if a user has already rated a specific project.
     * 
     * @param userId The user ID
     * @param projectId The project ID
     * @return true if the user has rated the project, false otherwise
     */
    fun hasUserRatedProject(userId: Long, projectId: Long): Boolean
    
    /**
     * Gets the distribution of ratings for a project (how many 1-star, 2-star, etc.).
     * 
     * @param projectId The project ID
     * @return Map of rating value to count (e.g., {5=10, 4=5, 3=2, 2=1, 1=0})
     */
    fun getRatingDistributionByProjectId(projectId: Long): Map<Int, Int>
    
    /**
     * Deletes all ratings for a specific project.
     * Used when a project is deleted or ratings need to be reset.
     * 
     * @param projectId The project ID
     */
    fun deleteAllRatingsByProjectId(projectId: Long)
}
