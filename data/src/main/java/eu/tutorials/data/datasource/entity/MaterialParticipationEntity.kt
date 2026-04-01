package eu.tutorials.data.datasource.entity

import eu.tutorials.data.datasource.table.MaterialParticipationTable
import eu.tutorials.data.mapper.DomainMappable
import eu.tutorials.domain.model.MaterialParticipation
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class MaterialParticipationEntity(id: EntityID<Int>): IntEntity(id), DomainMappable<MaterialParticipation> {
    companion object: IntEntityClass<MaterialParticipationEntity>(MaterialParticipationTable)

    var status by MaterialParticipationTable.status
    var id_volunteer by UserEntity referencedOn MaterialParticipationTable.id_volunteer
    var id_material by MaterialHelpEntity referencedOn MaterialParticipationTable.id_material
    var joined_at by MaterialParticipationTable.joined_at
    var delivered_at by MaterialParticipationTable.delivered_at

    override fun entityToDomain(): MaterialParticipation = MaterialParticipation(
        id = id.value,
        status = status,
        idVolunteer = id_volunteer.id.value,
        idMaterialRequest = id_material.id.value,
        deliveredAt = delivered_at
    )
}