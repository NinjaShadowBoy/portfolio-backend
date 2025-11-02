package com.ninjashadowboy.portfolio.exceptions

/**
 * Base exception for all portfolio application exceptions.
 */
sealed class PortfolioException(
    message: String,
    val errorCode: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)

/**
 * Thrown when a requested resource is not found.
 */
class ResourceNotFoundException(
    resourceName: String,
    identifier: Any,
    cause: Throwable? = null
) : PortfolioException(
    message = "$resourceName with identifier '$identifier' not found",
    errorCode = "RESOURCE_NOT_FOUND",
    cause = cause
)

/**
 * Thrown when a resource already exists (duplicate).
 */
class ResourceAlreadyExistsException(
    resourceName: String,
    identifier: String,
    cause: Throwable? = null
) : PortfolioException(
    message = "$resourceName with $identifier already exists",
    errorCode = "RESOURCE_ALREADY_EXISTS",
    cause = cause
)

/**
 * Thrown when a business rule is violated.
 */
class BusinessRuleViolationException(
    rule: String,
    cause: Throwable? = null
) : PortfolioException(
    message = "Business rule violation: $rule",
    errorCode = "BUSINESS_RULE_VIOLATION",
    cause = cause
)

/**
 * Thrown when file upload/processing fails.
 */
class FileProcessingException(
    message: String,
    cause: Throwable? = null
) : PortfolioException(
    message = message,
    errorCode = "FILE_PROCESSING_ERROR",
    cause = cause
)

/**
 * Thrown when user lacks permission for an operation.
 */
class InsufficientPermissionException(
    operation: String,
    cause: Throwable? = null
) : PortfolioException(
    message = "Insufficient permission to perform: $operation",
    errorCode = "INSUFFICIENT_PERMISSION",
    cause = cause
)

/**
 * Thrown when validation fails.
 */
class ValidationException(
    message: String,
    val fieldErrors: Map<String, String> = emptyMap(),
    cause: Throwable? = null
) : PortfolioException(
    message = message,
    errorCode = "VALIDATION_ERROR",
    cause = cause
)

/**
 * Thrown when user has already performed an action (e.g., already rated a project).
 */
class DuplicateActionException(
    action: String,
    resource: String,
    cause: Throwable? = null
) : PortfolioException(
    message = "You have already $action this $resource",
    errorCode = "DUPLICATE_ACTION",
    cause = cause
)
