package com.ninjashadowboy.portfolio.security.oauth2

import com.ninjashadowboy.portfolio.entities.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User

/**
 * UserPrincipal represents the authenticated user in the security context.
 * 
 * This class implements both UserDetails (for traditional authentication)
 * and OAuth2User (for OAuth2 authentication), allowing it to work with both
 * authentication mechanisms.
 */
class UserPrincipal(
    val id: Long,
    val email: String,
    private val password: String?,
    val name: String,
    private val authorities: Collection<GrantedAuthority>,
    private val attributes: Map<String, Any> = emptyMap()
) : OAuth2User, UserDetails {

    override fun getName(): String = name

    override fun getAttributes(): Map<String, Any> = attributes

    override fun getAuthorities(): Collection<GrantedAuthority> = authorities

    override fun getPassword(): String? = password

    override fun getUsername(): String = email

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true

    companion object {
        fun create(user: User): UserPrincipal {
            return UserPrincipal(
                id = user.id,
                email = user.email,
                password = user.pwd,
                name = user.name,
                authorities = user.authorities ?: emptyList()
            )
        }

        fun create(user: User, attributes: Map<String, Any>): UserPrincipal {
            val userPrincipal = create(user)
            return UserPrincipal(
                id = userPrincipal.id,
                email = userPrincipal.email,
                password = userPrincipal.password,
                name = userPrincipal.name,
                authorities = userPrincipal.authorities,
                attributes = attributes
            )
        }
    }
}
