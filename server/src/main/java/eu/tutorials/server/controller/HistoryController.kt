package eu.tutorials.server.controller

import eu.tutorials.domain.model.Bin
import eu.tutorials.domain.model.History
import eu.tutorials.domain.usecase.CreateHistoryUseCase
import eu.tutorials.domain.usecase.DeleteHistoryUseCase
import eu.tutorials.domain.usecase.FilterHistoriesByRecipientFullNameUseCase
import eu.tutorials.domain.usecase.FindHistoryByIdUseCase
import eu.tutorials.domain.usecase.GetAllHistoriesUseCase
import eu.tutorials.server.security.UserPrincipal
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText

class HistoryController(
    private val createHistory: CreateHistoryUseCase,
    private val findHistoryById: FindHistoryByIdUseCase,
    private val getAllHistories: GetAllHistoriesUseCase,
    private val deleteHistory: DeleteHistoryUseCase,
    private val filterHistoriesByRecipientFullName: FilterHistoriesByRecipientFullNameUseCase
) {
    suspend fun create(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)

        val history = call.receive<History>()
        val created = createHistory(history, principal.user)
        call.respond(created)
    }

    suspend fun findById(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return call.respondText("Invalid history id", status = HttpStatusCode.BadRequest)

        val history = findHistoryById(id, principal.user)
            ?: return call.respondText("History not found", status = HttpStatusCode.NotFound)

        call.respond(history)
    }

    suspend fun getAll(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)

        val histories = getAllHistories(principal.user)
        call.respond(histories)
    }

    suspend fun delete(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return call.respondText("Invalid history id", status = HttpStatusCode.BadRequest)

        val history = findHistoryById(id,principal.user)
            ?: return call.respondText("History not found", status = HttpStatusCode.NotFound)

        val bin = call.receive<Bin>()
        val deleted = deleteHistory(history, principal.user, bin)
        if (!deleted) {
            call.respondText("Cannot delete history", status = HttpStatusCode.BadRequest)
            return
        }

        call.respondText("History deleted successfully", status = HttpStatusCode.OK)
    }

    suspend fun filter(call: ApplicationCall) {
        val query = call.parameters["q"] ?: ""
        val histories = filterHistoriesByRecipientFullName(query)
        call.respond(histories)
    }
}