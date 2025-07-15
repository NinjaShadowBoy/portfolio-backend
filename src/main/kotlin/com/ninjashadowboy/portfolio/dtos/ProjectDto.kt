package com.ninjashadowboy.portfolio.dtos

import java.time.LocalDateTime

data class ProjectDto(
    val id: Long,
    val name: String,
    val description: String,
    val technologies: Set<String>,
    val githubLink: String?,
    val challenges: String?,
    val whatILearned: String?,
    val featured: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val averageRating: Float,
    val totalRatings: Int,
    val photoUrls: List<String>
)

data class ProjectCreateDto(
    val name: String,
    val description: String,
    val technologies: Set<String>,
    val githubLink: String? = null,
    val challenges: String? = null,
    val whatILearned: String? = null,
    val featured: Boolean = false
)

data class ProjectUpdateDto(
    val name: String? = null,
    val description: String? = null,
    val technologies: Set<String>? = null,
    val githubLink: String? = null,
    val challenges: String? = null,
    val whatILearned: String? = null,
    val featured: Boolean? = null
)