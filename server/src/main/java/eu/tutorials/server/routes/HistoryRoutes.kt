package eu.tutorials.server.routes

import eu.tutorials.server.controller.HistoryController
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.historyRoutes(historyController: HistoryController){
    route("/history") {
        authenticate("auth-basic") {
            post {
                historyController.create(call)
            }
            get("/{id}") {
                historyController.findById(call)
            }
            get {
                historyController.getAll(call)
            }
            delete("/{id}") {
                historyController.delete(call)
            }
            get("/filter") {
                historyController.filter(call)
            }
        }
    }
}