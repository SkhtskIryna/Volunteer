package eu.tutorials.server.controller

import eu.tutorials.domain.model.Help
import eu.tutorials.domain.usecase.CreateHelpUseCase
import eu.tutorials.domain.usecase.DeleteHelpUseCase
import eu.tutorials.domain.usecase.FindHelpByIdUseCase
import eu.tutorials.domain.usecase.GetAllHelpsUseCase
import eu.tutorials.domain.usecase.UpdateHelpUseCase
import eu.tutorials.server.security.UserPrincipal
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText

class HelpController(
    private val createHelp: CreateHelpUseCase,
    private val findHelpById: FindHelpByIdUseCase,
    private val getAllHelps: GetAllHelpsUseCase,
    private val deleteHelp: DeleteHelpUseCase,
    private val updateHelp: UpdateHelpUseCase
) {
    suspend fun create(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)

        val help = call.receive<Help>()
        val created = createHelp(help, principal.user)
        call.respond(created)
    }

    suspend fun findById(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return call.respondText("Invalid help id", status = HttpStatusCode.BadRequest)

        val help = findHelpById(id, principal.user)
            ?: return call.respondText("Help not found", status = HttpStatusCode.NotFound)

        call.respond(help)
    }

    suspend fun getAll(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)

        val helps = getAllHelps(principal.user)
        call.respond(helps)
    }

    suspend fun update(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)

        val help = call.receive<Help>()
        val updated = updateHelp(help, principal.user)
        if (!updated) {
            call.respondText("Help not found", status = HttpStatusCode.NotFound)
            return
        }

        call.respondText("Help updated successfully", status = HttpStatusCode.OK)
    }

    suspend fun delete(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return call.respondText("Invalid help id", status = HttpStatusCode.BadRequest)

        val deleted = deleteHelp(id, principal.user)
        if (!deleted) {
            call.respondText("Help not found", status = HttpStatusCode.NotFound)
            return
        }

        call.respondText("Help deleted successfully", status = HttpStatusCode.OK)
    }
}