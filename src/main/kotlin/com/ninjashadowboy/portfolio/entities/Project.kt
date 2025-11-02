package com.ninjashadowboy.portfolio.entities

import jakarta.persistence.*


@Entity
@Table(name = "projects")
data class Project(
    @Column(nullable = false, length = 500)
    var name: String,
    
    @Column(nullable = false, columnDefinition = "TEXT")
    var description: String,

    @ElementCollection(fetch = FetchType.EAGER) @Column(name = "technology") @CollectionTable(
        name = "project_technologies",
        joinColumns = [JoinColumn(name = "project_id")],
    ) var technologies: Set<String>,

    @Column(length = 1000)
    var githubLink: String?,
    
    @Column(columnDefinition = "TEXT")
    var challenges: String?,
    
    @Column(columnDefinition = "TEXT")
    var whatILearned: String?,
    
    var featured: Boolean,

    @OneToMany(
        fetch = FetchType.EAGER,
        cascade = [(CascadeType.ALL)],
        orphanRemoval = true,
        mappedBy = "project",
    ) val photos: MutableList<Photo> = emptyList<Photo>().toMutableList(),

    @OneToMany(
        fetch = FetchType.EAGER,
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        mappedBy = "project",
    ) val ratings: MutableList<Rating> = emptyList<Rating>().toMutableList(),
) : BaseEntity() {

    @get:Transient
    val averageRating: Float
        get() = ratings.map { it.rating }.average().toFloat()

    @get:Transient
    val totalRatings: Int
        get() = ratings.size
}