package eu.tutorials.server.routes

import eu.tutorials.server.controller.MaterialParticipationController
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.materialParticipationRoutes(materialParticipationController: MaterialParticipationController){
    route("/materialParticipation") {
        authenticate("auth-basic") {
            post {
                materialParticipationController.create(call)
            }
            get("/{id}") {
                materialParticipationController.findById(call)
            }
            get {
                materialParticipationController.getAll(call)
            }
            put("/{id}") {
                materialParticipationController.update(call)
            }
            delete("/{id}") {
                materialParticipationController.delete(call)
            }
        }
    }
}