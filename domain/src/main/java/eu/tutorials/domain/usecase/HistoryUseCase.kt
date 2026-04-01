package eu.tutorials.domain.usecase

import eu.tutorials.domain.model.Bin
import eu.tutorials.domain.model.History
import eu.tutorials.domain.model.HistoryStatus
import eu.tutorials.domain.model.User
import eu.tutorials.domain.model.UserRole
import eu.tutorials.domain.repository.IBinRepository
import eu.tutorials.domain.repository.IHelpRepository
import eu.tutorials.domain.repository.IHistoryRepository
import eu.tutorials.domain.repository.IUserRepository
import eu.tutorials.domain.usecase.generics.CreateUseCase
import eu.tutorials.domain.usecase.generics.FindByIdUseCase
import eu.tutorials.domain.usecase.generics.GetAllUseCase
import eu.tutorials.domain.utils.HistoryUtils

class CreateHistoryUseCase(iHistoryRepository: IHistoryRepository): CreateUseCase<History>(iHistoryRepository, listOf<UserRole>(UserRole.Admin))
class DeleteHistoryUseCase(
    private val iHistoryRepository: IHistoryRepository,
    private val iBinRepository: IBinRepository,
    private val iUserRepository: IUserRepository,
    private val iHelpRepository: IHelpRepository
) {
    suspend operator fun invoke(history: History, user: User, bin: Bin): Boolean {
        val userRoles = listOf(UserRole.Admin)
        if (user.role !in userRoles) {
            throw SecurityException("Ви не маєте доступу до цієї дії.")
        }

        val help = iHelpRepository.findById(history.idHelp)
        val recipientId = help?.idRecipient

        val histories: List<History> = iHistoryRepository.findAllByRecipientId(recipientId!!)

        val count = histories.count { it.status == HistoryStatus.Rejected }

        if (count >= 3) {
            iBinRepository.create(bin.copy(idRecipient = recipientId))

            val recipient = iUserRepository.findById(recipientId)
            iUserRepository.update(recipient!!.copy(isBlocked = true))
        }

        return iHistoryRepository.delete(history.id!!)
    }
}
class FindHistoryByIdUseCase(iHistoryRepository: IHistoryRepository): FindByIdUseCase<History>(iHistoryRepository, listOf<UserRole>(UserRole.Admin, UserRole.Recipient, UserRole.Volunteer))
class GetAllHistoriesUseCase(
    private val iHistoryRepository: IHistoryRepository
) : GetAllUseCase<History>(iHistoryRepository, listOf(UserRole.Admin, UserRole.Recipient, UserRole.Volunteer)) {

    override suspend operator fun invoke(user: User): List<History> {
        return when (user.role) {
            UserRole.Admin -> iHistoryRepository.getAll()

            UserRole.Recipient -> {
                iHistoryRepository.findAllByRecipientId(user.id!!)
            }

            UserRole.Volunteer -> {
                iHistoryRepository.getAll().filter { it.status == HistoryStatus.Approved }
            }

            else -> throw SecurityException("Ви не маєте доступу до цієї дії.")
        }
    }
}

class FilterHistoriesByRecipientFullNameUseCase(
    private val iHistoryRepository: IHistoryRepository,
    private val iHelpRepository: IHelpRepository,
    private val iUserRepository: IUserRepository
) {
    suspend operator fun invoke(query: String): List<History> {
        val histories = iHistoryRepository.getAll()
        val helps = iHelpRepository.getAll()
        val users = iUserRepository.getAll()
        return HistoryUtils.filterHistoriesByRecipientFullName(histories, helps, users, query)
    }
}