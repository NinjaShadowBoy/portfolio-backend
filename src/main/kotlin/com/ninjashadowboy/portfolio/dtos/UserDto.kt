package com.ninjashadowboy.portfolio.dtos

import com.ninjashadowboy.portfolio.entities.Role
import java.time.LocalDateTime

data class UserDto(
    val id: Long,
    val email: String,
    val name: String,
    val role: Role,
    val createdAt: LocalDateTime,
    val lastLoginAt: LocalDateTime?
)

data class UserRegistrationDto(
    val email: String,
    val password: String,
    val name: String
) {
    init {
        require(email.isNotBlank()) { "Email cannot be blank" }
        require(password.isNotBlank()) { "Password cannot be blank" }
        require(name.isNotBlank()) { "Name cannot be blank" }
    }
}

data class UserUpdateDto(
    val name: String? = null,
    val email: String? = null
) 