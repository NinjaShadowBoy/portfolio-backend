package com.ninjashadowboy.portfolio.dtos

import com.ninjashadowboy.portfolio.entities.Photo
import com.ninjashadowboy.portfolio.entities.Project
import com.ninjashadowboy.portfolio.entities.Rating
import com.ninjashadowboy.portfolio.entities.User

// User mapping extensions
fun User.toUserDto(): UserDto = UserDto(
    id = id,
    email = email,
    name = name,
    role = role,
    createdAt = createdAt,
    lastLoginAt = lastLoginAt
)

// Project mapping extensions
fun Project.toProjectDto(): ProjectDto = ProjectDto(
    id = id,
    name = name,
    description = description,
    technologies = technologies,
    githubLink = githubLink,
    challenges = challenges,
    whatILearned = whatILearned,
    featured = featured,
    createdAt = createdAt,
    updatedAt = updatedAt,
    averageRating = averageRating,
    totalRatings = totalRatings,
    photoUrls = photos.map { it.photoUrl }
)

fun ProjectCreateDto.toProject(): Project = Project(
    name = name,
    description = description,
    technologies = technologies,
    githubLink = githubLink,
    challenges = challenges,
    whatILearned = whatILearned,
    featured = featured
)

// Photo mapping extensions
fun Photo.toPhotoDto(): PhotoDto = PhotoDto(
    id = id,
    photoUrl = photoUrl,
    projectId = project?.id ?: 0
)

fun PhotoCreateDto.toPhoto(project: Project): Photo = Photo(
    photoUrl = photoUrl,
    project = project,
)

// Rating mapping extensions
fun Rating.toRatingDto(): RatingDto = RatingDto(
    id = id,
    rating = rating,
    comment = comment,
    createdAt = createdAt,
    userId = user.id,
    userName = user.name,
    projectId = project.id
)

fun RatingCreateDto.toRating(project: Project, user: User): Rating = Rating(
    rating = rating,
    comment = comment,
    project = project,
    user = user
)