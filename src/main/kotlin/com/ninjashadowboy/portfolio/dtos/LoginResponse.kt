package com.ninjashadowboy.portfolio.dtos

data class LoginResponse(
    val token: String,
    val user: UserDto,
    val expiresIn: Long
)

data class AuthResponse(
    val message: String,
    val user: UserDto? = null
)