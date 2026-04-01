import eu.tutorials.domain.model.Card
import eu.tutorials.domain.model.MaterialParticipation
import eu.tutorials.domain.model.MaterialParticipationStatus
import eu.tutorials.domain.model.User
import eu.tutorials.domain.model.UserRole
import eu.tutorials.domain.repository.IHelpRepository
import eu.tutorials.domain.repository.IMaterialParticipationRepository
import eu.tutorials.domain.usecase.CreateMaterialParticipationUseCase
import eu.tutorials.domain.usecase.DeleteMaterialParticipationUseCase
import eu.tutorials.domain.usecase.FindMaterialParticipationByIdUseCase
import eu.tutorials.domain.usecase.GetAllMaterialParticipationUseCase
import eu.tutorials.domain.usecase.UpdateMaterialParticipationUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.YearMonth
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MaterialParticipationUseCaseTest {
    private val iMaterialParticipationRepository = mockk<IMaterialParticipationRepository>()
    private val iHelpRepository = mockk<IHelpRepository>()

    private val createMaterialParticipationUseCase = CreateMaterialParticipationUseCase(iMaterialParticipationRepository)
    private val deleteMaterialParticipationUseCase = DeleteMaterialParticipationUseCase(iMaterialParticipationRepository)
    private val findMaterialParticipationByIdUseCase = FindMaterialParticipationByIdUseCase(
        iMaterialParticipationRepository,
        iHelpRepository
    )
    private val getAllMaterialParticipationUseCase = GetAllMaterialParticipationUseCase(iMaterialParticipationRepository)
    private val updateMaterialParticipationUseCase = UpdateMaterialParticipationUseCase(iMaterialParticipationRepository)

    private val volunteer = User(
        id = 1,
        role = UserRole.Volunteer,
        firstName = "Vol",
        lastName = "User",
        phone = "123",
        email = "v@e.com",
        telegram = "@volunteer"
    )
    private val recipient = User(
        id = 2,
        role = UserRole.Recipient,
        firstName = "Rec",
        lastName = "User",
        phone = "456",
        email = "r@e.com",
        telegram = "",
        _cards = mutableListOf(
            Card(
                id = 4,
                number = "7464 2626 7371 2632",
                validityPeriod = YearMonth.parse("2022-06"),
                idRecipient = 2
            )
        )
    )

    private val unknownRoleUser = User(
        id = 3,
        role = UserRole.Admin,
        firstName = "Admin",
        lastName = "User",
        phone = "789",
        email = "a@e.com",
        telegram = ""
    )

    private val participation = MaterialParticipation(
        id = 1,
        status = MaterialParticipationStatus.Registered,
        idVolunteer = 1,
        idMaterialRequest = 5
    )

    @Test
    fun createMaterialParticipationSuccessfully() = runBlocking {
        coEvery { iMaterialParticipationRepository.create(participation) } returns participation
        coEvery { iMaterialParticipationRepository.findById(participation.id!!) } returns participation

        val result = createMaterialParticipationUseCase(participation, volunteer)
        assertEquals(participation, result)
        coVerify { iMaterialParticipationRepository.create(participation) }
    }

    @Test
    fun deleteMaterialParticipationSuccessfully() = runBlocking {
        coEvery { iMaterialParticipationRepository.findById(participation.id!!) } returns participation
        coEvery { iMaterialParticipationRepository.delete(participation.id!!) } returns true

        val result = deleteMaterialParticipationUseCase(participation.id!!, volunteer)
        assertTrue(result)
        coVerify { iMaterialParticipationRepository.delete(participation.id!!) }
    }

    @Test
    fun findMaterialParticipationByIdSuccessfully() = runBlocking {
        coEvery { iMaterialParticipationRepository.findById(participation.id!!) } returns participation

        val result = findMaterialParticipationByIdUseCase(participation.id!!, volunteer)
        assertEquals(participation, result)
        coVerify { iMaterialParticipationRepository.findById(participation.id!!) }
    }

    @Test
    fun getAllMaterialParticipationSuccessfully() = runBlocking {
        val list = listOf(participation)
        coEvery { iMaterialParticipationRepository.getAll() } returns list

        val result = getAllMaterialParticipationUseCase(volunteer)
        assertEquals(list, result)
        coVerify { iMaterialParticipationRepository.getAll() }
    }

    @Test
    fun volunteerUpdateStatusToInProgress() = runBlocking {
        val updated = participation.copy(status = MaterialParticipationStatus.InProgress)

        coEvery { iMaterialParticipationRepository.findById(participation.id!!) } returns participation
        coEvery { iMaterialParticipationRepository.update(any()) } returns true

        val result = updateMaterialParticipationUseCase(updated, volunteer)
        assertTrue(result)
        coVerify { iMaterialParticipationRepository.update(match { it.status == MaterialParticipationStatus.InProgress }) }
    }

    @Test
    fun recipientUpdateStatusToDelivered() = runBlocking {
        coEvery { iMaterialParticipationRepository.findById(participation.id!!) } returns participation
        coEvery { iMaterialParticipationRepository.update(any()) } returns true

        val result = updateMaterialParticipationUseCase(
            participation.copy(status = MaterialParticipationStatus.Delivered),
            recipient
        )

        assertTrue(result)

        coVerify {
            iMaterialParticipationRepository.update(match {
                it.status == MaterialParticipationStatus.Delivered &&
                        it.deliveredAt != null
            })
        }
    }

    @Test
    fun recipientUpdateStatusToInProgress() = runBlocking {
        val invalid = participation.copy(status = MaterialParticipationStatus.InProgress)

        coEvery { iMaterialParticipationRepository.findById(participation.id!!) } returns participation

        val exception = assertFailsWith<SecurityException> {
            updateMaterialParticipationUseCase(invalid, recipient)
        }

        assertEquals("You don't have permission to set this status", exception.message)
    }

    @Test
    fun materialParticipationImplementsInterfacesCorrectly() {
        assertEquals(participation.idVolunteer, participation.idUser)
        assertEquals(participation.idMaterialRequest, participation.idHelp)
    }
}
