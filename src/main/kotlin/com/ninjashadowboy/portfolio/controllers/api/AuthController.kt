package com.ninjashadowboy.portfolio.controllers.api

import com.ninjashadowboy.portfolio.config.JwtService
import com.ninjashadowboy.portfolio.controllers.docs.AuthApiDocs
import com.ninjashadowboy.portfolio.dtos.LoginRequest
import com.ninjashadowboy.portfolio.dtos.LoginResponse
import com.ninjashadowboy.portfolio.dtos.UserRegistrationDto
import com.ninjashadowboy.portfolio.entities.User
import com.ninjashadowboy.portfolio.services.UserService
import com.ninjashadowboy.portfolio.utils.ResponseUtils
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

/**
 * REST API controller for authentication operations.
 * 
 * Handles user authentication and registration with JWT token generation.
 * All endpoint documentation is centralized in [AuthEndpointDocs].
 */
@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
    private val userService: UserService,
) : BaseController(), AuthApiDocs {

    @Operation(
        summary = "User login",
        description = """
            Authenticates a user with email and password credentials and returns a JWT token.
            
            **Process:**
            1. Validates the provided email and password
            2. Authenticates against the user database
            3. Generates a JWT access token valid for 24 hours
            4. Returns user information and token
            
            **Token Usage:**
            Include the returned token in subsequent requests:
            ```
            Authorization: Bearer <token>
            ```
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Login successful - JWT token and user information returned",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = LoginResponse::class),
                    examples = [ExampleObject(
                        name = "Successful Login",
                        description = "Example of a successful login response with JWT token",
                        value = """
                        {
                            "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                            "user": {
                                "id": 1,
                                "email": "user@example.com",
                                "name": "John Doe",
                                "role": "USER"
                            },
                            "expiresIn": 86400000
                        }
                        """
                    )]
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Authentication failed - Invalid credentials",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        name = "Invalid Credentials",
                        value = """
                        {
                            "timestamp": "2025-10-30T14:30:00",
                            "status": 401,
                            "error": "Unauthorized",
                            "message": "Invalid email or password"
                        }
                        """
                    )]
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request - Invalid input data",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        logRequest("login", mapOf("email" to request.email))
        
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.email, request.password)
        )
        
        SecurityContextHolder.getContext().authentication = authentication
        
        val user = authentication.principal as? User
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        
        val response = jwtService.generateLoginResponse(user)
        
        logResponse("login")
        return ResponseUtils.ok(response)
    }

    @Operation(
        summary = "Register new user",
        description = """
            Creates a new user account with the provided credentials.
            
            **Validation Rules:**
            - **Email:** Must be valid email format and unique in the system
            - **Password:** Minimum 8 characters recommended
            - **Name:** Minimum 2 characters, maximum 100 characters
            
            **After Registration:**
            Use the `/login` endpoint to obtain a JWT token for authentication.
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "User created successfully",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        name = "Successful Registration",
                        value = """{"message": "User created successfully"}"""
                    )]
                )]
            ),
            ApiResponse(
                responseCode = "409",
                description = "Conflict - Email already registered",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        name = "Email Already Exists",
                        value = """{"message": "User already exists"}"""
                    )]
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request - Invalid registration data",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    @PostMapping("/register")
    fun register(@RequestBody request: UserRegistrationDto): ResponseEntity<Map<String, String>> {
        logRequest("register", mapOf("email" to request.email))
        
        val created = userService.registerUser(request)
        
        return if (created) {
            logResponse("register")
            ResponseUtils.message("User created successfully", HttpStatus.CREATED)
        } else {
            ResponseUtils.message("User already exists", HttpStatus.CONFLICT)
        }
    }
}
