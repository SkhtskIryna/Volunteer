package eu.tutorials.server.controller

import eu.tutorials.domain.model.Card
import eu.tutorials.domain.usecase.CreateCardUseCase
import eu.tutorials.domain.usecase.DeleteCardUseCase
import eu.tutorials.domain.usecase.FindCardByIdUseCase
import eu.tutorials.domain.usecase.GetAllCardsUseCase
import eu.tutorials.domain.usecase.UpdateCardUseCase
import eu.tutorials.server.security.UserPrincipal
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText

class CardController(
    private val createCard: CreateCardUseCase,
    private val findCardById: FindCardByIdUseCase,
    private val getAllCards: GetAllCardsUseCase,
    private val updateCard: UpdateCardUseCase,
    private val deleteCard: DeleteCardUseCase
) {
    suspend fun create(call: ApplicationCall) {
        val card = call.receive<Card>()
        val created = createCard(card)
        call.respond(created)
    }

    suspend fun findById(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return call.respondText("Invalid card id", status = HttpStatusCode.BadRequest)

        val card = findCardById(id, principal.user)
            ?: return call.respondText("Card not found", status = HttpStatusCode.NotFound)

        call.respond(card)
    }

    suspend fun getAll(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)

        val cards = getAllCards(principal.user)
        call.respond(cards)
    }

    suspend fun update(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)

        val received = call.receive<Card>()

        val updated = updateCard(received, principal.user)

        if (!updated) {
            call.respondText("Card not found", status = HttpStatusCode.NotFound)
            return
        }

        call.respondText("Card updated successfully", status = HttpStatusCode.OK)
    }

    suspend fun delete(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return call.respondText("Invalid card id", status = HttpStatusCode.BadRequest)

        val deleted = deleteCard(id, principal.user)
        if (!deleted) {
            call.respondText("Card not found", status = HttpStatusCode.NotFound)
            return
        }

        call.respondText("Card deleted successfully", status = HttpStatusCode.OK)
    }
}