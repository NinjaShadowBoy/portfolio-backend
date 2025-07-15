package com.ninjashadowboy.portfolio.dtos

import java.time.LocalDateTime

data class RatingDto(
    val id: Long,
    val rating: Int,
    val comment: String?,
    val createdAt: LocalDateTime,
    val userId: Long,
    val userName: String,
    val projectId: Long
)

data class RatingCreateDto(
    val rating: Int,
    val comment: String? = null,
    val projectId: Long
) {
    init {
        require(rating in 1..5) { "Rating must be between 1 and 5" }
    }
}

data class RatingUpdateDto(
    val rating: Int? = null,
    val comment: String? = null
) {
    init {
        rating?.let { require(it in 1..5) { "Rating must be between 1 and 5" } }
    }
}