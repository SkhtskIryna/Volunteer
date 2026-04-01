package eu.tutorials.server.routes

import eu.tutorials.server.controller.HelpController
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.helpRoutes(helpController: HelpController){
    route("/help") {
        authenticate("auth-basic") {
            post {
                helpController.create(call)
            }
            get("/{id}") {
                helpController.findById(call)
            }
            get {
                helpController.getAll(call)
            }
            put("/{id}") {
                helpController.update(call)
            }
            delete("/{id}") {
                helpController.delete(call)
            }
        }
    }
}