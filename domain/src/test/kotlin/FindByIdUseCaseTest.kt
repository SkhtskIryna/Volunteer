import eu.tutorials.domain.model.Donation
import eu.tutorials.domain.model.User
import eu.tutorials.domain.model.UserRole
import eu.tutorials.domain.repository.IRepository
import eu.tutorials.domain.usecase.generics.FindByIdUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FindByIdUseCaseTest {
    private val iDonationRepository = mockk<IRepository<Donation>>()
    private val findByIdUseCase =
        FindByIdUseCase(iDonationRepository, listOf(UserRole.Volunteer))

    private val donationId = 4

    private val donation = Donation(
        id = donationId,
        sum = 200.45,
        idVolunteer = 6,
        idFinancialRequest = 10
    )

    private val volunteer1 = User(
        id = 6,
        role = UserRole.Volunteer,
        firstName = "Jody",
        lastName = "Tanner",
        phone = "(440) 631-6717",
        email = "raymundo.mcdowell@example.com",
        telegram = "amet",
        isBlocked = false
    )

    private val volunteer2 = User(
        id = 4,
        role = UserRole.Volunteer,
        firstName = "Fran Ryan",
        lastName = "Keri Frost",
        phone = "(843) 227-5367",
        email = "josephine.steele@example.com",
        telegram = "civibus",
        isBlocked = false
    )

    private val admin = User(
        id = 9,
        role = UserRole.Admin,
        firstName = "Eduardo Rose",
        lastName = "Austin Harvey",
        phone = "(784) 887-1666",
        email = "sylvester.mendoza@example.com",
        telegram = "tamquam",
        isBlocked = false
    )

    @Test
    fun findByIdHasPermission() = runBlocking {
        coEvery { iDonationRepository.findById(donationId) } returns donation
        val result = findByIdUseCase.invoke(donationId, volunteer1)
        assertEquals(donation.id, result?.id)
        coVerify { iDonationRepository.findById(donationId) }
    }

    @Test
    fun findByIdHasNoPermission1() = runBlocking {
        coEvery { iDonationRepository.findById(donationId) } returns donation
        assertFailsWith<SecurityException> {
            findByIdUseCase.invoke(donationId, admin)
        }
        coVerify(exactly = 0) { iDonationRepository.findById(any()) }
    }

    @Test
    fun findByIdHasNoPermission2() = runBlocking {
        coEvery { iDonationRepository.findById(donationId) } returns donation
        assertFailsWith<SecurityException> {
            findByIdUseCase.invoke(donationId, volunteer2)
        }
        coVerify { iDonationRepository.findById(any()) }
    }
}
