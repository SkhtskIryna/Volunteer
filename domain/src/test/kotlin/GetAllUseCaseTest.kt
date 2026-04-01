import eu.tutorials.domain.model.Bin
import eu.tutorials.domain.model.Card
import eu.tutorials.domain.model.User
import eu.tutorials.domain.model.UserRole
import eu.tutorials.domain.repository.IRepository
import eu.tutorials.domain.usecase.generics.GetAllUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.YearMonth
import kotlin.test.assertEquals
import kotlin.test.assertFails

class GetAllUseCaseTest {
    private val iBinRepository = mockk<IRepository<Bin>>()
    private val getAllUseCase = GetAllUseCase(iBinRepository, listOf(UserRole.Admin))

    private val bin = Bin(
        id = 3,
        idRecipient = 5
    )

    private val recipient = User(
        id = 5,
        role = UserRole.Recipient,
        firstName = "Maryann",
        lastName = "Morris",
        phone = "(508) 198-2022",
        email = "antonio.conner@example.com",
        telegram = "bibendum",
        isBlocked = false,
        _cards = mutableListOf(Card(
            id = 3,
            number = "5646 3737 4747 3747",
            validityPeriod = YearMonth.parse("2021-03"),
            idRecipient = 5
        ))
    )

    private val admin = User(
        id = 3,
        role = UserRole.Admin,
        firstName = "Carlson",
        lastName = "Oneal",
        phone = "(351) 863-1926",
        email = "kathy.fuller@example.com",
        telegram = "quod",
        isBlocked = false
    )

    @Test
    fun getAllSuccessfully() = runBlocking {
        val list = listOf(bin)
        coEvery { iBinRepository.getAll() } returns list
        val result = getAllUseCase.invoke(admin)
        assertEquals(list,result)
        coVerify { iBinRepository.getAll() }
    }

    @Test
    fun getAllAccessDenied() = runBlocking {
        coEvery { iBinRepository.getAll() } returns listOf(bin)
        assertFails {
            "Користувачу у доступі відмовлено."
            getAllUseCase.invoke(recipient)
        }
        coVerify(exactly = 0) { iBinRepository.getAll() }
    }
}