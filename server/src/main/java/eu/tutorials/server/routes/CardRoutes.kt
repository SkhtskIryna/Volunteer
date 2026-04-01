package eu.tutorials.server.routes

import eu.tutorials.server.controller.CardController
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.cardRoutes(cardController: CardController){
    route("/card") {
        post {
            cardController.create(call)
        }
        authenticate("auth-basic") {
            get {
                cardController.getAll(call)
            }
            get ("/{id}"){
                cardController.findById(call)
            }
            put("/{id}") {
                cardController.update(call)
            }
            delete("/{id}") {
                cardController.delete(call)
            }
        }
    }
}