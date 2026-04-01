package eu.tutorials.domain.repository

import eu.tutorials.domain.model.Card

interface ICardRepository: IRepository<Card> {
    suspend fun update(card: Card): Boolean
}