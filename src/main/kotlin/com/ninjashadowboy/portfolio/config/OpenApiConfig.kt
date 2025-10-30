package com.ninjashadowboy.portfolio.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * OpenAPI/Swagger Configuration for Portfolio API
 * 
 * This configuration class sets up comprehensive API documentation using SpringDoc OpenAPI 3.
 * It defines:
 * - API metadata (title, version, description, contact info)
 * - Security schemes (JWT Bearer authentication)
 * - Server configurations for different environments
 * - Global security requirements
 * 
 * The Swagger UI is accessible at: /swagger-ui/index.html
 * The OpenAPI JSON specification is available at: /api-docs
 * 
 * @author NinjaShadowBoy
 * @version 1.0
 * @since 1.0
 */
@Configuration
class OpenApiConfig {

    @Value("\${springdoc.info.title:Portfolio API}")
    private lateinit var title: String

    @Value("\${springdoc.info.description:API for managing personal portfolio projects, photos, and user authentication}")
    private lateinit var description: String

    @Value("\${springdoc.info.version:1.0.0}")
    private lateinit var version: String

    @Value("\${springdoc.info.contact.name:NinjaShadowBoy}")
    private lateinit var contactName: String

    @Value("\${springdoc.info.contact.email:admin@portfolio.com}")
    private lateinit var contactEmail: String

    @Value("\${server.port:8080}")
    private lateinit var serverPort: String

    /**
     * Configures the OpenAPI specification with detailed API information.
     * 
     * This bean creates a comprehensive OpenAPI configuration including:
     * - API information (title, description, version, license)
     * - Contact information for API support
     * - Server definitions for local development and production
     * - JWT Bearer token security scheme
     * - Global security requirements for protected endpoints
     * 
     * @return OpenAPI configuration object
     */
    @Bean
    fun customOpenAPI(): OpenAPI {
        val securitySchemeName = "bearerAuth"
        
        return OpenAPI()
            .info(
                Info()
                    .title(title)
                    .description(
                        """
                        # Portfolio Backend API
                        
                        This is a comprehensive REST API for managing a personal portfolio website.
                        
                        ## Features
                        
                        - **Authentication & Authorization**: Secure user registration and login with JWT tokens
                        - **Project Management**: CRUD operations for portfolio projects
                        - **Photo Management**: Upload and manage project and profile photos
                        - **User Management**: User profile and account management
                        
                        ## Authentication
                        
                        Most endpoints require authentication using JWT Bearer tokens. To authenticate:
                        
                        1. Register a new account using `POST /api/v1/auth/register`
                        2. Login using `POST /api/v1/auth/login` to receive a JWT token
                        3. Include the token in the `Authorization` header as `Bearer <token>`
                        4. The token is valid for 24 hours by default
                        
                        ## Rate Limiting
                        
                        API requests may be rate-limited to prevent abuse. Current limits:
                        - Authenticated users: 1000 requests per hour
                        - Unauthenticated users: 100 requests per hour
                        
                        ## Error Handling
                        
                        The API uses standard HTTP status codes:
                        - `2xx` - Success
                        - `4xx` - Client errors (invalid request, unauthorized, not found, etc.)
                        - `5xx` - Server errors
                        
                        All error responses include a descriptive message to help with debugging.
                        
                        ## API Versioning
                        
                        The API is versioned through the URL path (`/api/v1/`). Breaking changes will 
                        result in a new version (`/api/v2/`), while the old version will be maintained 
                        for backward compatibility.
                        
                        """.trimIndent()
                    )
                    .version(version)
                    .contact(
                        Contact()
                            .name(contactName)
                            .email(contactEmail)
                            .url("https://github.com/NinjaShadowBoy")
                    )
                    .license(
                        License()
                            .name("MIT License")
                            .url("https://opensource.org/licenses/MIT")
                    )
            )
            .servers(
                listOf(
                    Server()
                        .url("http://localhost:$serverPort")
                        .description("Local Development Server"),
                    Server()
                        .url("https://api.portfolio.com")
                        .description("Production Server")
                )
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        securitySchemeName,
                        SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .description(
                                """
                                JWT Bearer token authentication.
                                
                                **How to use:**
                                1. Obtain a JWT token by calling `/api/v1/auth/login`
                                2. Click the 'Authorize' button below
                                3. Enter: `Bearer <your-token>` (the word 'Bearer' followed by a space and your token)
                                4. Click 'Authorize' and then 'Close'
                                5. All subsequent requests will include the Authorization header
                                
                                **Token Format:**
                                ```
                                Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
                                ```
                                
                                **Note:** Tokens expire after 24 hours. You'll need to login again to get a new token.
                                """.trimIndent()
                            )
                    )
            )
            .addSecurityItem(
                SecurityRequirement()
                    .addList(securitySchemeName)
            )
    }
}
