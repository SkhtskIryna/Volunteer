package eu.tutorials.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class FondyResponse(
    val checkout_url: String,
    val order_id: String
)