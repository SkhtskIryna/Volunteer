package eu.tutorials.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class FondyRequest(
    val amount: Double
)
