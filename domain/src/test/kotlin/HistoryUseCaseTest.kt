import eu.tutorials.domain.model.Bin
import eu.tutorials.domain.model.Help
import eu.tutorials.domain.model.History
import eu.tutorials.domain.model.HistoryStatus
import eu.tutorials.domain.model.User
import eu.tutorials.domain.model.UserRole
import eu.tutorials.domain.repository.IBinRepository
import eu.tutorials.domain.repository.IHelpRepository
import eu.tutorials.domain.repository.IHistoryRepository
import eu.tutorials.domain.repository.IUserRepository
import eu.tutorials.domain.usecase.CreateHistoryUseCase
import eu.tutorials.domain.usecase.DeleteHistoryUseCase
import eu.tutorials.domain.usecase.FilterHistoriesByRecipientFullNameUseCase
import eu.tutorials.domain.usecase.FindHistoryByIdUseCase
import eu.tutorials.domain.usecase.GetAllHistoriesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class HistoryUseCaseTest {

    private val iHistoryRepository = mockk<IHistoryRepository>()
    private val iBinRepository = mockk<IBinRepository>()
    private val iHelpRepository = mockk<IHelpRepository>()
    private val iUserRepository = mockk<IUserRepository>()

    private val createHistoryUseCase = CreateHistoryUseCase(iHistoryRepository)
    private val deleteHistoryUseCase = DeleteHistoryUseCase(
        iHistoryRepository,
        iBinRepository,
        iUserRepository,
        iHelpRepository
    )
    private val findHistoryByIdUseCase = FindHistoryByIdUseCase(iHistoryRepository)
    private val getAllHistoriesUseCase = GetAllHistoriesUseCase(iHistoryRepository)
    private val filterHistoriesByRecipientFullNameUseCase = FilterHistoriesByRecipientFullNameUseCase(
        iHistoryRepository,
        iHelpRepository,
        iUserRepository
    )

    private val admin = User(
        id = 1,
        role = UserRole.Admin,
        firstName = "Admin",
        lastName = "User",
        phone = "111",
        email = "admin@example.com",
        telegram = ""
    )

    private val recipient = User(
        id = 2,
        role = UserRole.Recipient,
        firstName = "Ivan",
        lastName = "Ivanov",
        phone = "222",
        email = "ivan@example.com",
        telegram = "@res"
    )

    private val help = Help.Financial(
        id = 5,
        title = "Help Title",
        description = "Desc",
        idRecipient = recipient.id ?: error("recipient.id не може бути null"),
        from = LocalDate.now().minusDays(10),
        to = LocalDate.now().plusDays(10),
        plannedAmount = 1000.0,
        collected = 300.0,
        createdAt = LocalDateTime.now().minusDays(5),
        updatedAt = null
    )

    private val history = History(
        id = 100,
        idAdmin = admin.id ?: error("admin.id не може бути null"),
        idRequest = help.id ?: error("help.id не може бути null"),
        status = HistoryStatus.Rejected
    )

    private val bin = Bin(
        id = 10,
        idRecipient = recipient.id ?: error("recipient.id не може бути null")
    )

    @Test
    fun createHistorySuccessfully() = runBlocking {
        val historyId = history.id ?: error("history.id не може бути null")

        coEvery { iHistoryRepository.findById(historyId) } returns history
        coEvery { iHistoryRepository.create(history) } returns history

        val result = createHistoryUseCase(history, admin)
        assertEquals(history, result)
        coVerify { iHistoryRepository.create(history) }
    }

    @Test
    fun deleteHistorySuccessfullyWithBlock() = runBlocking {
        val historyId = history.id ?: error("history.id не може бути null")
        val recipientId = recipient.id ?: error("recipient.id не може бути null")
        val helpId = help.id ?: error("help.id не може бути null")

        coEvery { iHelpRepository.findById(history.idRequest) } returns help
        coEvery { iHistoryRepository.findAllByRecipientId(recipientId) } returns
                listOf(history, history.copy(id = 101), history.copy(id = 102))
        coEvery { iBinRepository.create(any()) } returns bin
        coEvery { iUserRepository.findById(recipientId) } returns recipient
        coEvery { iUserRepository.update(match { it.isBlocked }) } returns true
        coEvery { iHistoryRepository.delete(historyId) } returns true

        val result = deleteHistoryUseCase(history, admin, bin)
        assertTrue(result)

        coVerify { iUserRepository.update(match { it.isBlocked }) }
        coVerify { iBinRepository.create(match { it.idRecipient == recipientId }) }
    }

    @Test
    fun deleteHistoryFailsForNonAdmin() = runBlocking {
        val fakeUser = recipient.copy(role = UserRole.Volunteer)
        assertFailsWith<SecurityException> {
            deleteHistoryUseCase(history, fakeUser, bin)
        }
        coVerify(exactly = 0) { iHistoryRepository.delete(any()) }
    }

    @Test
    fun findHistoryByIdSuccessfully() = runBlocking {
        val historyId = history.id ?: error("history.id не може бути null")

        coEvery { iHistoryRepository.findById(historyId) } returns history
        val result = findHistoryByIdUseCase(historyId, admin)
        assertEquals(history.idAdmin, result?.idAdmin)
        coVerify { iHistoryRepository.findById(historyId) }
    }

    @Test
    fun getAllHistoriesSuccessfully() = runBlocking {
        val histories = listOf(history)
        coEvery { iHistoryRepository.getAll() } returns histories

        val result = getAllHistoriesUseCase(admin)
        assertEquals(histories, result)
        coVerify { iHistoryRepository.getAll() }
    }

    @Test
    fun filterHistoriesByRecipientFullNameTest() = runBlocking {
        val historyId = history.id ?: error("history.id не може бути null")

        val histories = listOf(history)
        val helps = listOf(help)
        val users = listOf(recipient)

        coEvery { iHistoryRepository.getAll() } returns histories
        coEvery { iHelpRepository.getAll() } returns helps
        coEvery { iUserRepository.getAll() } returns users

        val result = filterHistoriesByRecipientFullNameUseCase("Ivan ivanov")
        assertEquals(historyId, result.first().id)
    }
}