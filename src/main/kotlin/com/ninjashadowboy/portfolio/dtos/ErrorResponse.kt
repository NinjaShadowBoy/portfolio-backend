package com.ninjashadowboy.portfolio.dtos

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

/**
 * Standard error response structure for API errors.
 * 
 * This DTO provides consistent error information across all API endpoints.
 * It follows Spring Boot's default error response format with additional
 * context for better debugging and user experience.
 * 
 * @property timestamp When the error occurred
 * @property status HTTP status code
 * @property error HTTP status text (e.g., "Bad Request", "Not Found")
 * @property message Detailed error message explaining what went wrong
 * @property path The request path that caused the error
 * @property fieldErrors Optional field-level validation errors (for 400 responses)
 */
@Schema(
    description = "Standard error response structure returned by the API when an error occurs"
)
data class ErrorResponse(
    @Schema(
        description = "Timestamp when the error occurred",
        example = "2025-10-30T14:30:00",
        required = true,
        format = "date-time"
    )
    val timestamp: LocalDateTime,
    
    @Schema(
        description = "HTTP status code",
        example = "400",
        required = true
    )
    val status: Int,
    
    @Schema(
        description = "HTTP status text description",
        example = "Bad Request",
        required = true
    )
    val error: String,
    
    @Schema(
        description = "Detailed error message explaining what went wrong",
        example = "Email cannot be blank",
        required = true
    )
    val message: String,
    
    @Schema(
        description = "The request path that caused the error",
        example = "/api/v1/auth/login",
        required = true
    )
    val path: String,
    
    @Schema(
        description = "Field-level validation errors (present for validation failures)",
        required = false,
        nullable = true
    )
    val fieldErrors: Map<String, String>? = null
)

/**
 * Field validation error details.
 * 
 * Used in error responses when request validation fails.
 * Provides specific information about which fields failed validation and why.
 * 
 * @property field Name of the field that failed validation
 * @property message Validation error message for this field
 * @property rejectedValue The value that was rejected (may be null)
 */
@Schema(
    description = "Detailed information about a field validation error"
)
data class FieldValidationError(
    @Schema(
        description = "Name of the field that failed validation",
        example = "email",
        required = true
    )
    val field: String,
    
    @Schema(
        description = "Validation error message",
        example = "must be a well-formed email address",
        required = true
    )
    val message: String,
    
    @Schema(
        description = "The value that was rejected (may be null or omitted for security)",
        example = "invalid-email",
        required = false,
        nullable = true
    )
    val rejectedValue: Any? = null
)
