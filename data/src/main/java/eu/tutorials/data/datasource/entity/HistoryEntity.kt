package eu.tutorials.data.datasource.entity

import eu.tutorials.data.datasource.table.HistoryTable
import eu.tutorials.data.mapper.DomainMappable
import eu.tutorials.domain.model.History
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class HistoryEntity(id: EntityID<Int>): IntEntity(id), DomainMappable<History> {
    companion object: IntEntityClass<HistoryEntity>(HistoryTable)

    var status by HistoryTable.status
    var id_admin by UserEntity optionalReferencedOn HistoryTable.id_admin
    var id_help by HelpEntity referencedOn HistoryTable.id_help
    var added_at by HistoryTable.added_at

    override fun entityToDomain(): History = History(
        id = id.value,
        status = status,
        idAdmin = id_admin?.id?.value,
        idRequest = id_help.id.value
    )
}