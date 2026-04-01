import eu.tutorials.domain.model.Donation
import eu.tutorials.domain.model.Help
import eu.tutorials.domain.model.MaterialCategory
import eu.tutorials.domain.model.User
import eu.tutorials.domain.model.UserRole
import eu.tutorials.domain.repository.IDonationRepository
import eu.tutorials.domain.repository.IHelpRepository
import eu.tutorials.domain.usecase.CreateDonationUseCase
import eu.tutorials.domain.usecase.DeleteDonationUseCase
import eu.tutorials.domain.usecase.FindDonationByIdUseCase
import eu.tutorials.domain.usecase.GetAllDonationsUseCase
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

class DonationUseCaseTest {
    private val iDonationRepository = mockk<IDonationRepository>()
    private val iHelpRepository = mockk<IHelpRepository>()
    private val deleteDonationUseCase = DeleteDonationUseCase(iDonationRepository)
    private val getAllDonationsUseCase = GetAllDonationsUseCase(iDonationRepository)
    private val findDonationByIdUseCase = FindDonationByIdUseCase(iDonationRepository)
    private val createDonationUseCase = CreateDonationUseCase(iDonationRepository)

    private val donationId = 1

    private val donation = Donation(
        id = donationId,
        sum = 150.0,
        idVolunteer = 3,
        idFinancialRequest = 5
    )

    private val volunteer = User(
        id = 3,
        role = UserRole.Volunteer,
        firstName = "Volunteer",
        lastName = "User",
        phone = "111",
        email = "vol@example.com",
        telegram = ""
    )

    private val financialHelp = Help.Financial(
        id = 5,
        title = "Medical Treatment",
        description = "Urgent surgery needed",
        idRecipient = 10,
        from = LocalDate.now().minusDays(10),
        to = LocalDate.now().plusDays(10),
        plannedAmount = 1000.0,
        collected = 150.0,
        createdAt = LocalDateTime.now().minusDays(10),
        updatedAt = null
    )

    private val materialHelp = Help.Material(
        id = 5,
        title = "Medical Treatment",
        description = "Urgent surgery needed",
        idRecipient = 10,
        category = MaterialCategory.Food.toString(),
        region = "partiendo",
        area = "epicurei",
        city = "New Diggings",
        createdAt = LocalDateTime.parse("2021-03-13T00:00:00"),
        updatedAt = null
    )

    @Test
    fun createDonationSuccessfully() = runBlocking {
        coEvery { iDonationRepository.findById(donationId) } returns donation
        coEvery { iDonationRepository.create(donation) } returns donation
        val result = createDonationUseCase(donation, volunteer)
        assertEquals(donation, result)
        coVerify { iDonationRepository.create(donation) }
    }

    @Test
    fun findDonationByIdSuccessfully() = runBlocking {
        coEvery { iDonationRepository.findById(donationId) } returns donation
        val result = findDonationByIdUseCase.invoke(donationId, volunteer)
        assertEquals(donation.idVolunteer, result?.idVolunteer)
        coVerify { iDonationRepository.findById(donationId) }
    }

    @Test
    fun deleteDonationSuccessfully() = runBlocking {
        coEvery { iDonationRepository.findById(donationId) } returns donation
        coEvery { iDonationRepository.delete(donationId) } returns true
        val result = deleteDonationUseCase(donationId, volunteer)
        assertTrue(result)
        coVerify { iDonationRepository.delete(donationId) }
    }

    @Test
    fun getAllDonationsSuccessfully() = runBlocking {
        val list = listOf(donation)
        coEvery { iDonationRepository.getAll() } returns list
        val result = getAllDonationsUseCase.invoke(volunteer)
        assertEquals(list, result)
        coVerify { iDonationRepository.getAll() }
    }

    @Test
    fun donationPointsToExistingFinancialRequest() = runBlocking {
        coEvery { iHelpRepository.findById(5) } returns financialHelp

        val foundHelp = iHelpRepository.findById(donation.idFinancialRequest)

        assertTrue(foundHelp is Help.Financial)
        assertEquals(financialHelp.id, (foundHelp as Help.Financial).id)
        assertEquals(5, donation.idFinancialRequest)

        coVerify { iHelpRepository.findById(5) }
    }

    @Test
    fun donationHelpValidation() = runBlocking {
        val expected = donation.idFinancialRequest
        val actual = donation.idHelp
        assertEquals(expected, actual)

        coEvery { iHelpRepository.findById(actual) } returns materialHelp

        val exception = assertFailsWith<IllegalArgumentException> {
            val help = iHelpRepository.findById(actual)
            if (help !is Help.Financial) {
                throw IllegalArgumentException("Донати можна робити тільки на запит фінансової допомоги")
            }
        }

        assertEquals("Донати можна робити тільки на запит фінансової допомоги", exception.message)
    }
}
