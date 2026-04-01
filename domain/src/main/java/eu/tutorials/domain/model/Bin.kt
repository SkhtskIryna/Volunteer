package eu.tutorials.domain.model

import eu.tutorials.domain.security.UserIdentifiable
import kotlinx.serialization.Serializable

@Serializable
data class Bin(
    val id: Int? = null,
    val idRecipient: Int
): UserIdentifiable {
    override val idUser: Int
        get() = idRecipient
}
