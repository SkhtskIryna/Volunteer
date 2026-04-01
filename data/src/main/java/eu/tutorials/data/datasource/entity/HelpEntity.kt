package eu.tutorials.data.datasource.entity

import eu.tutorials.data.datasource.table.FinancialHelpTable
import eu.tutorials.data.datasource.table.HelpTable
import eu.tutorials.data.datasource.table.MaterialHelpTable
import eu.tutorials.data.mapper.DomainMappable
import eu.tutorials.domain.model.Help
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class HelpEntity(id: EntityID<Int>): IntEntity(id), DomainMappable<Help> {
    companion object: IntEntityClass<HelpEntity>(HelpTable)

    var type by HelpTable.type
    var title by HelpTable.title
    var description by HelpTable.description
    var id_recipient by UserEntity referencedOn HelpTable.id_recipient
    var created_at by HelpTable.created_at
    var updated_at by HelpTable.updated_at

    val financialHelp by FinancialHelpEntity.optionalBackReferencedOn(FinancialHelpTable.id)
    val materialHelp by MaterialHelpEntity.optionalBackReferencedOn(MaterialHelpTable.id)

    override fun entityToDomain(): Help = when (type) {
        "FINANCIAL" -> Help.Financial(
            id = id.value,
            title = title,
            description = description,
            idRecipient = id_recipient.id.value,
            from = financialHelp!!.from,
            to = financialHelp!!.to,
            plannedAmount = financialHelp!!.planned_amount,
            collected = financialHelp!!.collected,
            createdAt = created_at,
            updatedAt = updated_at
        )
        "MATERIAL" -> Help.Material(
            id = id.value,
            title = title,
            description = description,
            idRecipient = id_recipient.id.value,
            category = materialHelp!!.category,
            region = materialHelp!!.region,
            area = materialHelp!!.area,
            city = materialHelp!!.city,
            createdAt = created_at,
            updatedAt = updated_at
        )
        else -> error("Unknown help type: $type")
    }
}