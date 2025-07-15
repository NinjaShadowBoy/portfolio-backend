package com.ninjashadowboy.portfolio.entities

import jakarta.persistence.*


@Entity
@Table(name = "projects")
data class Project(
    val name: String,
    val description: String,

    @ElementCollection(fetch = FetchType.EAGER) @Column(name = "technology") @CollectionTable(
        name = "project_technologies",
        joinColumns = [JoinColumn(name = "project_id")],
    ) val technologies: Set<String>,

    val githubLink: String?,
    val challenges: String?,
    val whatILearned: String?,
    val featured: Boolean,

    @OneToMany(
        fetch = FetchType.EAGER,
        cascade = [(CascadeType.ALL)],
        orphanRemoval = true,
        mappedBy = "project",
    ) val photos: List<Photo> = emptyList(),

    @OneToMany(
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        mappedBy = "project",
    ) val ratings: List<Rating> = emptyList(),
) : BaseEntity() {

    @get:Transient
    val averageRating: Float
        get() = ratings.map { it.rating }.average().toFloat()

    @get:Transient
    val totalRatings: Int
        get() = ratings.size
}