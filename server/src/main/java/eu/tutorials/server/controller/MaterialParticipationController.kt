package eu.tutorials.server.controller

import eu.tutorials.domain.model.MaterialParticipation
import eu.tutorials.domain.usecase.CreateMaterialParticipationUseCase
import eu.tutorials.domain.usecase.DeleteMaterialParticipationUseCase
import eu.tutorials.domain.usecase.FindMaterialParticipationByIdUseCase
import eu.tutorials.domain.usecase.GetAllMaterialParticipationUseCase
import eu.tutorials.domain.usecase.UpdateMaterialParticipationUseCase
import eu.tutorials.server.security.UserPrincipal
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import kotlin.text.toIntOrNull

class MaterialParticipationController(
    private val createMaterialParticipation: CreateMaterialParticipationUseCase,
    private val findMaterialParticipationById: FindMaterialParticipationByIdUseCase,
    private val getAllMaterialParticipation: GetAllMaterialParticipationUseCase,
    private val updateMaterialParticipation: UpdateMaterialParticipationUseCase,
    private val deleteMaterialParticipation: DeleteMaterialParticipationUseCase
) {

    suspend fun create(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)

        val materialParticipation = call.receive<MaterialParticipation>()
        val created = createMaterialParticipation(materialParticipation,principal.user)
        call.respond(created)
    }

    suspend fun findById(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return call.respondText("Invalid material participation id", status = HttpStatusCode.BadRequest)

        val participation = findMaterialParticipationById(id, principal.user)
            ?: return call.respondText("Material participation not found", status = HttpStatusCode.NotFound)

        call.respond(participation)
    }

    suspend fun getAll(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)

        val list = getAllMaterialParticipation(principal.user)
        call.respond(list)
    }

    suspend fun update(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)

        val materialParticipation = call.receive<MaterialParticipation>()
        val updated = updateMaterialParticipation(materialParticipation, principal.user)
        if (!updated) {
            call.respondText("Material participation not found", status = HttpStatusCode.NotFound)
            return
        }

        call.respondText("Material participation updated successfully", status = HttpStatusCode.OK)
    }

    suspend fun delete(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return call.respondText("Invalid material participation id", status = HttpStatusCode.BadRequest)

        val deleted = deleteMaterialParticipation(id, principal.user)
        if (!deleted) {
            call.respondText("Material participation not found", status = HttpStatusCode.NotFound)
            return
        }

        call.respondText("Material participation deleted successfully", status = HttpStatusCode.OK)
    }
}