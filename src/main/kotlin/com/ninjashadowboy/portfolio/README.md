# Portfolio Backend - Entity and DTO Structure

## Overview

This document describes the refactored entity and DTO structure following clean Kotlin idioms, proper domain modeling,
and clean separation of concerns.

## Architecture Principles

### 1. Clean Kotlin Idioms

- Use of data classes for immutable entities and DTOs
- Non-nullable values where appropriate
- Proper use of Kotlin's type system
- Extension functions for mapping between entities and DTOs

### 2. Domain Model

- Realistic domain relationships
- Proper JPA annotations and relationships
- Validation at the entity level
- Computed properties for derived data

### 3. Clean Separation

- Entities represent the database model
- DTOs represent the API contract
- Clear separation between input and output DTOs
- Extension functions handle mapping

## Entity Structure

### BaseEntity

All entities extend from `BaseEntity` which provides:

- `id`: Primary key
- `createdAt`: Creation timestamp (immutable)
- `updatedAt`: Last update timestamp
- Automatic `updatedAt` management via `@PreUpdate`

### User Entity

```kotlin
@Entity
@Table(name = "users")
data class User(
    val email: String,           // Unique, non-nullable
    val password: String,        // Non-nullable
    val name: String,           // Non-nullable
    val role: Role = Role.USER, // Default role
    val lastLoginAt: LocalDateTime? = null,
    val ratings: MutableList<Rating> = mutableListOf()
) : BaseEntity(), UserDetails
```

**Features:**

- Implements Spring Security's `UserDetails`
- Proper role-based authority mapping
- One-to-many relationship with ratings
- Email uniqueness constraint

### Project Entity

```kotlin
@Entity
@Table(name = "projects")
data class Project(
    val name: String,                    // Non-nullable
    val description: String,             // Non-nullable, TEXT
    val technologies: Set<String>,       // Element collection
    val githubLink: String? = null,
    val challenges: String? = null,      // TEXT
    val whatILearned: String? = null,    // TEXT
    val featured: Boolean = false,
    val photos: MutableList<Photo> = mutableListOf(),
    val ratings: MutableList<Rating> = mutableListOf()
) : BaseEntity()
```

**Features:**

- Computed properties for `averageRating` and `totalRatings`
- Element collection for technologies
- One-to-many relationships with photos and ratings
- TEXT columns for long content

### Photo Entity

```kotlin
@Entity
@Table(name = "photos")
data class Photo(
    val photoUrl: String,                // Non-nullable
    val project: Project                 // Non-nullable relationship
) : BaseEntity()
```

**Features:**

- Many-to-one relationship with Project
- Lazy loading for performance

### Rating Entity

```kotlin
@Entity
@Table(name = "ratings")
data class Rating(
    val rating: Int,                     // 1-5 validation
    val project: Project,                // Non-nullable
    val user: User,                      // Non-nullable
    val comment: String? = null          // Optional TEXT
) : BaseEntity()
```

**Features:**

- Rating validation (1-5 range)
- Many-to-one relationships with Project and User
- Optional comment field

## DTO Structure

### User DTOs

- `UserDto`: Output DTO for user information
- `UserRegistrationDto`: Input DTO for user registration
- `UserUpdateDto`: Input DTO for user updates

### Project DTOs

- `ProjectDto`: Output DTO with computed properties
- `ProjectCreateDto`: Input DTO for project creation
- `ProjectUpdateDto`: Input DTO for project updates

### Photo DTOs

- `PhotoDto`: Output DTO for photo information
- `PhotoCreateDto`: Input DTO for photo creation

### Rating DTOs

- `RatingDto`: Output DTO with user information
- `RatingCreateDto`: Input DTO with validation
- `RatingUpdateDto`: Input DTO for rating updates

### Authentication DTOs

- `LoginRequest`: Input DTO with validation
- `LoginResponse`: Output DTO with token and user info
- `AuthResponse`: Generic authentication response

## Mapping Strategy

### Extension Functions

All entity-to-DTO mapping is handled by extension functions in `EntityMappers.kt`:

```kotlin
// Entity to DTO
fun User.toUserDto(): UserDto
fun Project.toProjectDto(): ProjectDto
fun Photo.toPhotoDto(): PhotoDto
fun Rating.toRatingDto(): RatingDto

// DTO to Entity
fun ProjectCreateDto.toProject(): Project
fun PhotoCreateDto.toPhoto(project: Project): Photo
fun RatingCreateDto.toRating(project: Project, user: User): Rating
```

### Benefits

- Clean separation of concerns
- Type-safe mapping
- Reusable mapping logic
- Easy to test and maintain

## Validation Strategy

### Entity Level

- `@Column(nullable = false)` for required fields
- `init` blocks for business rule validation
- JPA constraints for data integrity

### DTO Level

- `init` blocks for input validation
- Required field validation
- Business rule validation (e.g., rating range)

## Best Practices Implemented

1. **Immutability**: Entities and DTOs use `val` for immutable properties
2. **Non-nullable by default**: Only use nullable types when truly optional
3. **Proper relationships**: Lazy loading, cascade operations, orphan removal
4. **Validation**: Both entity and DTO level validation
5. **Computed properties**: Derived data calculated at the entity level
6. **Clean mapping**: Extension functions for entity-DTO conversion
7. **Audit fields**: Automatic timestamp management
8. **Type safety**: Proper use of Kotlin's type system

## Usage Examples

### Creating a Project

```kotlin
val projectDto = ProjectCreateDto(
    name = "My Project",
    description = "A great project",
    technologies = setOf("Kotlin", "Spring Boot"),
    featured = true
)
val project = projectDto.toProject()
```

### Converting Entity to DTO

```kotlin
val projectDto = project.toProjectDto()
```

### Creating a Rating

```kotlin
val ratingDto = RatingCreateDto(
    rating = 5,
    comment = "Excellent project!",
    projectId = 1L
)
val rating = ratingDto.toRating(project, user)
```

This structure provides a solid foundation for a clean, maintainable, and type-safe portfolio backend application.
