package eu.tutorials.domain.usecase.generics

import eu.tutorials.domain.model.User
import eu.tutorials.domain.model.UserRole
import eu.tutorials.domain.repository.IRepository
import eu.tutorials.domain.security.UserIdentifiable

open class GetAllUseCase<T: UserIdentifiable>(private val iRepository: IRepository<T>,
                                              private val userRoles: List<UserRole>) {
    open suspend operator fun invoke(user: User): List<T> {
        if (user.role !in userRoles) {
            throw SecurityException("Ви не маєте доступу до цієї дії.")
        }

        return when (user.role) {
            UserRole.Admin -> iRepository.getAll()
            else -> iRepository.getAll().filter { it.idUser == user.id }
        }
    }
}