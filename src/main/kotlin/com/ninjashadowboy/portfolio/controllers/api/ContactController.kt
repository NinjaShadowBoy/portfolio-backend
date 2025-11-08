package com.ninjashadowboy.portfolio.controllers.api

import com.ninjashadowboy.portfolio.dtos.ContactDto
import com.ninjashadowboy.portfolio.dtos.ContactMessageResponse
import com.ninjashadowboy.portfolio.services.ContactService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/contact")
@Tag(name = "Contact", description = "Contact form and message management APIs")
class ContactController(
    private val contactService: ContactService
) {
    private val logger = LoggerFactory.getLogger(ContactController::class.java)

    @PostMapping
    @Operation(summary = "Submit contact form", description = "Submit a contact form message (public endpoint)")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Message submitted successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input data")
        ]
    )
    fun submitContactForm(
        @Valid @RequestBody contactDto: ContactDto
    ): ResponseEntity<Map<String, Any>> {
        logger.info("Received contact form submission from: ${contactDto.email}")

        return try {
            val response = contactService.submitContactForm(contactDto)
            ResponseEntity.status(HttpStatus.CREATED).body(
                mapOf(
                    "success" to true,
                    "message" to "Thank you for your message! I'll get back to you soon.",
                    "data" to response
                )
            )
        } catch (e: Exception) {
            logger.error("Error processing contact form: ${e.message}", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                mapOf(
                    "success" to false,
                    "message" to "Failed to submit contact form. Please try again later."
                )
            )
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all contact messages", description = "Get all contact messages (admin only)")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved messages"),
            ApiResponse(responseCode = "403", description = "Access denied")
        ]
    )
    fun getAllMessages(): ResponseEntity<List<ContactMessageResponse>> {
        val messages = contactService.getAllMessages()
        return ResponseEntity.ok(messages)
    }

    @GetMapping("/unread")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get unread messages", description = "Get all unread contact messages (admin only)")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved unread messages"),
            ApiResponse(responseCode = "403", description = "Access denied")
        ]
    )
    fun getUnreadMessages(): ResponseEntity<List<ContactMessageResponse>> {
        val messages = contactService.getUnreadMessages()
        return ResponseEntity.ok(messages)
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mark message as read", description = "Mark a contact message as read (admin only)")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Message marked as read"),
            ApiResponse(responseCode = "404", description = "Message not found"),
            ApiResponse(responseCode = "403", description = "Access denied")
        ]
    )
    fun markAsRead(@PathVariable id: Long): ResponseEntity<ContactMessageResponse> {
        val message = contactService.markAsRead(id)
        return ResponseEntity.ok(message)
    }

    @PatchMapping("/{id}/replied")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mark message as replied", description = "Mark a contact message as replied (admin only)")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Message marked as replied"),
            ApiResponse(responseCode = "404", description = "Message not found"),
            ApiResponse(responseCode = "403", description = "Access denied")
        ]
    )
    fun markAsReplied(@PathVariable id: Long): ResponseEntity<ContactMessageResponse> {
        val message = contactService.markAsReplied(id)
        return ResponseEntity.ok(message)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete contact message", description = "Delete a contact message (admin only)")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Message deleted successfully"),
            ApiResponse(responseCode = "404", description = "Message not found"),
            ApiResponse(responseCode = "403", description = "Access denied")
        ]
    )
    fun deleteMessage(@PathVariable id: Long): ResponseEntity<Void> {
        contactService.deleteMessage(id)
        return ResponseEntity.noContent().build()
    }
}
