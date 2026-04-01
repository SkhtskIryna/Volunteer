package eu.tutorials.data.datasource.entity

import eu.tutorials.data.datasource.table.MaterialHelpTable
import eu.tutorials.data.mapper.DomainMappable
import eu.tutorials.domain.model.Help
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class MaterialHelpEntity(id: EntityID<Int>): IntEntity(id), DomainMappable<Help.Material> {
    companion object: IntEntityClass<MaterialHelpEntity>(MaterialHelpTable)

    var help by HelpEntity referencedOn MaterialHelpTable.id
    var category by MaterialHelpTable.category
    var region by MaterialHelpTable.region
    var area by MaterialHelpTable.area
    var city by MaterialHelpTable.city

    override fun entityToDomain(): Help.Material = Help.Material(
        id = id.value,
        title = help.title,
        description = help.description,
        idRecipient = help.id_recipient.id.value,
        category = category,
        region = region,
        area = area,
        city = city,
        createdAt = help.created_at,
        updatedAt = help.updated_at
    )
}