package com.ninjashadowboy.portfolio.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {
    @Value("\${profile-photo.upload.dir}")
    private lateinit var profilePhotoDir: String

    @Value("\${photo.upload.dir}")
    private lateinit var photoDir: String

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOriginPatterns(
                "http://localhost:*",
                "https://ninjashadowboy.github.io",
                "https://*.onrender.com",
                "https://*.vercel.app",
                "https://*.netlify.app"
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
            .allowedHeaders("*")
            .exposedHeaders("Authorization", "Content-Type")
            .allowCredentials(true)
            .maxAge(3600)
    }

    /**
     * Configure resource handlers for static resources
     */
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        // Profile photos
        registry.addResourceHandler("/${profilePhotoDir}/**")
            .addResourceLocations("file:${profilePhotoDir}/")
        registry.addResourceHandler("/${photoDir}/**")
            .addResourceLocations("file:${photoDir}/")
    }
} 