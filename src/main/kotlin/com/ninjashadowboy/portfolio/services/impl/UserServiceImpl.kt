package com.ninjashadowboy.portfolio.services.impl

import com.ninjashadowboy.portfolio.MyProfiler
import com.ninjashadowboy.portfolio.dtos.UserRegistrationDto
import com.ninjashadowboy.portfolio.entities.User
import com.ninjashadowboy.portfolio.repositories.UserRepo
import com.ninjashadowboy.portfolio.services.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    val userRepo: UserRepo
) : UserService {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    override fun loadUserByUsername(username: String?): UserDetails? {
        return MyProfiler.profileOperation("Loading user by username") {
            userRepo.findUserByEmail(username!!)
        }
    }

    override fun registerUser(user: UserRegistrationDto): Boolean {
        if (userRepo.existsByEmail(user.email)) {
            return false
        } else {
            userRepo.save(User(user.email, user.password, user.name))
            return true
        }
    }
}