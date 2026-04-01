package eu.tutorials.data.datasource.entity

import eu.tutorials.data.datasource.table.DonationTable
import eu.tutorials.data.mapper.DomainMappable
import eu.tutorials.domain.model.Donation
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class DonationEntity(id: EntityID<Int>): IntEntity(id), DomainMappable<Donation> {
    companion object: IntEntityClass<DonationEntity>(DonationTable)

    var sum by DonationTable.sum
    var id_volunteer by UserEntity referencedOn DonationTable.id_volunteer
    var id_financial by FinancialHelpEntity referencedOn DonationTable.id_financial
    var donation_at by DonationTable.donation_at

    override fun entityToDomain(): Donation = Donation(
        id = id.value,
        sum = sum,
        idVolunteer = id_volunteer.id.value,
        idFinancialRequest = id_financial.id.value
    )
}