package eu.tutorials.data.datasource.entity

import eu.tutorials.data.datasource.table.CardTable
import eu.tutorials.data.mapper.DomainMappable
import eu.tutorials.domain.model.Card
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.time.YearMonth

class CardEntity(id: EntityID<Int>): IntEntity(id), DomainMappable<Card>{
    companion object: IntEntityClass<CardEntity>(CardTable)

    var number by CardTable.number
    var validity_period by CardTable.validity_period
    var id_recipient by UserEntity referencedOn CardTable.id_recipient
    var updated_at by CardTable.updated_at

    override fun entityToDomain(): Card = Card(
        id = id.value,
        number = number,
        validityPeriod = YearMonth.parse(validity_period),
        idRecipient = id_recipient.id.value
    )
}