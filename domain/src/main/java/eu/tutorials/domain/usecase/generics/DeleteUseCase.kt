package eu.tutorials.domain.usecase.generics

import eu.tutorials.domain.model.User
import eu.tutorials.domain.model.UserRole
import eu.tutorials.domain.repository.IRepository
import eu.tutorials.domain.security.UserIdentifiable

open class DeleteUseCase<T>(private val iRepository: IRepository<T>,
                                              private val userRoles: List<UserRole>) {
    suspend operator fun invoke(id: Int, user: User): Boolean{
        if(user.role !in userRoles){
            throw SecurityException("Ви не маєте доступу до цієї дії.")
        }
        val findById = iRepository.findById(id)
        if (findById is UserIdentifiable) {
            if (findById.idUser != user.id && user.role != UserRole.Admin) {
                throw SecurityException("Відмовлено в доступі до стороннього ресурсу.")
            }
        }
        return iRepository.delete(id)
    }
}