package com.ninjashadowboy.portfolio.services

import com.ninjashadowboy.portfolio.dtos.ContactDto
import com.ninjashadowboy.portfolio.dtos.ContactMessageResponse
import com.ninjashadowboy.portfolio.entities.ContactMessage
import com.ninjashadowboy.portfolio.repositories.ContactMessageRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ContactService(
    private val contactMessageRepository: ContactMessageRepository,
    private val emailService: EmailService
) {
    private val logger = LoggerFactory.getLogger(ContactService::class.java)

    @Transactional
    fun submitContactForm(contactDto: ContactDto): ContactMessageResponse {
        logger.info("Processing contact form submission from ${contactDto.email}")

        // Save message to database
        val contactMessage = ContactMessage(
            name = contactDto.name,
            email = contactDto.email,
            message = contactDto.message
        )

        val savedMessage = contactMessageRepository.save(contactMessage)
        logger.info("Contact message saved with ID: ${savedMessage.id}")

        // Send email notification to admin
        try {
            emailService.sendContactFormNotification(
                contactDto.name,
                contactDto.email,
                contactDto.message
            )
        } catch (e: Exception) {
            logger.error("Failed to send email notification, but message was saved", e)
            // Continue - message is saved even if email fails
        }

        // Send confirmation email to sender
        try {
            emailService.sendContactConfirmation(contactDto.name, contactDto.email)
        } catch (e: Exception) {
            logger.error("Failed to send confirmation email to sender", e)
            // Continue - this is not critical
        }

        return mapToResponse(savedMessage)
    }

    fun getAllMessages(): List<ContactMessageResponse> {
        return contactMessageRepository.findAllByOrderBySubmittedAtDesc()
            .map { mapToResponse(it) }
    }

    fun getUnreadMessages(): List<ContactMessageResponse> {
        return contactMessageRepository.findByIsReadFalse()
            .map { mapToResponse(it) }
    }

    @Transactional
    fun markAsRead(id: Long): ContactMessageResponse {
        val message = contactMessageRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Contact message not found with id: $id") }

        message.isRead = true
        val updatedMessage = contactMessageRepository.save(message)

        return mapToResponse(updatedMessage)
    }

    @Transactional
    fun markAsReplied(id: Long): ContactMessageResponse {
        val message = contactMessageRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Contact message not found with id: $id") }

        message.isReplied = true
        message.isRead = true
        val updatedMessage = contactMessageRepository.save(message)

        return mapToResponse(updatedMessage)
    }

    @Transactional
    fun deleteMessage(id: Long) {
        contactMessageRepository.deleteById(id)
        logger.info("Deleted contact message with ID: $id")
    }

    private fun mapToResponse(message: ContactMessage): ContactMessageResponse {
        return ContactMessageResponse(
            id = message.id!!,
            name = message.name,
            email = message.email,
            message = message.message,
            submittedAt = message.submittedAt.toString(),
            isRead = message.isRead,
            isReplied = message.isReplied
        )
    }
}
