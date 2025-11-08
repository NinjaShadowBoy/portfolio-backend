package com.ninjashadowboy.portfolio.dtos

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ContactDto(
    @field:NotBlank(message = "Name is required")
    @field:Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    val name: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    val email: String,

    @field:NotBlank(message = "Message is required")
    @field:Size(min = 10, max = 2000, message = "Message must be between 10 and 2000 characters")
    val message: String
)

data class ContactMessageResponse(
    val id: Long,
    val name: String,
    val email: String,
    val message: String,
    val submittedAt: String,
    val isRead: Boolean,
    val isReplied: Boolean
)
