package com.ninjashadowboy.portfolio.services

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val mailSender: JavaMailSender,
    @Value("\${email.sending.enabled:true}") private val emailEnabled: Boolean,
    @Value("\${email.recipient:admin@portfolio.com}") private val recipientEmail: String,
    @Value("\${spring.mail.username}") private val fromEmail: String
) {
    private val logger = LoggerFactory.getLogger(EmailService::class.java)

    /**
     * Sends a contact form notification to the admin
     */
    fun sendContactFormNotification(name: String, email: String, message: String) {
        if (!emailEnabled) {
            logger.warn("Email sending is disabled. Skipping email notification.")
            return
        }

        try {
            val mailMessage = SimpleMailMessage().apply {
                setFrom(fromEmail)
                setTo(recipientEmail)
                subject = "New Contact Form Submission from $name"
                text = buildContactEmailBody(name, email, message)
            }

            mailSender.send(mailMessage)
            logger.info("Contact form notification sent successfully to $recipientEmail")
        } catch (e: Exception) {
            logger.error("Failed to send contact form notification: ${e.message}", e)
            throw RuntimeException("Failed to send email notification", e)
        }
    }

    /**
     * Sends a confirmation email to the person who submitted the contact form
     */
    fun sendContactConfirmation(name: String, email: String) {
        if (!emailEnabled) {
            logger.warn("Email sending is disabled. Skipping confirmation email.")
            return
        }

        try {
            val mailMessage = SimpleMailMessage().apply {
                setFrom(fromEmail)
                setTo(email)
                subject = "Thank you for contacting me!"
                text = buildConfirmationEmailBody(name)
            }

            mailSender.send(mailMessage)
            logger.info("Confirmation email sent successfully to $email")
        } catch (e: Exception) {
            logger.error("Failed to send confirmation email: ${e.message}", e)
            // Don't throw exception for confirmation email failure
        }
    }

    private fun buildContactEmailBody(name: String, email: String, message: String): String {
        return """
            You have received a new contact form submission.
            
            From: $name
            Email: $email
            
            Message:
            $message
            
            ---
            This email was sent from your portfolio website contact form.
        """.trimIndent()
    }

    private fun buildConfirmationEmailBody(name: String): String {
        return """
            Hi $name,
            
            Thank you for reaching out! I've received your message and will get back to you as soon as possible.
            
            Best regards,
            NinjaShadowBoy
            
            ---
            This is an automated confirmation email.
        """.trimIndent()
    }
}
