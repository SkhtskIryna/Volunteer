package eu.tutorials.server.security

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Int?,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val role: String,
    val photo: String? = null
)