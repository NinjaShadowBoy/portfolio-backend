package com.ninjashadowboy.portfolio.repositories

import com.ninjashadowboy.portfolio.entities.ContactMessage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ContactMessageRepository : JpaRepository<ContactMessage, Long> {
    fun findAllByOrderBySubmittedAtDesc(): List<ContactMessage>
    fun findByIsReadFalse(): List<ContactMessage>
}
