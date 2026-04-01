package eu.tutorials.volunteerapp.data

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class Card(
    val number: String,
    val validityPeriod: String,
    val idRecipient: Int,
    val id: Int? = null,
    @Transient
    val fullNumber: String? = null
)