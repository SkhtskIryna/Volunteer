package eu.tutorials.domain.usecase.generics

import eu.tutorials.domain.model.User
import eu.tutorials.domain.model.UserRole
import eu.tutorials.domain.repository.IRepository

open class CreateUseCase<T>(private val iRepository: IRepository<T>,
                                              private val userRoles: List<UserRole>) {
    suspend operator fun invoke(item: T, user: User): T {
        if (user.role !in userRoles) {
            throw SecurityException("Ви не маєте доступу до цієї дії.")
        }
        return iRepository.create(item)
    }
}