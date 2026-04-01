package eu.tutorials.volunteerapp.data

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class FondyStatusResponse(
    val status: String
)