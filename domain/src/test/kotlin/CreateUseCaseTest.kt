import eu.tutorials.domain.model.Bin
import eu.tutorials.domain.model.User
import eu.tutorials.domain.model.UserRole
import eu.tutorials.domain.repository.IRepository
import eu.tutorials.domain.usecase.generics.CreateUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CreateUseCaseTest {
    private val binRepository = mockk<IRepository<Bin>>()
    private val createUseCase = CreateUseCase(binRepository, listOf(UserRole.Admin))

    private val bin = Bin(id = 1, idRecipient = 10)
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
    private val volunteer = User(
        id = 2,
        role = UserRole.Volunteer,
        firstName = "Nadine Daugherty",
        lastName = "Olivia Atkinson",
        phone = "(491) 296-3685",
        email = "dirk.conway@example.com",
        telegram = "olivia",
        isBlocked = false
    )

    @Test
    fun userHasPermission() = runBlocking {
        coEvery { binRepository.create(bin) } returns bin

        val result = createUseCase(bin, admin)

        assertEquals(bin, result,
            "Користувач має доступ")
        coVerify { binRepository.create(bin) }
    }

    @Test
    fun userHasNoPermission() = runBlocking {
        assertFailsWith<SecurityException> {
            "Користувачу у доступі відмовлено."
            createUseCase(bin, volunteer)
        }
        coVerify(exactly = 0) { binRepository.create(any()) }
    }
}
