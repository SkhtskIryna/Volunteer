import eu.tutorials.domain.model.Card
import eu.tutorials.domain.model.User
import eu.tutorials.domain.model.UserRole
import eu.tutorials.domain.repository.IBinRepository
import eu.tutorials.domain.repository.IUserRepository
import eu.tutorials.domain.security.IPasswordHashed
import eu.tutorials.domain.usecase.CreateUserUseCase
import eu.tutorials.domain.usecase.UnblockedUserUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import java.time.YearMonth
import kotlin.test.assertFails
import kotlin.test.assertTrue

class UnblockedUserUseCaseTest {
    private val iBinRepository = mockk<IBinRepository>()
    private val iUserRepository = mockk<IUserRepository>()
    private val iPasswordHashed = mockk<IPasswordHashed>()

    private val unblockedUser = UnblockedUserUseCase(iBinRepository)
    private val createUserUseCase = CreateUserUseCase(
        iUserRepository,
        iPasswordHashed
    )

    private val card = Card(
        id = 1,
        number = "**** **** **** 4563",
        validityPeriod = YearMonth.parse("2021-01"),
        idRecipient = 6
    )

    private val admin = User(
        id = 4,
        firstName = "Ashley",
        lastName = "Finch",
        role = UserRole.Admin,
        phone = "(725) 349-6034",
        email = "courtney.mann@example.com",
        telegram = "et",
        isBlocked = false
    )
    private val recipient = User(
        id = 4,
        firstName = "Ashlee",
        lastName = "England",
        role = UserRole.Recipient,
        phone = "(429) 924-1731",
        email = "morris.justice@example.com",
        telegram = "proin",
        isBlocked = true
    )

    @Test
    fun unblockedUserAccessDenied() = runBlocking {
        coEvery { iBinRepository.unblockedUser(admin.id!!) } returns admin
        assertFails {
            "Відмовлено в доступі до стороннього ресурсу."
            unblockedUser(recipient, admin.id!!)
        }
        coVerify(exactly = 0) { iBinRepository.unblockedUser(any()) }
    }

    @Test
    fun unblockedUserSuccessfully() = runBlocking {
        coEvery { iBinRepository.unblockedUser(recipient.id!!) } returns recipient.copy(isBlocked = false)
        val result = unblockedUser(admin, recipient.id!!)
        assertTrue(recipient.isBlocked)
        assertFalse(result.isBlocked)
        coVerify { iBinRepository.unblockedUser(recipient.id!!) }
    }
}