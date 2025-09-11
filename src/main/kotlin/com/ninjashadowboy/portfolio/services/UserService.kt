package com.ninjashadowboy.portfolio.services

import com.ninjashadowboy.portfolio.dtos.UserRegistrationDto
import org.springframework.security.core.userdetails.UserDetailsService

interface UserService : UserDetailsService {
    fun registerUser(user: UserRegistrationDto): Boolean
}