package eu.tutorials.volunteerapp.data

import android.annotation.SuppressLint
import eu.tutorials.domain.model.UserRole
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val role: UserRole,
    val telegram: String?,
    val password: String? = null,
    val isBlocked: Boolean? = false,
    val photoBase64: String? = null,
    val id: Int? = null
)