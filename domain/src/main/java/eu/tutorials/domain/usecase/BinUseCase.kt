package eu.tutorials.domain.usecase

import eu.tutorials.domain.model.Bin
import eu.tutorials.domain.model.User
import eu.tutorials.domain.model.UserRole
import eu.tutorials.domain.repository.IBinRepository
import eu.tutorials.domain.repository.IUserRepository
import eu.tutorials.domain.usecase.generics.CreateUseCase
import eu.tutorials.domain.usecase.generics.DeleteUseCase
import eu.tutorials.domain.usecase.generics.FindByIdUseCase
import eu.tutorials.domain.usecase.generics.GetAllUseCase
import eu.tutorials.domain.utils.BinUtils

class CreateBinUseCase(iBinRepository: IBinRepository): CreateUseCase<Bin>(iBinRepository, listOf<UserRole>(UserRole.Admin))
class DeleteBinUseCase(iBinRepository: IBinRepository): DeleteUseCase<Bin>(iBinRepository, listOf<UserRole>(UserRole.Admin))
class FindBinByIdUseCase(iBinRepository: IBinRepository): FindByIdUseCase<Bin>(iBinRepository, listOf<UserRole>(UserRole.Admin))
class GetAllBinsUseCase(iBinRepository: IBinRepository): GetAllUseCase<Bin>(iBinRepository, listOf<UserRole>(UserRole.Admin))
class UnblockedUserUseCase(private val iBinRepository: IBinRepository) {
    suspend operator fun invoke(admin: User, recipientId: Int): User {
        if (admin.role != UserRole.Admin) {
            throw SecurityException("Ви не маєте доступу до цієї дії.")
        }
        return iBinRepository.unblockedUser(recipientId)
    }
}
class FilterBinsByRecipientNameUseCase(
    private val iBinRepository: IBinRepository,
    private val iUserRepository: IUserRepository
) {
    suspend operator fun invoke(query: String): List<Bin> {
        val bins = iBinRepository.getAll()
        val users = iUserRepository.getAll()
        return BinUtils.filterBinsByRecipientFullName(bins, users, query)
    }
}