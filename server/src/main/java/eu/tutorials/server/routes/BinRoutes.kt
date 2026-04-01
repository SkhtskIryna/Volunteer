package eu.tutorials.server.routes

import eu.tutorials.server.controller.BinController
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.binRoutes(binController: BinController) {
    route("/bin") {
        authenticate("auth-basic") {
            post {
                binController.create(call)
            }
            get("/{id}") {
                binController.findById(call)
            }
            get {
                binController.getAll(call)
            }
            delete("/{id}") {
                binController.delete(call)
            }
            get("/filter") {
                binController.filterByRecipientName(call)
            }
            post("/unblock/{id}") {
                binController.unblockUser(call)
            }
        }
    }
}
