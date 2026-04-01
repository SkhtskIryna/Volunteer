package eu.tutorials.domain.repository

import eu.tutorials.domain.model.History

interface IHistoryRepository: IRepository<History>{
    suspend fun findAllByRecipientId(id: Int): List<History>
}