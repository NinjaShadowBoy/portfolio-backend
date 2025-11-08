package com.ninjashadowboy.portfolio.entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "contact_messages")
data class ContactMessage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val email: String,

    @Column(nullable = false, length = 2000)
    val message: String,

    @Column(nullable = false)
    val submittedAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var isRead: Boolean = false,

    @Column(nullable = false)
    var isReplied: Boolean = false
)
