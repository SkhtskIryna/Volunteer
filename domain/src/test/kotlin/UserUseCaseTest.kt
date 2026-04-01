
import eu.tutorials.domain.model.Card
import eu.tutorials.domain.model.User
import eu.tutorials.domain.model.UserRole
import eu.tutorials.domain.repository.IUserRepository
import eu.tutorials.domain.security.IPasswordHashed
import eu.tutorials.domain.security.IPhotoEncoder
import eu.tutorials.domain.usecase.CreateUserUseCase
import eu.tutorials.domain.usecase.DeleteUserUseCase
import eu.tutorials.domain.usecase.FindUserByEmailUseCase
import eu.tutorials.domain.usecase.FindUserByIdUseCase
import eu.tutorials.domain.usecase.GetAllUsersUseCase
import eu.tutorials.domain.usecase.GetPasswordHashUseCase
import eu.tutorials.domain.usecase.UpdateUserUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.YearMonth
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserUseCaseTest {
    private val iUserRepository = mockk<IUserRepository>()
    private val passwordHashed = mockk<IPasswordHashed>()
    private val iPhotoEncoder = mockk<IPhotoEncoder>()

    private val createUserUseCase = CreateUserUseCase(
        iUserRepository,
        passwordHashed
    )
    private val deleteUserUseCase = DeleteUserUseCase(iUserRepository)
    private val findUserByIdUseCase = FindUserByIdUseCase(iUserRepository)
    private val getAllUsersUseCase = GetAllUsersUseCase(iUserRepository)
    private val updateUserUseCase = UpdateUserUseCase(
        iUserRepository,
        passwordHashed,
        iPhotoEncoder
    )
    private val findUserByEmailUseCase = FindUserByEmailUseCase(iUserRepository)
    private val getPasswordHashUseCase = GetPasswordHashUseCase(iUserRepository, passwordHashed)

    private val admin = User(
        id = 1,
        firstName = "Admin",
        lastName = "User",
        role = UserRole.Admin,
        phone = "123",
        email = "admin@mail.com",
        telegram = "")

    private val recipient = User(
        id = 2,
        firstName = "Ivan",
        lastName = "Ivanov",
        role = UserRole.Recipient,
        phone = "456",
        email = "ivan@mail.com",
        telegram = "@res",
        _cards = mutableListOf(Card(
            id = 5,
            number = "7464 2626 7371 2632",
            validityPeriod = YearMonth.parse("2022-06"),
            idRecipient = 4
        )))

    @Test
    fun deleteUserByAdmin() = runBlocking {
        coEvery { iUserRepository.findById(recipient.id!!) } returns recipient
        coEvery { iUserRepository.delete(recipient.id!!) } returns true

        val result = deleteUserUseCase(recipient.id!!, admin)

        assertTrue(result)
        coVerify { iUserRepository.findById(recipient.id!!) }
        coVerify { iUserRepository.delete(recipient.id!!) }
    }

    @Test
    fun findUserByIdByAdmin() = runBlocking {
        coEvery { iUserRepository.findById(recipient.id!!) } returns recipient

        val result = findUserByIdUseCase(recipient.id!!)

        assertEquals(recipient, result)
        coVerify { iUserRepository.findById(recipient.id!!) }
    }

    @Test
    fun getAllUsersByAdmin() = runBlocking {
        coEvery { iUserRepository.getAll() } returns listOf(admin, recipient)

        val result = getAllUsersUseCase()

        assertEquals(2, result.size)
        assertTrue(result.contains(recipient))
    }

    @Test
    fun updateUserTest() = runBlocking {
        val updated = recipient.copy(firstName = "IvanUpdated")
        coEvery { iUserRepository.update(updated) } returns true

        val result = updateUserUseCase(
            updated,
            rawPassword = null,
            rawPhotoBytes = null
        )

        assertTrue(result)
        coVerify { iUserRepository.update(updated) }
    }

    @Test
    fun findUserByEmailTest() = runBlocking {
        coEvery { iUserRepository.findByEmail("ivan@mail.com") } returns recipient

        val result = findUserByEmailUseCase("ivan@mail.com")

        assertEquals(recipient, result)
        coVerify { iUserRepository.findByEmail("ivan@mail.com") }
    }
}