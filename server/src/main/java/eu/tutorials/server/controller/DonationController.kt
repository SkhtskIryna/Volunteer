package eu.tutorials.server.controller

import eu.tutorials.domain.model.Donation
import eu.tutorials.domain.usecase.CreateDonationUseCase
import eu.tutorials.domain.usecase.DeleteDonationUseCase
import eu.tutorials.domain.usecase.FindDonationByIdUseCase
import eu.tutorials.domain.usecase.GetAllDonationsUseCase
import eu.tutorials.server.security.UserPrincipal
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import kotlin.text.toIntOrNull

class DonationController(
    private val createDonation: CreateDonationUseCase,
    private val findDonationById: FindDonationByIdUseCase,
    private val getAllDonations: GetAllDonationsUseCase,
    private val deleteDonation: DeleteDonationUseCase
) {
    suspend fun create(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)

        val donation = call.receive<Donation>()
        val created = createDonation(donation, principal.user)
        call.respond(created)
    }

    suspend fun findById(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return call.respondText("Invalid donation id", status = HttpStatusCode.BadRequest)

        val donation = findDonationById(id, principal.user)
            ?: return call.respondText("Donation not found", status = HttpStatusCode.NotFound)

        call.respond(donation)
    }

    suspend fun getAll(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)

        val donations = getAllDonations(principal.user)
        call.respond(donations)
    }

    suspend fun delete(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return call.respondText("Invalid donation id", status = HttpStatusCode.BadRequest)

        val deleted = deleteDonation(id, principal.user)
        if (!deleted) {
            call.respondText("Donation not found", status = HttpStatusCode.NotFound)
            return
        }

        call.respondText("Donation deleted successfully", status = HttpStatusCode.OK)
    }
}