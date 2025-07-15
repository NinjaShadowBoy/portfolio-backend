package com.ninjashadowboy.portfolio.services.impl

import com.ninjashadowboy.portfolio.myProfiler
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
        return myProfiler.profileOperation("Loading user by username") {
            userRepo.findUserByEmail(username!!)
        }
    }
}