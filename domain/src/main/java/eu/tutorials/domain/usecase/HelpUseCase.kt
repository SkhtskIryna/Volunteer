package eu.tutorials.domain.usecase

import eu.tutorials.domain.model.Help
import eu.tutorials.domain.model.HistoryStatus
import eu.tutorials.domain.model.User
import eu.tutorials.domain.model.UserRole
import eu.tutorials.domain.repository.IHelpRepository
import eu.tutorials.domain.repository.IHistoryRepository
import eu.tutorials.domain.security.UserIdentifiable
import eu.tutorials.domain.usecase.generics.CreateUseCase
import eu.tutorials.domain.usecase.generics.DeleteUseCase

class CreateHelpUseCase(iHelpRepository: IHelpRepository): CreateUseCase<Help>(iHelpRepository, listOf<UserRole>(UserRole.Recipient))
class DeleteHelpUseCase(iHelpRepository: IHelpRepository): DeleteUseCase<Help>(iHelpRepository, listOf<UserRole>(UserRole.Admin, UserRole.Recipient))
class FindHelpByIdUseCase(
    private val iHelpRepository: IHelpRepository,
    private val userRoles: List<UserRole> = listOf(
        UserRole.Admin,
        UserRole.Recipient,
        UserRole.Volunteer
    )
){
    suspend operator fun invoke(id: Int,  user: User): Help? {
        if(user.role !in userRoles){
            throw SecurityException("Ви не маєте доступу до цієї дії.")
        }
        val findById = iHelpRepository.findById(id)
        if (findById is UserIdentifiable) {
            if (findById.idUser != user.id && user.role != UserRole.Admin && user.role != UserRole.Volunteer) {
                throw SecurityException("Відмовлено в доступі до стороннього ресурсу.")
            }
        }
        return findById
    }
}

class GetAllHelpsUseCase(
    private val iHelpRepository: IHelpRepository,
    private val userRoles: List<UserRole> = listOf(
        UserRole.Admin,
        UserRole.Recipient,
        UserRole.Volunteer
    )
) {

    suspend operator fun invoke(user: User): List<Help> {
        if (user.role !in userRoles) {
            throw SecurityException("Ви не маєте доступу до цієї дії.")
        }

        return when (user.role) {
            UserRole.Admin,
            UserRole.Volunteer -> iHelpRepository.getAll()

            UserRole.Recipient ->
                iHelpRepository.getAll()
                    .filter { it.idUser == user.id }
        }
    }
}

class UpdateHelpUseCase(
    private val iHelpRepository: IHelpRepository,
    private val iHistoryRepository: IHistoryRepository
) {
    suspend operator fun invoke(help: Help, user: User): Boolean {
        if (user.role == UserRole.Admin) {
            throw SecurityException("Ви не маєте доступу до цієї дії.")
        }

        // Отримання всіх історій для даної допомоги
        val histories = iHistoryRepository.getAll()
        val lastHistory = histories
            .filter { it.idHelp == help.id }
            .maxByOrNull { it.id ?: 0 }

        // Якщо історії немає — оновлення допомоги
        if (lastHistory != null) {
            when (lastHistory.status) {
                HistoryStatus.Approved -> {
                    if (user.role != UserRole.Volunteer) {
                        throw SecurityException(
                            "Ваш запит уже схвалено. Ви більше не можете його змінювати."
                        )
                    }
                }
                HistoryStatus.Rejected -> throw SecurityException(
                    "Ваш запит було відхилено. Ви більше не можете його змінювати."
                )
                else -> { }
            }
        }

        return iHelpRepository.update(help)
    }
}