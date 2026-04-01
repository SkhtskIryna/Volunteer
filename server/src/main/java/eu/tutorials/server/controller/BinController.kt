package eu.tutorials.server.controller

import eu.tutorials.domain.model.Bin
import eu.tutorials.domain.model.User
import eu.tutorials.domain.usecase.CreateBinUseCase
import eu.tutorials.domain.usecase.DeleteBinUseCase
import eu.tutorials.domain.usecase.FilterBinsByRecipientNameUseCase
import eu.tutorials.domain.usecase.FindBinByIdUseCase
import eu.tutorials.domain.usecase.GetAllBinsUseCase
import eu.tutorials.domain.usecase.UnblockedUserUseCase
import eu.tutorials.server.security.UserPrincipal
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText

class BinController(
    private val createBin: CreateBinUseCase,
    private val findBinById: FindBinByIdUseCase,
    private val getAllBins: GetAllBinsUseCase,
    private val deleteBin: DeleteBinUseCase,
    private val filterBinsByRecipientName: FilterBinsByRecipientNameUseCase,
    private val unblockedUser: UnblockedUserUseCase
) {
    suspend fun create(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respondText(
                text = "Unauthorized",
                status = HttpStatusCode.Unauthorized
            )

        val bin = call.receive<Bin>()
        val created = createBin(bin, principal.user)
        call.respond(created)
    }

    suspend fun findById(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return call.respondText("Invalid bin id", status = HttpStatusCode.BadRequest)

        val bin = findBinById(id, principal.user)
            ?: return call.respondText("Bin not found", status = HttpStatusCode.NotFound)

        call.respond(bin)
    }

    suspend fun getAll(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)

        val bins = getAllBins(principal.user)
        call.respond(bins)
    }

    suspend fun delete(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return call.respondText("Invalid bin id", status = HttpStatusCode.BadRequest)

        val deleted = deleteBin(id, principal.user)
        if (!deleted) {
            call.respondText("Bin not found", status = HttpStatusCode.NotFound)
            return
        }

        call.respondText("Bin deleted successfully", status = HttpStatusCode.OK)
    }

    suspend fun filterByRecipientName(call: ApplicationCall) {
        val query = call.parameters["q"] ?: ""
        val bins = filterBinsByRecipientName(query)
        call.respond(bins)
    }

    suspend fun unblockUser(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)

        val recipientId = call.parameters["id"]?.toIntOrNull()
            ?: return call.respondText("Invalid user id", status = HttpStatusCode.BadRequest)

        val updated: User = unblockedUser(principal.user, recipientId)
        call.respond(updated)
    }
}
