package eu.tutorials.data.repository

import eu.tutorials.data.datasource.entity.MaterialHelpEntity
import eu.tutorials.data.datasource.entity.MaterialParticipationEntity
import eu.tutorials.data.datasource.entity.UserEntity
import eu.tutorials.domain.model.MaterialParticipation
import eu.tutorials.domain.repository.IMaterialParticipationRepository
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class MaterialParticipationRepositoryImpl: RepositoryImpl<MaterialParticipationEntity, MaterialParticipation>(
    MaterialParticipationEntity), IMaterialParticipationRepository {
    override suspend fun create(materialParticipation: MaterialParticipation): MaterialParticipation =
        transaction {
            val userEntity = UserEntity.findById(materialParticipation.idUser)
            requireNotNull(userEntity){
                "User with id ${materialParticipation.idUser} not found"
            }

            val requestEntity = MaterialHelpEntity.findById(materialParticipation.idHelp)
            requireNotNull(requestEntity){
                "Require with id ${materialParticipation.idHelp} not found"
            }

            MaterialParticipationEntity.new {
                this.status = materialParticipation.status
                this.id_volunteer = userEntity
                this.id_material = requestEntity
                this.joined_at = joined_at
            }.entityToDomain()
        }

    override suspend fun update(materialParticipation: MaterialParticipation): Boolean = transaction {
        MaterialParticipationEntity.findById(materialParticipation.id!!)?.let {
            materialParticipationEntity ->
            materialParticipationEntity.status = materialParticipation.status
            materialParticipationEntity.delivered_at = LocalDateTime.now()
            true
        } ?: false
    }
}