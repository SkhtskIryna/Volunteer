package eu.tutorials.data.repository

import eu.tutorials.data.datasource.entity.DonationEntity
import eu.tutorials.data.datasource.entity.FinancialHelpEntity
import eu.tutorials.data.datasource.entity.UserEntity
import eu.tutorials.domain.model.Donation
import eu.tutorials.domain.repository.IDonationRepository
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class DonationRepositoryImpl: RepositoryImpl<DonationEntity, Donation>(DonationEntity), IDonationRepository {
    override suspend fun create(donation: Donation): Donation = transaction{
        val userEntity = UserEntity.findById(donation.idUser)
        requireNotNull(userEntity){
            "User with id ${donation.idUser} not found"
        }

        val requestEntity = FinancialHelpEntity.findById(donation.idFinancialRequest)
        requireNotNull(requestEntity){
            "Request with id ${donation.idFinancialRequest} not found"
        }

        DonationEntity.new {
            this.sum = donation.sum
            this.id_volunteer = userEntity
            this.id_financial = requestEntity
            this.donation_at = LocalDateTime.now()
        }.entityToDomain()
    }
}