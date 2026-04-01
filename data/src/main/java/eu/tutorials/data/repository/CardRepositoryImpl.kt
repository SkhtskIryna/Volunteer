package eu.tutorials.data.repository

import eu.tutorials.data.datasource.entity.CardEntity
import eu.tutorials.data.datasource.entity.UserEntity
import eu.tutorials.domain.model.Card
import eu.tutorials.domain.repository.ICardRepository
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class CardRepositoryImpl: RepositoryImpl<CardEntity, Card>(CardEntity), ICardRepository {
    override suspend fun create(card: Card): Card = transaction {
        val userEntity = UserEntity.findById(card.idRecipient)
            ?: throw IllegalArgumentException("User with id ${card.idRecipient} not found")

        val cardEntity = CardEntity.new {
            number = card.number
            validity_period = card.validityPeriod.toString()
            id_recipient = userEntity
        }

        cardEntity.entityToDomain()
    }

    override suspend fun update(card: Card): Boolean = transaction {
        CardEntity.findById(card.id!!)?.let { entity ->
            entity.number = card.number
            entity.validity_period = card.validityPeriod.toString()
            entity.updated_at = LocalDateTime.now()
            true
        } ?: false
    }
}