package eu.tutorials.domain.usecase

import eu.tutorials.domain.model.Card
import eu.tutorials.domain.model.User
import eu.tutorials.domain.model.UserRole
import eu.tutorials.domain.repository.ICardRepository
import eu.tutorials.domain.usecase.generics.DeleteUseCase
import eu.tutorials.domain.usecase.generics.FindByIdUseCase
import eu.tutorials.domain.usecase.generics.GetAllUseCase

class CreateCardUseCase(private val iCardRepository: ICardRepository) {
    suspend operator fun invoke(card: Card): Card{
        return iCardRepository.create(card)
    }
}
class DeleteCardUseCase(iCardRepository: ICardRepository): DeleteUseCase<Card>(iCardRepository, listOf<UserRole>(UserRole.Recipient))
class FindCardByIdUseCase(iCardRepository: ICardRepository): FindByIdUseCase<Card>(iCardRepository, listOf<UserRole>(UserRole.Recipient))
class GetAllCardsUseCase(iCardRepository: ICardRepository): GetAllUseCase<Card>(iCardRepository, listOf<UserRole>(UserRole.Recipient,
    UserRole.Admin
))
class UpdateCardUseCase(private val iCardRepository: ICardRepository) {
    suspend operator fun invoke(card: Card, user: User): Boolean {
        if(user.role != UserRole.Recipient){
            throw SecurityException("Ви не маєте доступу до цієї дії.")
        }
        val findById = iCardRepository.findById(card.id!!)
        if (findById?.idUser != user.id) {
            throw SecurityException("Відмовлено в доступі до стороннього ресурсу.")
        }
        return iCardRepository.update(card)
    }
}