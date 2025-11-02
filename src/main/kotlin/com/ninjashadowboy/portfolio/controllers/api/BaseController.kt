package com.ninjashadowboy.portfolio.controllers.api

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Base controller providing common functionality for all API controllers.
 */
abstract class BaseController {
    
    /**
     * Logger instance for the implementing controller.
     */
    protected val log: Logger = LoggerFactory.getLogger(this::class.java)
    
    /**
     * Logs a request at debug level.
     */
    protected fun logRequest(operation: String, params: Map<String, Any> = emptyMap()) {
        if (params.isEmpty()) {
            log.debug("Request: {}", operation)
        } else {
            log.debug("Request: {} with params: {}", operation, params)
        }
    }
    
    /**
     * Logs a successful response at debug level.
     */
    protected fun logResponse(operation: String, result: Any? = null) {
        log.debug("Response: {} completed successfully", operation)
    }
}
