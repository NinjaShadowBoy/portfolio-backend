package com.ninjashadowboy.portfolio.controllers

import com.ninjashadowboy.portfolio.config.JwtService
import com.ninjashadowboy.portfolio.dtos.LoginRequest
import com.ninjashadowboy.portfolio.dtos.LoginResponse
import com.ninjashadowboy.portfolio.entities.User
import com.ninjashadowboy.portfolio.MyProfiler
import com.ninjashadowboy.portfolio.dtos.UserRegistrationDto
import com.ninjashadowboy.portfolio.services.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
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

/**
 * REST API controller for authentication operations.
 * 
 * This controller handles user authentication and registration:
 * - User login with email and password
 * - New user registration
 * - JWT token generation and management
 * 
 * All endpoints in this controller are public (no authentication required).
 * 
 * @author NinjaShadowBoy
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(
    name = "Authentication",
    description = """
        Authentication and user management endpoints.
        
        **Features:**
        - User login with JWT token generation
        - New user registration
        - Secure password handling
        - Token-based authentication
        
        **Security:**
        - Passwords are hashed using BCrypt
        - JWT tokens expire after 24 hours
        - Failed login attempts may result in temporary account lockout
        
        **Note:** These endpoints do not require authentication (they are publicly accessible).
    """
)
internal class AuthApi(
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
    private val userService: UserService,
) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @Operation(
        summary = "User login",
        description = """
            Authenticates a user with email and password credentials.
            
            **Process:**
            1. Validates the provided email and password
            2. Authenticates against the user database
            3. Generates a JWT access token valid for 24 hours
            4. Returns user information and token
            
            **Security:**
            - Password is verified using BCrypt hashing
            - Multiple failed attempts may temporarily lock the account
            - Tokens are signed with HS256 algorithm
            
            **Token Usage:**
            Include the returned token in subsequent requests:
            ```
            Authorization: Bearer <token>
            ```
            
            **Performance:**
            - Average response time: <100ms
            - Includes profiling metrics for authentication steps
        """,
        security = [] // Override global security to indicate this endpoint is public
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Login successful - JWT token and user information returned",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = LoginResponse::class),
                        examples = [
                            ExampleObject(
                                name = "Successful Login",
                                description = "Example of a successful login response with JWT token",
                                value = """
                                {
                                    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNjk4MzI0ODAwLCJleHAiOjE2OTg0MTEyMDB9.signature",
                                    "user": {
                                        "id": 1,
                                        "email": "user@example.com",
                                        "name": "John Doe",
                                        "role": "USER",
                                        "createdAt": "2025-01-15T10:30:00",
                                        "lastLoginAt": "2025-10-30T14:25:30"
                                    },
                                    "expiresIn": 86400000
                                }
                                """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Authentication failed - Invalid credentials or account locked",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "Invalid Credentials",
                                description = "Response when email or password is incorrect",
                                value = """
                                {
                                    "timestamp": "2025-10-30T14:30:00",
                                    "status": 401,
                                    "error": "Unauthorized",
                                    "message": "Invalid email or password",
                                    "path": "/api/v1/auth/login"
                                }
                                """
                            ),
                            ExampleObject(
                                name = "Account Locked",
                                description = "Response when account is temporarily locked due to too many failed attempts",
                                value = """
                                {
                                    "timestamp": "2025-10-30T14:30:00",
                                    "status": 401,
                                    "error": "Unauthorized",
                                    "message": "Account temporarily locked. Try again in 30 minutes.",
                                    "path": "/api/v1/auth/login"
                                }
                                """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request - Invalid input data",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "Validation Error",
                                description = "Response when required fields are missing or invalid",
                                value = """
                                {
                                    "timestamp": "2025-10-30T14:30:00",
                                    "status": 400,
                                    "error": "Bad Request",
                                    "message": "Email cannot be blank",
                                    "path": "/api/v1/auth/login"
                                }
                                """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error - Unexpected error during authentication",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "Server Error",
                                value = """
                                {
                                    "timestamp": "2025-10-30T14:30:00",
                                    "status": 500,
                                    "error": "Internal Server Error",
                                    "message": "An unexpected error occurred",
                                    "path": "/api/v1/auth/login"
                                }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        val result = MyProfiler.profileOperation("Handle login ") {
            val authentication = MyProfiler.profileOperation("Authentication to login") {
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
                MyProfiler.profileOperation("JWT token generation") { jwtService.generateLoginResponse(user) }

            return@profileOperation ResponseEntity.ok(response)
        }

        return result
    }

    @Operation(
        summary = "Register new user",
        description = """
            Creates a new user account in the system.
            
            **Process:**
            1. Validates the registration data (email format, password strength, name)
            2. Checks if email is already registered
            3. Hashes the password using BCrypt
            4. Creates the user account with default USER role
            5. Returns success or conflict status
            
            **Validation Rules:**
            - **Email:** Must be valid email format and unique in the system
            - **Password:** Minimum 8 characters recommended (include letters, numbers, special chars)
            - **Name:** Minimum 2 characters, maximum 100 characters
            
            **Security:**
            - Passwords are never stored in plain text
            - BCrypt hashing with salt for password security
            - New accounts have USER role by default
            
            **After Registration:**
            Use the `/login` endpoint to obtain a JWT token for authentication.
            
            **Rate Limiting:**
            Registration attempts may be rate-limited to prevent abuse (max 5 attempts per hour per IP).
        """,
        security = [] // Public endpoint, no authentication required
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "User created successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "Successful Registration",
                                value = """"User created successfully.""""
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "409",
                description = "Conflict - Email already registered",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "Email Already Exists",
                                description = "Response when trying to register with an already existing email",
                                value = """"User already exists.""""
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request - Invalid registration data",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "Validation Error",
                                description = "Response when validation fails",
                                value = """
                                {
                                    "timestamp": "2025-10-30T14:30:00",
                                    "status": 400,
                                    "error": "Bad Request",
                                    "message": "Email cannot be blank",
                                    "path": "/api/v1/auth/register"
                                }
                                """
                            ),
                            ExampleObject(
                                name = "Invalid Email Format",
                                value = """
                                {
                                    "timestamp": "2025-10-30T14:30:00",
                                    "status": 400,
                                    "error": "Bad Request",
                                    "message": "Invalid email format",
                                    "path": "/api/v1/auth/register"
                                }
                                """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "Server Error",
                                value = """
                                {
                                    "timestamp": "2025-10-30T14:30:00",
                                    "status": 500,
                                    "error": "Internal Server Error",
                                    "message": "An unexpected error occurred during registration",
                                    "path": "/api/v1/auth/register"
                                }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @PostMapping("/register")
    fun register(@RequestBody registerRequest: UserRegistrationDto): ResponseEntity<String> {
        return if(userService.registerUser(registerRequest)) {
            ResponseEntity.status(HttpStatus.CREATED).body("User created successfully.")
        } else {
            ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists.")
        }
    }

}
