package eu.tutorials.data.datasource.entity

import eu.tutorials.data.datasource.table.FinancialHelpTable
import eu.tutorials.data.mapper.DomainMappable
import eu.tutorials.domain.model.Help
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class FinancialHelpEntity(id: EntityID<Int>): IntEntity(id), DomainMappable<Help.Financial> {
    companion object: IntEntityClass<FinancialHelpEntity>(FinancialHelpTable)

    var help by HelpEntity referencedOn FinancialHelpTable.id
    var from by FinancialHelpTable.from
    var to by FinancialHelpTable.to
    var planned_amount by FinancialHelpTable.planned_amount
    var collected by FinancialHelpTable.collected

    override fun entityToDomain(): Help.Financial = Help.Financial(
        id = id.value,
        title = help.title,
        description = help.description,
        idRecipient = help.id_recipient.id.value,
        from = from,
        to = to,
        plannedAmount = planned_amount,
        collected = collected,
        createdAt = help.created_at,
        updatedAt = help.updated_at
    )
}