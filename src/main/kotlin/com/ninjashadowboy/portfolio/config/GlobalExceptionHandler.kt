package com.ninjashadowboy.portfolio.config

import com.ninjashadowboy.portfolio.exceptions.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import java.io.IOException
import java.time.Instant

/**
 * Global exception handler for the Portfolio API.
 *
 * This class provides centralized exception handling for all controllers, ensuring consistent error
 * responses across the application.
 *
 * Features:
 * - Comprehensive exception coverage
 * - Structured error responses
 * - Proper HTTP status codes
 * - Detailed logging for debugging
 * - Security-aware error messages
 * - Validation error handling
 * - File upload error handling
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    private val log: Logger = LoggerFactory.getLogger(javaClass)


    // ─── CUSTOM EXCEPTIONS ─────────────────────────────────────────────────────────

    @ExceptionHandler(com.ninjashadowboy.portfolio.exceptions.ResourceNotFoundException::class)
    fun handleResourceNotFoundException(
        ex: com.ninjashadowboy.portfolio.exceptions.ResourceNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        log.warn("Resource not found: {} - {}", ex.message, request.requestURI)

        val errorResponse = ApiErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            error = "Resource Not Found",
            code = ex.errorCode,
            message = ex.message ?: "The requested resource was not found",
            path = request.requestURI,
            timestamp = Instant.now()
        )

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(com.ninjashadowboy.portfolio.exceptions.ResourceAlreadyExistsException::class)
    fun handleResourceAlreadyExistsException(
        ex: com.ninjashadowboy.portfolio.exceptions.ResourceAlreadyExistsException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        log.warn("Resource already exists: {} - {}", ex.message, request.requestURI)

        val errorResponse = ApiErrorResponse(
            status = HttpStatus.CONFLICT.value(),
            error = "Resource Already Exists",
            code = ex.errorCode,
            message = ex.message ?: "The resource already exists",
            path = request.requestURI,
            timestamp = Instant.now()
        )

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse)
    }

    @ExceptionHandler(com.ninjashadowboy.portfolio.exceptions.BusinessRuleViolationException::class)
    fun handleBusinessRuleViolationException(
        ex: com.ninjashadowboy.portfolio.exceptions.BusinessRuleViolationException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        log.warn("Business rule violation: {} - {}", ex.message, request.requestURI)

        val errorResponse = ApiErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Business Rule Violation",
            code = ex.errorCode,
            message = ex.message ?: "A business rule was violated",
            path = request.requestURI,
            timestamp = Instant.now()
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(com.ninjashadowboy.portfolio.exceptions.FileProcessingException::class)
    fun handleFileProcessingException(
        ex: com.ninjashadowboy.portfolio.exceptions.FileProcessingException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        log.error("File processing error: {} - {}", ex.message, request.requestURI, ex)

        val errorResponse = ApiErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "File Processing Error",
            code = ex.errorCode,
            message = ex.message ?: "Failed to process file",
            path = request.requestURI,
            timestamp = Instant.now()
        )

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }

    @ExceptionHandler(com.ninjashadowboy.portfolio.exceptions.InsufficientPermissionException::class)
    fun handleInsufficientPermissionException(
        ex: com.ninjashadowboy.portfolio.exceptions.InsufficientPermissionException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        log.warn("Insufficient permission: {} - {}", ex.message, request.requestURI)

        val errorResponse = ApiErrorResponse(
            status = HttpStatus.FORBIDDEN.value(),
            error = "Forbidden",
            code = ex.errorCode,
            message = ex.message ?: "You don't have permission to perform this operation",
            path = request.requestURI,
            timestamp = Instant.now()
        )

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse)
    }

    @ExceptionHandler(com.ninjashadowboy.portfolio.exceptions.ValidationException::class)
    fun handleCustomValidationException(
        ex: com.ninjashadowboy.portfolio.exceptions.ValidationException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        log.warn("Validation error: {} - {}", ex.message, request.requestURI)

        val fieldErrors = ex.fieldErrors.map { (field, message) ->
            FieldErrorDetail(field = field, message = message)
        }

        val errorResponse = ApiErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Validation Error",
            code = ex.errorCode,
            message = ex.message ?: "Validation failed",
            fieldErrors = if (fieldErrors.isNotEmpty()) fieldErrors else null,
            path = request.requestURI,
            timestamp = Instant.now()
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(com.ninjashadowboy.portfolio.exceptions.DuplicateActionException::class)
    fun handleDuplicateActionException(
        ex: com.ninjashadowboy.portfolio.exceptions.DuplicateActionException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        log.warn("Duplicate action: {} - {}", ex.message, request.requestURI)

        val errorResponse = ApiErrorResponse(
            status = HttpStatus.CONFLICT.value(),
            error = "Duplicate Action",
            code = ex.errorCode,
            message = ex.message ?: "This action has already been performed",
            path = request.requestURI,
            timestamp = Instant.now()
        )

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse)
    }

    // ─── SPRING FRAMEWORK EXCEPTIONS ───────────────────────────────────────────────

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(
        ex: NoSuchElementException, request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        log.warn("Resource not found: {} - {}", ex.message, request.requestURI)

        val errorResponse = ApiErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            error = "Not Found",
            code = "RESOURCE_NOT_FOUND",
            message = "The requested resource was not found",
            debugMessage = ex.message,
            path = request.requestURI,
            timestamp = Instant.now()
        )

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException, request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        val fieldErrors = ex.bindingResult.fieldErrors.map { fieldError ->
            FieldErrorDetail(
                field = fieldError.field,
                message = fieldError.defaultMessage ?: "Invalid value",
                rejectedValue = fieldError.rejectedValue?.toString()
            )
        }

        log.warn(
            "Validation failed: {} fields with errors - {}", fieldErrors.size, request.requestURI
        )

        val errorResponse = ApiErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Validation Failed",
            code = "VALIDATION_ERROR",
            message = "Request validation failed",
            fieldErrors = fieldErrors,
            path = request.requestURI,
            timestamp = Instant.now()
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(
        ex: ConstraintViolationException, request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        val fieldErrors = ex.constraintViolations.map { violation ->
            FieldErrorDetail(
                field = violation.propertyPath.toString(),
                message = violation.message,
                rejectedValue = violation.invalidValue?.toString()
            )
        }

        log.warn(
            "Constraint violation: {} violations - {}", fieldErrors.size, request.requestURI
        )

        val errorResponse = ApiErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Validation Failed",
            code = "CONSTRAINT_VIOLATION",
            message = "Request validation failed",
            fieldErrors = fieldErrors,
            path = request.requestURI,
            timestamp = Instant.now()
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(
        ex: HttpMessageNotReadableException, request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        log.warn("Invalid request body: {} - {}", ex.message, request.requestURI)

        val errorResponse = ApiErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Bad Request",
            code = "INVALID_REQUEST_BODY",
            message = "The request body is invalid or malformed",
            debugMessage = ex.message,
            path = request.requestURI,
            timestamp = Instant.now()
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingServletRequestParameterException(
        ex: MissingServletRequestParameterException, request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        log.warn("Missing parameter: {} - {}", ex.message, request.requestURI)

        val errorResponse = ApiErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Bad Request",
            code = "MISSING_PARAMETER",
            message = "Required parameter '${ex.parameterName}' is missing",
            path = request.requestURI,
            timestamp = Instant.now()
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatchException(
        ex: MethodArgumentTypeMismatchException, request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        log.warn("Type mismatch: {} - {}", ex.message, request.requestURI)

        val errorResponse = ApiErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Bad Request",
            code = "TYPE_MISMATCH",
            message = "Parameter '${ex.name}' should be of type ${ex.requiredType?.simpleName}",
            path = request.requestURI,
            timestamp = Instant.now()
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleHttpRequestMethodNotSupportedException(
        ex: HttpRequestMethodNotSupportedException, request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        log.warn("Method not supported: {} - {}", ex.message, request.requestURI)

        val errorResponse = ApiErrorResponse(
            status = HttpStatus.METHOD_NOT_ALLOWED.value(),
            error = "Method Not Allowed",
            code = "METHOD_NOT_SUPPORTED",
            message = "HTTP method '${ex.method}' is not supported for this endpoint",
            path = request.requestURI,
            timestamp = Instant.now()
        )

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse)
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun handleHttpMediaTypeNotSupportedException(
        ex: HttpMediaTypeNotSupportedException, request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        log.warn("Media type not supported: {} - {}", ex.message, request.requestURI)

        val errorResponse = ApiErrorResponse(
            status = HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
            error = "Unsupported Media Type",
            code = "MEDIA_TYPE_NOT_SUPPORTED",
            message = "Media type '${ex.contentType}' is not supported",
            path = request.requestURI,
            timestamp = Instant.now()
        )

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(errorResponse)
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFoundException(
        ex: NoHandlerFoundException, request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        log.warn("No handler found: {} - {}", ex.message, request.requestURI)

        val errorResponse = ApiErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            error = "Not Found",
            code = "ENDPOINT_NOT_FOUND",
            message = "The requested endpoint was not found",
            path = request.requestURI,
            timestamp = Instant.now()
        )

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    // ─── SECURITY EXCEPTIONS ───────────────────────────────────────────────────────

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(
        ex: AuthenticationException, request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        log.warn("Authentication failed: {} - {}", ex.message, request.requestURI)

        val errorResponse = ApiErrorResponse(
            status = HttpStatus.UNAUTHORIZED.value(),
            error = "Unauthorized",
            code = "AUTHENTICATION_FAILED",
            message = "Authentication failed",
            debugMessage = ex.message,
            path = request.requestURI,
            timestamp = Instant.now()
        )

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(
        ex: BadCredentialsException, request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        log.warn("Bad credentials: {} - {}", ex.message, request.requestURI)

        val errorResponse = ApiErrorResponse(
            status = HttpStatus.UNAUTHORIZED.value(),
            error = "Unauthorized",
            code = "BAD_CREDENTIALS",
            message = "Invalid username or password",
            path = request.requestURI,
            timestamp = Instant.now()
        )

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(
        ex: AccessDeniedException, request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        log.warn("Access denied: {} - {}", ex.message, request.requestURI)

        val errorResponse = ApiErrorResponse(
            status = HttpStatus.FORBIDDEN.value(),
            error = "Forbidden",
            code = "ACCESS_DENIED",
            message = "You don't have permission to access this resource",
            path = request.requestURI,
            timestamp = Instant.now()
        )

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse)
    }

    @ExceptionHandler(IOException::class)
    fun handleIOException(
        ex: IOException, request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        log.error("IO error occurred: {} - {}", ex.message, request.requestURI, ex)

        val errorResponse = ApiErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Internal Server Error",
            code = "IO_ERROR",
            message = "An error occurred while processing the request",
            debugMessage = ex.message,
            path = request.requestURI,
            timestamp = Instant.now()
        )

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }

    // ─── DATABASE EXCEPTIONS ───────────────────────────────────────────────────────

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolationException(
        ex: DataIntegrityViolationException, request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        log.error("Data integrity violation: {} - {}", ex.message, request.requestURI, ex)

        val errorResponse = ApiErrorResponse(
            status = HttpStatus.CONFLICT.value(),
            error = "Conflict",
            code = "DATA_INTEGRITY_VIOLATION",
            message = "The operation conflicts with existing data",
            debugMessage = ex.message,
            path = request.requestURI,
            timestamp = Instant.now()
        )

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse)
    }

    // ─── GENERIC EXCEPTION HANDLER ────────────────────────────────────────────────

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception, request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        log.error("Unexpected error occurred: {} - {}", ex.message, request.requestURI, ex)

        val errorResponse = ApiErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Internal Server Error",
            code = "INTERNAL_ERROR",
            message = "An unexpected error occurred",
            debugMessage = ex.message,
            path = request.requestURI,
            timestamp = Instant.now()
        )

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }
}

/**
 * Structured error response for API endpoints.
 */
data class ApiErrorResponse(
    val timestamp: Instant = Instant.now(),
    val status: Int,
    val error: String,
    val code: String? = null,
    val message: String,
    val debugMessage: String? = null,
    val fieldErrors: List<FieldErrorDetail>? = null,
    val path: String? = null
)

/**
 * Details about validation errors for specific fields.
 */
data class FieldErrorDetail(
    val field: String,
    val message: String,
    val rejectedValue: String? = null
)
