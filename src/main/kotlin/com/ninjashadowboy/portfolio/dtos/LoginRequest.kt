package com.ninjashadowboy.portfolio.dtos

import kotlin.text.isNotBlank

data class LoginRequest(
    val email: String,
    val password: String
) {
    init {
        require(email.isNotBlank()) { "Email cannot be blank" }
        require(password.isNotBlank()) { "Password cannot be blank" }
    }
}