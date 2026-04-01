package eu.tutorials.data.datasource.entity

import eu.tutorials.data.datasource.table.BinTable
import eu.tutorials.data.mapper.DomainMappable
import eu.tutorials.domain.model.Bin
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class BinEntity(id: EntityID<Int>) : IntEntity(id), DomainMappable<Bin> {
    companion object : IntEntityClass<BinEntity>(BinTable)

    var id_recipient by UserEntity referencedOn BinTable.id_recipient
    var added_at by BinTable.added_at

    override fun entityToDomain(): Bin = Bin(
        id = id.value,
        idRecipient = id_recipient.id.value
    )
}
