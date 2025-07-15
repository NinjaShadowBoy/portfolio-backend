package com.ninjashadowboy.portfolio.entities

import jakarta.persistence.*
import java.time.LocalDateTime
import kotlin.jvm.javaClass

@MappedSuperclass
abstract class BaseEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0L,

    @Column(nullable = false, updatable = false) val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false) var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BaseEntity

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
