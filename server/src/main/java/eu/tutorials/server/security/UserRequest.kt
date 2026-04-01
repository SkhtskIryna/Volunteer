package eu.tutorials.server.security

import kotlinx.serialization.Serializable

@Serializable
data class UserRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val role: UserRole,
    val telegram: String?,
    val password: String
)

@Serializable
enum class UserRole {
    Admin,
    Volunteer,
    Recipient
}