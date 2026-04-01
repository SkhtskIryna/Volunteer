package eu.tutorials.volunteerapp.data

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class AuthResponse(
    val success: Boolean,
    val message: String,
    val role: String? = null
)