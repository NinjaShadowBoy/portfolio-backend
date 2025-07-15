package com.ninjashadowboy.portfolio.dtos

data class PhotoDto(
    val id: Long,
    val photoUrl: String,
    val projectId: Long
)

data class PhotoCreateDto(
    val photoUrl: String,
    val projectId: Long
)