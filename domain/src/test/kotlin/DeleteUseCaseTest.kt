import eu.tutorials.domain.model.Card
import eu.tutorials.domain.model.User
import eu.tutorials.domain.model.UserRole
import eu.tutorials.domain.repository.IRepository
import eu.tutorials.domain.usecase.generics.DeleteUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.YearMonth
import kotlin.test.assertFailsWith

class DeleteUseCaseTest {
    private val cardRepository = mockk<IRepository<Card>>()
    private val deleteUseCase = DeleteUseCase(cardRepository, listOf(UserRole.Recipient))

    private val cardId = 4

    private val card = Card(
        id = cardId,
        number = "**** **** 9764",
        validityPeriod = YearMonth.parse("2025-07"),
        idRecipient = 4
    )

    private val admin = User(
        id = 1,
        role = UserRole.Admin,
        firstName = "Grover Potts",
        lastName = "Ariel Lowe",
        phone = "(326) 995-7807",
        email = "latasha.hale@example.com",
        telegram = "his",
        isBlocked = false
    )

    private val recipient = User(
        id = 4,
        role = UserRole.Recipient,
        firstName = "Nadine Daugherty",
        lastName = "Olivia Atkinson",
        phone = "(491) 296-3685",
        email = "dirk.conway@example.com",
        telegram = "olivia",
        isBlocked = false,
        _cards = mutableListOf(card)
    )

    @Test
    fun userHasPermission() = runBlocking {
        coEvery { cardRepository.findById(cardId) } returns card
        coEvery { cardRepository.delete(cardId) } returns true

        val result = deleteUseCase(cardId, recipient)

        assert(result) { "Успішно видалено." }
        coVerify { cardRepository.delete(cardId) }
    }

    @Test
    fun userHasNoPermission() = runBlocking {
        assertFailsWith<SecurityException> {
            deleteUseCase(cardId, admin)
        }
        coVerify(exactly = 0) { cardRepository.delete(any()) }
    }
}