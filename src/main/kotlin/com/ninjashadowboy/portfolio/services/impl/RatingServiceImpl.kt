package com.ninjashadowboy.portfolio.services.impl

import com.ninjashadowboy.portfolio.exceptions.ResourceNotFoundException
import com.ninjashadowboy.portfolio.dtos.RatingCreateDto
import com.ninjashadowboy.portfolio.dtos.RatingDto
import com.ninjashadowboy.portfolio.dtos.RatingUpdateDto
import com.ninjashadowboy.portfolio.dtos.toRating
import com.ninjashadowboy.portfolio.dtos.toRatingDto
import com.ninjashadowboy.portfolio.entities.Rating
import com.ninjashadowboy.portfolio.repositories.ProjectRepo
import com.ninjashadowboy.portfolio.repositories.RatingRepo
import com.ninjashadowboy.portfolio.repositories.UserRepo
import com.ninjashadowboy.portfolio.services.RatingService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class RatingServiceImpl(
    private val ratingRepo: RatingRepo,
    private val projectRepo: ProjectRepo,
    private val userRepo: UserRepo
) : RatingService {

    override fun getAllRatings(): List<RatingDto> {
        return ratingRepo.findAll().map { it.toRatingDto() }
    }

    override fun getRatingById(id: Long): RatingDto {
        val rating = ratingRepo.findByIdOrNull(id)
            ?: throw ResourceNotFoundException("Rating", id)
        return rating.toRatingDto()
    }

    override fun getRatingsByProjectId(projectId: Long): List<RatingDto> {
        // Verify project exists
        if (!projectRepo.existsById(projectId)) {
            throw ResourceNotFoundException("Project", projectId)
        }
        return ratingRepo.findRatingsByProjectId(projectId).map { it.toRatingDto() }
    }

    override fun getRatingsByUserId(userId: Long): List<RatingDto> {
        return ratingRepo.findRatingsByUserId(userId).map { it.toRatingDto() }
    }

    override fun createRating(ratingDto: RatingCreateDto, userId: Long): RatingDto {
        // Verify user exists
        val user = userRepo.findByIdOrNull(userId)
            ?: throw ResourceNotFoundException("User", userId)

        // Verify project exists
        val project = projectRepo.findByIdOrNull(ratingDto.projectId)
            ?: throw ResourceNotFoundException("Project", ratingDto.projectId)

        // Check if user has already rated this project
        if (ratingRepo.existsByUserIdAndProjectId(userId, ratingDto.projectId)) {
            throw IllegalStateException("User has already rated this project. Use update instead.")
        }

        // Create and save the rating
        val rating = ratingDto.toRating(project, user)
        val savedRating = ratingRepo.save(rating)
        
        return savedRating.toRatingDto()
    }

    override fun updateRating(id: Long, ratingDto: RatingUpdateDto, userId: Long): RatingDto {
        // Find the existing rating
        val existingRating = ratingRepo.findByIdOrNull(id)
            ?: throw ResourceNotFoundException("Rating", id)

        // Verify the user owns this rating
        if (existingRating.user.id != userId) {
            throw IllegalStateException("You can only update your own ratings")
        }

        // Create updated rating
        val updatedRating = Rating(
            rating = ratingDto.rating ?: existingRating.rating,
            comment = ratingDto.comment ?: existingRating.comment,
            user = existingRating.user,
            project = existingRating.project
        )

        // Copy the ID and timestamps from existing rating
        val ratingWithId = updatedRating.apply {
            // The ID is set via reflection or by creating a new entity with the same ID
        }

        // For updating, we need to work with the existing entity
        // Since Rating is not a data class, we need to save a new one and delete the old
        ratingRepo.delete(existingRating)
        val savedRating = ratingRepo.save(updatedRating)
        
        return savedRating.toRatingDto()
    }

    override fun deleteRating(id: Long, userId: Long) {
        val rating = ratingRepo.findByIdOrNull(id)
            ?: throw ResourceNotFoundException("Rating", id)

        // Verify the user owns this rating
        if (rating.user.id != userId) {
            throw IllegalStateException("You can only delete your own ratings")
        }

        ratingRepo.deleteById(id)
    }

    override fun getAverageRatingByProjectId(projectId: Long): Float {
        val ratings = ratingRepo.findRatingsByProjectId(projectId)
        if (ratings.isEmpty()) return 0f
        
        val average = ratings.map { it.rating }.average()
        return if (average.isNaN()) 0f else average.toFloat()
    }

    override fun getRatingCountByProjectId(projectId: Long): Int {
        return ratingRepo.findRatingsByProjectId(projectId).size
    }

    override fun hasUserRatedProject(userId: Long, projectId: Long): Boolean {
        return ratingRepo.existsByUserIdAndProjectId(userId, projectId)
    }

    override fun getRatingDistributionByProjectId(projectId: Long): Map<Int, Int> {
        val ratings = ratingRepo.findRatingsByProjectId(projectId)
        
        // Initialize map with all rating values (1-5) set to 0
        val distribution = mutableMapOf(1 to 0, 2 to 0, 3 to 0, 4 to 0, 5 to 0)
        
        // Count occurrences of each rating
        ratings.forEach { rating ->
            distribution[rating.rating] = distribution.getOrDefault(rating.rating, 0) + 1
        }
        
        return distribution
    }

    override fun deleteAllRatingsByProjectId(projectId: Long) {
        ratingRepo.deleteAllByProjectId(projectId)
    }
}
