package com.ninjashadowboy.portfolio.config

import com.ninjashadowboy.portfolio.security.oauth2.CustomOAuth2UserService
import com.ninjashadowboy.portfolio.security.oauth2.OAuth2AuthenticationFailureHandler
import com.ninjashadowboy.portfolio.security.oauth2.OAuth2AuthenticationSuccessHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val authenticationProvider: AuthenticationProvider,
    private val customOAuth2UserService: CustomOAuth2UserService,
    private val oAuth2AuthenticationSuccessHandler: OAuth2AuthenticationSuccessHandler,
    private val oAuth2AuthenticationFailureHandler: OAuth2AuthenticationFailureHandler
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { it.disable() }.cors { it }.authorizeHttpRequests { auth ->
            auth.requestMatchers(
                "/**", "/api/v1/auth/**", "/oauth2/**",
                "/css/**", "/js/**", "/images/**", "/favicon.ico",
                "/uploads/**", "/swagger-ui", "/swagger-ui/**", "/api-docs", "/api-docs/**",
            ).permitAll().requestMatchers(HttpMethod.GET, "/api/v1/projects").permitAll()
                .requestMatchers("/api/v1/photo/**").hasAuthority("ADMIN").anyRequest().authenticated()
        }.sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authenticationProvider(authenticationProvider).formLogin { formLogin -> formLogin.disable() }
            .oauth2Login { oauth2 ->
                oauth2
                    .userInfoEndpoint { userInfo ->
                        userInfo.userService(customOAuth2UserService)
                    }
                    .successHandler(oAuth2AuthenticationSuccessHandler)
                    .failureHandler(oAuth2AuthenticationFailureHandler)
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            allowedOrigins = listOf(
                "http://localhost:8080",
                "http://localhost:3000",
                "http://localhost:4200",
                "https://ninjashadowboy.github.io"
            )
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
            allowedHeaders = listOf("*")
            allowCredentials = true // Using jwt via Authorization header
        }

        val source = UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", configuration)
        }

        return source
    }
}