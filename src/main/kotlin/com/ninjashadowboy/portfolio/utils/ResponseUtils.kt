package com.ninjashadowboy.portfolio.utils

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

/**
 * Utility class for creating standardized API responses.
 */
object ResponseUtils {
    
    /**
     * Creates a success response with HTTP 200 OK.
     */
    fun <T> ok(body: T): ResponseEntity<T> = ResponseEntity.ok(body)
    
    /**
     * Creates a created response with HTTP 201 CREATED.
     */
    fun <T> created(body: T): ResponseEntity<T> = 
        ResponseEntity.status(HttpStatus.CREATED).body(body)
    
    /**
     * Creates a no content response with HTTP 204 NO CONTENT.
     */
    fun noContent(): ResponseEntity<Unit> = 
        ResponseEntity.noContent().build()
    
    /**
     * Creates a response with a custom message.
     */
    fun message(message: String, status: HttpStatus = HttpStatus.OK): ResponseEntity<Map<String, String>> =
        ResponseEntity.status(status).body(mapOf("message" to message))
    
    /**
     * Creates a map with a single key-value pair.
     */
    fun <T> singleValue(key: String, value: T): Map<String, T> = mapOf(key to value)
}

/**
 * Extension function to convert nullable values to NotFound response.
 */
fun <T> T?.orNotFound(resourceName: String, id: Any): T =
    this ?: throw com.ninjashadowboy.portfolio.exceptions.ResourceNotFoundException(resourceName, id)
