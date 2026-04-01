import eu.tutorials.domain.model.Bin
import eu.tutorials.domain.model.User
import eu.tutorials.domain.model.UserRole
import eu.tutorials.domain.repository.IBinRepository
import eu.tutorials.domain.repository.IUserRepository
import eu.tutorials.domain.usecase.CreateBinUseCase
import eu.tutorials.domain.usecase.DeleteBinUseCase
import eu.tutorials.domain.usecase.FilterBinsByRecipientNameUseCase
import eu.tutorials.domain.usecase.FindBinByIdUseCase
import eu.tutorials.domain.usecase.GetAllBinsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class BinUseCaseTest {
    private val iBinRepository = mockk<IBinRepository>()
    private val iUserRepository = mockk<IUserRepository>()

    private val deleteBinUseCase = DeleteBinUseCase(iBinRepository)
    private val getAllBinsUseCase = GetAllBinsUseCase(iBinRepository)
    private val findBinByIdUseCase = FindBinByIdUseCase(iBinRepository)
    private val createBinUseCase = CreateBinUseCase(iBinRepository)
    private val filterBinsByRecipientNameUseCase = FilterBinsByRecipientNameUseCase(iBinRepository, iUserRepository)

    private val binId = 3

    private val bin = Bin(
        id = binId,
        idRecipient = 5
    )

    private val admin = User(
        id = 1,
        firstName = "Admin",
        lastName = "User",
        role = UserRole.Admin,
        phone = "000",
        email = "admin@example.com",
        telegram = ""
    )

    private val recipient = User(
        id = 5,
        role = UserRole.Recipient,
        firstName = "Recipient1",
        lastName = "Test",
        phone = "111",
        email = "recipient1@example.com",
        _cards = mutableListOf(),
        telegram = "@res"
    )

    @Test
    fun createBinAccessDenied() = runBlocking {
        coEvery { iBinRepository.findById(binId) } returns bin
        coEvery { iBinRepository.create(bin) } returns bin
        assertFailsWith<SecurityException> {
            createBinUseCase(bin, recipient)
        }
        coVerify(exactly = 0) { iBinRepository.create(any()) }
    }

    @Test
    fun createBinSuccessfully() = runBlocking {
        coEvery { iBinRepository.findById(binId) } returns bin
        coEvery { iBinRepository.create(bin) } returns bin
        val result = createBinUseCase(bin, admin)
        assertEquals(bin, result)
        coVerify { iBinRepository.create(bin) }
    }

    @Test
    fun findBinByIdSuccessfully() = runBlocking {
        coEvery { iBinRepository.findById(binId) } returns bin
        val result = findBinByIdUseCase.invoke(binId, admin)
        assertEquals(bin.idRecipient, result?.idRecipient)
        coVerify { iBinRepository.findById(binId) }
    }

    @Test
    fun deleteBinSuccessfully() = runBlocking {
        coEvery { iBinRepository.findById(binId) } returns bin
        coEvery { iBinRepository.delete(binId) } returns true
        val result = deleteBinUseCase(binId, admin)
        assertTrue(result)
        coVerify { iBinRepository.delete(binId) }
    }

    @Test
    fun getAllBinsSuccessfully() = runBlocking {
        val list = listOf(bin)
        coEvery { iBinRepository.getAll() } returns list
        val result = getAllBinsUseCase.invoke(admin)
        assertEquals(list, result)
        coVerify { iBinRepository.getAll() }
    }

    @Test
    fun filterBinsByUserFullNameTest() = runBlocking {
        val users = listOf(admin, recipient)
        val bins = listOf(bin)

        coEvery { iBinRepository.getAll() } returns bins
        coEvery { iUserRepository.getAll() } returns users

        val result = filterBinsByRecipientNameUseCase("tEst Recipient1")
        assertEquals(binId, result.first().id)
    }
}