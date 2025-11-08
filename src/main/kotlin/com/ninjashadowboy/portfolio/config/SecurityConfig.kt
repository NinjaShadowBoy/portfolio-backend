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
        http.csrf { it.disable() }
            .cors { it }
            .authorizeHttpRequests { auth ->
                auth
                    // Allow all OPTIONS requests for CORS preflight
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    // Public authentication endpoints
                    .requestMatchers("/api/v1/auth/**", "/oauth2/**").permitAll()
                    // Static resources
                    .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                    // File uploads
                    .requestMatchers("/uploads/**").permitAll()
                    // API documentation
                    .requestMatchers("/swagger-ui", "/swagger-ui/**", "/api-docs", "/api-docs/**").permitAll()
                    // Public GET endpoints
                    .requestMatchers(HttpMethod.GET, "/api/v1/projects", "/api/v1/projects/**").permitAll()
                    // Contact form - public POST endpoint
                    .requestMatchers(HttpMethod.POST, "/api/v1/contact").permitAll()
                    // Admin-only endpoints
                    .requestMatchers("/api/v1/photo/**").hasAuthority("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/v1/contact/**").hasAuthority("ADMIN")
                    .requestMatchers(HttpMethod.PATCH, "/api/v1/contact/**").hasAuthority("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/contact/**").hasAuthority("ADMIN")
                    // All other requests require authentication
                    .anyRequest().authenticated()
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authenticationProvider(authenticationProvider)
            .formLogin { formLogin -> formLogin.disable() }
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
            // Allow multiple origins for different environments
            allowedOriginPatterns = listOf(
                "http://localhost:*",
                "https://ninjashadowboy.github.io",
                "https://*.onrender.com",  // For Render deployments
                "https://*.vercel.app",    // For Vercel deployments
                "https://*.netlify.app"    // For Netlify deployments
            )
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
            allowedHeaders = listOf("*")
            exposedHeaders = listOf("Authorization", "Content-Type")
            allowCredentials = true // Using jwt via Authorization header
            maxAge = 3600L // Cache preflight response for 1 hour
        }

        val source = UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", configuration)
        }

        return source
    }
}