package eu.tutorials.domain.model

import eu.tutorials.domain.security.UserIdentifiable
import kotlinx.serialization.Serializable

@Serializable
data class User (
    val id: Int? = null,
    val firstName: String,
    val lastName: String,
    var role: UserRole,
    val phone: String,
    val email: String,
    val telegram: String?,
    val password: String? = null,
    val photoBase64: String? = null,
    val isBlocked: Boolean = false,
    private val _cards: MutableList<Card> = mutableListOf()
): UserIdentifiable {
    fun addCard(card: Card) {
        if (card.idRecipient != this.id) {
            throw IllegalArgumentException("Картка належить іншому користувачу.")
        }
        if (_cards.contains(card)) {
            throw IllegalArgumentException("Картку вже додано.")
        }
        _cards.add(card)
        role = UserRole.Recipient
    }

    override val idUser: Int
        get() = id!!
}