package eu.tutorials.domain.usecase

import eu.tutorials.domain.model.MaterialParticipation
import eu.tutorials.domain.model.MaterialParticipationStatus
import eu.tutorials.domain.model.User
import eu.tutorials.domain.model.UserRole
import eu.tutorials.domain.repository.IHelpRepository
import eu.tutorials.domain.repository.IMaterialParticipationRepository
import eu.tutorials.domain.usecase.generics.CreateUseCase
import eu.tutorials.domain.usecase.generics.DeleteUseCase
import eu.tutorials.domain.usecase.generics.GetAllUseCase
import java.time.LocalDateTime

class CreateMaterialParticipationUseCase(iMaterialParticipationRepository: IMaterialParticipationRepository):
    CreateUseCase<MaterialParticipation>(iMaterialParticipationRepository, listOf<UserRole>(UserRole.Volunteer))
class DeleteMaterialParticipationUseCase(iMaterialParticipationRepository: IMaterialParticipationRepository):
    DeleteUseCase<MaterialParticipation>(iMaterialParticipationRepository, listOf<UserRole>(UserRole.Volunteer))
class FindMaterialParticipationByIdUseCase(
    private val iMaterialParticipationRepository: IMaterialParticipationRepository,
    private val iHelpRepository: IHelpRepository
) {
    suspend operator fun invoke(id: Int, user: User): MaterialParticipation? {
        val participation = iMaterialParticipationRepository.findById(id)
            ?: throw IllegalArgumentException("Material participation not found")

        return when (user.role) {
            UserRole.Admin -> participation
            UserRole.Volunteer -> {
                if (participation.idVolunteer != user.id) {
                    throw SecurityException("Відмовлено в доступі до чужого ресурсу")
                }
                participation
            }
            UserRole.Recipient -> {
                val materialRequest = iHelpRepository.findById(participation.idMaterialRequest)
                    ?: throw IllegalArgumentException("Material request not found")

                if (materialRequest.idRecipient != user.id) {
                    throw SecurityException("Відмовлено в доступі до чужого ресурсу")
                }
                participation
            }
            else -> throw SecurityException("Ви не маєте доступу до цієї дії")
        }
    }
}
class GetAllMaterialParticipationUseCase(iMaterialParticipationRepository: IMaterialParticipationRepository):
    GetAllUseCase<MaterialParticipation>(iMaterialParticipationRepository,
        listOf<UserRole>(UserRole.Admin, UserRole.Volunteer))
class UpdateMaterialParticipationUseCase(
    private val iMaterialParticipationRepository: IMaterialParticipationRepository) {
    @Suppress("NewApi")
    suspend operator fun invoke(materialParticipation: MaterialParticipation, user: User): Boolean {
        val recipientStatus = listOf(MaterialParticipationStatus.Delivered)

        val existing = iMaterialParticipationRepository.findById((materialParticipation.id)?.toInt()
            ?: throw IllegalArgumentException("Participation not found"))
        val newStatus = materialParticipation.status

        when (user.role) {
            UserRole.Recipient -> {
                if (newStatus !in recipientStatus) {
                    throw SecurityException("You don't have permission to set this status")
                }
            }
            else -> {}
        }

        val updated = existing?.copy(
            status = newStatus,
            deliveredAt = if (newStatus == MaterialParticipationStatus.Delivered) LocalDateTime.now() else existing.deliveredAt
        )
        return iMaterialParticipationRepository.update(updated!!)
    }
}