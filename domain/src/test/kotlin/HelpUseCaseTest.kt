
import eu.tutorials.domain.model.Help
import eu.tutorials.domain.model.History
import eu.tutorials.domain.model.HistoryStatus
import eu.tutorials.domain.model.MaterialCategory
import eu.tutorials.domain.model.User
import eu.tutorials.domain.model.UserRole
import eu.tutorials.domain.repository.IHelpRepository
import eu.tutorials.domain.repository.IHistoryRepository
import eu.tutorials.domain.usecase.CreateHelpUseCase
import eu.tutorials.domain.usecase.DeleteHelpUseCase
import eu.tutorials.domain.usecase.FindHelpByIdUseCase
import eu.tutorials.domain.usecase.GetAllHelpsUseCase
import eu.tutorials.domain.usecase.UpdateHelpUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertFailsWith

class HelpUseCaseTest {

    private val iHelpRepository = mockk<IHelpRepository>()
    private val iHistoryRepository = mockk< IHistoryRepository>()

    private val createHelpUseCase = CreateHelpUseCase(iHelpRepository)
    private val deleteHelpUseCase = DeleteHelpUseCase(iHelpRepository)
    private val findHelpByIdUseCase = FindHelpByIdUseCase(iHelpRepository)
    private val getAllHelpsUseCase = GetAllHelpsUseCase(iHelpRepository)
    private val updateHelpUseCase = UpdateHelpUseCase(
        iHelpRepository,
        iHistoryRepository
    )

    private val recipient = User(
        id = 1,
        role = UserRole.Recipient,
        firstName = "Rec",
        lastName = "User",
        phone = "123",
        email = "r@e.com",
        telegram = ""
    )

    private val admin = User(
        id = 2,
        role = UserRole.Admin,
        firstName = "Admin",
        lastName = "User",
        phone = "456",
        email = "a@e.com",
        telegram = "@admin"
    )

    private val volunteer = User(
        id = 3,
        role = UserRole.Volunteer,
        firstName = "Vol",
        lastName = "User",
        phone = "789",
        email = "v@e.com",
        telegram = ""
    )

    private val financialHelp = Help.Financial(
        id = 10,
        title = "Help for school",
        description = "Buying books",
        idRecipient = 1,
        from = LocalDate.of(2025, 1, 1),
        to = LocalDate.of(2025, 12, 31),
        plannedAmount = 1000.0,
        collected = 200.0,
        createdAt = LocalDateTime.now()
    )

    private val materialHelp = Help.Material(
        id = 11,
        title = "Winter jackets",
        description = "Need warm clothes",
        idRecipient = 1,
        category = MaterialCategory.Clothes.toString(),
        region = "Kyiv region",
        area = "Some area",
        city = "Kyiv",
        createdAt = LocalDateTime.now()
    )

    private val pendingHistory = History(
        id = 1,
        status = HistoryStatus.Pending,
        idAdmin = 3,
        idRequest = 11
    )

    private val approvedHistory = pendingHistory.copy(status = HistoryStatus.Approved)
    private val rejectedHistory = pendingHistory.copy(status = HistoryStatus.Rejected)

    @Test
    fun createHelpSuccessfully() = runBlocking {
        coEvery { iHelpRepository.create(financialHelp) } returns financialHelp
        coEvery { iHelpRepository.findById(financialHelp.id!!) } returns financialHelp

        val result = createHelpUseCase(financialHelp, recipient)
        assertEquals(financialHelp, result)
        coVerify { iHelpRepository.create(financialHelp) }
    }

    @Test
    fun createHelpFailsForNonRecipient() = runBlocking {
        val ex = assertFailsWith<SecurityException> {
            createHelpUseCase(financialHelp, volunteer)
        }
        assertEquals("Ви не маєте доступу до цієї дії.", ex.message)
    }

    @Test
    fun deleteHelpAsAdmin() = runBlocking {
        coEvery { iHelpRepository.findById(materialHelp.id!!) } returns materialHelp
        coEvery { iHelpRepository.delete(materialHelp.id!!) } returns true

        val result = deleteHelpUseCase(materialHelp.id!!, admin)
        assertTrue(result)
        coVerify { iHelpRepository.delete(materialHelp.id!!) }
    }

    @Test
    fun deleteHelpAsRecipient() = runBlocking {
        coEvery { iHelpRepository.findById(financialHelp.id!!) } returns financialHelp
        coEvery { iHelpRepository.delete(financialHelp.id!!) } returns true

        val result = deleteHelpUseCase(financialHelp.id!!, recipient)
        assertTrue(result)
    }

    @Test
    fun deleteHelpFailsForVolunteer() = runBlocking {
        coEvery { iHelpRepository.findById(financialHelp.id!!) } returns financialHelp
        val ex = assertFailsWith<SecurityException> {
            deleteHelpUseCase(financialHelp.id!!, volunteer)
        }
        assertEquals("Ви не маєте доступу до цієї дії.", ex.message)
    }

    @Test
    fun getAllHelpsAsAdmin() = runBlocking {
        val list = listOf(financialHelp, materialHelp)
        coEvery { iHelpRepository.getAll() } returns list

        val result = getAllHelpsUseCase(admin)
        assertEquals(list, result)
    }

    @Test
    fun helpImplementsUserIdentifiableCorrectly() {
        assertEquals(financialHelp.idRecipient, financialHelp.idUser)
        assertEquals(materialHelp.idRecipient, materialHelp.idUser)
    }
}