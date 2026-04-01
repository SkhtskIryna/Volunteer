package eu.tutorials.data.repository

import eu.tutorials.data.datasource.entity.HelpEntity
import eu.tutorials.data.datasource.entity.HistoryEntity
import eu.tutorials.data.datasource.entity.UserEntity
import eu.tutorials.data.datasource.table.HelpTable
import eu.tutorials.data.datasource.table.HistoryTable
import eu.tutorials.domain.model.History
import eu.tutorials.domain.repository.IHistoryRepository
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class HistoryRepositoryImpl: RepositoryImpl<HistoryEntity, History>(HistoryEntity), IHistoryRepository {
    override suspend fun create(history: History): History = transaction {
        val userEntity = history.idAdmin?.let { UserEntity.findById(it) }

        val requestEntity = HelpEntity.findById(history.idRequest)
        requireNotNull(requestEntity) { "Help request with id ${history.idRequest} not found" }

        HistoryEntity.new {
            this.status = history.status
            this.id_admin = userEntity
            this.id_help = requestEntity
            this.added_at = LocalDateTime.now()
        }.entityToDomain()
    }

    override suspend fun findAllByRecipientId(id: Int): List<History> = transaction {
        HistoryEntity.find { HistoryTable.id_help inSubQuery
                HelpTable.slice(HelpTable.id)
                    .select { HelpTable.id_recipient eq id }
        }.map { it.entityToDomain() }
    }
}