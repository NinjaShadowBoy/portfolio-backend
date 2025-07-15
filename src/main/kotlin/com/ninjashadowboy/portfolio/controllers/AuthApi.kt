package com.ninjashadowboy.portfolio.controllers

import com.ninjashadowboy.portfolio.config.JwtService
import com.ninjashadowboy.portfolio.dtos.LoginRequest
import com.ninjashadowboy.portfolio.dtos.LoginResponse
import com.ninjashadowboy.portfolio.entities.User
import com.ninjashadowboy.portfolio.myProfiler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
internal class AuthApi(
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        val result = myProfiler.profileOperation("Handle login ") {
            val authentication = myProfiler.profileOperation("Authentication to login") {
                authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken(
                        loginRequest.email, loginRequest.password
                    )
                )
            }

            SecurityContextHolder.getContext().authentication = authentication

            val user = authentication.principal
            if (user !is User) {
                return@profileOperation ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
            }

            // Profiling token creation
            val response =
                myProfiler.profileOperation("JWT token generation") { jwtService.generateLoginResponse(user) }

            return@profileOperation ResponseEntity.ok(response)
        }

        return result
    }

}
