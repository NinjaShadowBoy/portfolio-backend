package com.ninjashadowboy.portfolio.entities;

import jakarta.persistence.*

@Entity
@Table(name = "photos")
data class Photo(
    var photoUrl: String = "",

    @JoinColumn @ManyToOne(
        fetch = FetchType.LAZY,
    ) val project: Project? = null,
) : BaseEntity()

