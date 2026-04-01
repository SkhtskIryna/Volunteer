package eu.tutorials.volunteerapp.data

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class AuthRequest(val email: String, val password: String)

