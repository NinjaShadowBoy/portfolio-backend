package com.ninjashadowboy.portfolio.entities

import jakarta.persistence.CascadeType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "ratings")
class Rating(
    var rating: Int,
    var comment: String?,

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    val user: User,

    @JoinColumn(name = "project_id")
    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val project: Project
) : BaseEntity() {
}