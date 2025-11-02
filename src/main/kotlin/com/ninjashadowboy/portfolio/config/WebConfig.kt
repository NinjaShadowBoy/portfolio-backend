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
            .allowedOrigins("http://localhost:3000", "http://localhost:4200",
            "https://ninjashadowboy.github.io") // Frontend URL
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
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