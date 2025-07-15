package com.ninjashadowboy.portfolio.repositories

import com.ninjashadowboy.portfolio.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserRepo : JpaRepository<User, Long> {
    fun findUserByEmail(email: String): User?
}