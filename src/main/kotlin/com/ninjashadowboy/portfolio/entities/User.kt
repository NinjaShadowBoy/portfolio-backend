package com.ninjashadowboy.portfolio.entities

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime


@Entity
@Table(name = "users")
data class User(
    @Column(nullable = false, unique = true) val email: String,

    @Column(nullable = false) val pwd: String,

    @Column(nullable = false) val name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false) val role: Role = Role.USER,

    val lastLoginAt: LocalDateTime? = null,

    @OneToMany(
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        mappedBy = "user"
    )
    val ratings: MutableList<Rating> = mutableListOf(),
) : BaseEntity(), UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority?>? = when (role) {
        Role.ADMIN -> listOf(
            SimpleGrantedAuthority(Role.ADMIN.name),
            SimpleGrantedAuthority(Role.USER.name),
        )
        Role.USER -> listOf(
            SimpleGrantedAuthority(Role.USER.name),
        )
    }

    override fun getPassword(): String? = pwd

    override fun getUsername(): String? = email
}