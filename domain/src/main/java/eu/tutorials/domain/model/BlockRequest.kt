package eu.tutorials.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class BlockRequest(
    val isBlocked: Boolean
)