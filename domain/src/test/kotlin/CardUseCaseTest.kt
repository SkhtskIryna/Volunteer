
import eu.tutorials.domain.model.Card
import eu.tutorials.domain.model.User
import eu.tutorials.domain.model.UserRole
import eu.tutorials.domain.repository.ICardRepository
import eu.tutorials.domain.usecase.CreateCardUseCase
import eu.tutorials.domain.usecase.DeleteCardUseCase
import eu.tutorials.domain.usecase.FindCardByIdUseCase
import eu.tutorials.domain.usecase.GetAllCardsUseCase
import eu.tutorials.domain.usecase.UpdateCardUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.YearMonth
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith

class CardUseCaseTest {
    private val iCardRepository = mockk<ICardRepository>()
    private val createCardUseCase = CreateCardUseCase(iCardRepository)
    private val deleteCardUseCase = DeleteCardUseCase(iCardRepository)
    private val findCardByIdUseCase = FindCardByIdUseCase(iCardRepository)
    private val getAllCardsUseCase = GetAllCardsUseCase(iCardRepository)
    private val updateCardUseCase = UpdateCardUseCase(iCardRepository)

    private val card1Id = 1
    private val card2Id = 2
    private val card3Id = 3

    private val card1 = Card(
        id = card1Id,
        number = "1234 5678 5445 9274",
        validityPeriod = YearMonth.parse("2022-06"),
        idRecipient = 11
    )

    private val card2 = Card(
        id = card2Id,
        number = "7464 2626 7371 2632",
        validityPeriod = YearMonth.parse("2022-06"),
        idRecipient = 10
    )

    private val card3 = Card(
        id = card3Id,
        number = "8373 0474 1234 2234",
        validityPeriod = YearMonth.parse("2021-01"),
        idRecipient = 10
    )

    private val recipient1 = User(
        id = 10,
        role = UserRole.Recipient,
        firstName = "Gil Gilliam",
        lastName = "Brendan Mayo",
        phone = "(711) 413-1412",
        email = "beryl.wells@example.com",
        telegram = "turpis",
        isBlocked = false,
        _cards = mutableListOf(card2, card3)
    )

    private val recipient2 = User(
        id = 11,
        role = UserRole.Recipient,
        firstName = "Loren Roberson",
        lastName = "Reynaldo McIntosh",
        phone = "(333) 830-2452",
        email = "adrienne.bowman@example.com",
        telegram = "mazim",
        isBlocked = false,
        _cards = mutableListOf(card1)
    )

    private val admin = User(
        id = 12,
        role = UserRole.Admin,
        firstName = "Grover Potts",
        lastName = "Ariel Lowe",
        phone = "(326) 995-7807",
        email = "latasha.hale@example.com",
        telegram = "his",
        isBlocked = false
    )

    @Test
    fun createCard() = runBlocking {
        coEvery { iCardRepository.create(card1) } returns card1

        val result = createCardUseCase.invoke(card1)

        assertEquals(card1, result)
        coVerify { iCardRepository.create(card1) }
    }

    @Test
    fun deleteCardAccessDenied() = runBlocking {
        coEvery { iCardRepository.findById(card1Id) } returns card1
        assertFailsWith<SecurityException> {
            deleteCardUseCase.invoke(card1Id, recipient1)
        }
        coVerify(exactly = 0) { iCardRepository.delete(any()) }
    }

    @Test
    fun deleteCardSuccessfully() = runBlocking {
        coEvery { iCardRepository.findById(card2Id) } returns card2
        coEvery { iCardRepository.delete(card2Id) } returns true
        val result = deleteCardUseCase(card2Id, recipient1)
        assertTrue(result)
        coVerify { iCardRepository.delete(card2Id) }
    }

    @Test
    fun findCardById() = runBlocking {
        coEvery { iCardRepository.findById(card2Id) } returns card2
        val result = findCardByIdUseCase.invoke(card2Id, recipient1)
        assert(card2.idRecipient == result?.idRecipient)
        coVerify { iCardRepository.findById(card2Id) }
    }

    @Test
    fun getAllCards() = runBlocking {
        val list = listOf(card2, card3)
        coEvery { iCardRepository.getAll() } returns list
        val result = getAllCardsUseCase.invoke(recipient1)
        assertEquals(list, result)
        coVerify { iCardRepository.getAll() }
    }

    @Test
    fun updateCardAccessDenied1() = runBlocking {
        coEvery { iCardRepository.findById(card1Id) } returns card1
        coEvery { iCardRepository.update(card1) } returns true
        assertFails {
            updateCardUseCase(card1, admin)
        }
        coVerify(exactly = 0) { iCardRepository.update(any()) }
    }

    @Test
    fun updateCardAccessDenied2() = runBlocking {
        coEvery { iCardRepository.findById(card2Id) } returns card2
        coEvery { iCardRepository.update(card2) } returns true
        assertFails {
            updateCardUseCase(card2, recipient2)
        }
        coVerify(exactly = 0) { iCardRepository.update(any()) }
    }

    @Test
    fun updateCardSuccessfully() = runBlocking {
        coEvery { iCardRepository.findById(card3Id) } returns card3
        coEvery { iCardRepository.update(card3) } returns true
        val result = updateCardUseCase.invoke(card3, recipient1)
        assertTrue(result)
        coVerify { iCardRepository.update(card3) }
    }
}