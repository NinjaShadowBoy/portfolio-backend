package com.ninjashadowboy.portfolio.config

import com.ninjashadowboy.portfolio.services.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
@EnableAspectJAutoProxy
class ApplicationConfig(
    val userService: UserService
) {
    @Bean
    fun encoder(): PasswordEncoder {
        return NoOpPasswordEncoder.getInstance()
//        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationProvider(encoder: PasswordEncoder): AuthenticationProvider {
        val ap = DaoAuthenticationProvider(userService)
        ap.setPasswordEncoder(encoder)
        return ap
    }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager = config.authenticationManager
}