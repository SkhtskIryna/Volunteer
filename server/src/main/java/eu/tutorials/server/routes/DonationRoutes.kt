package eu.tutorials.server.routes

import eu.tutorials.server.controller.DonationController
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.donationRoutes(donationController: DonationController){
    route("/donation") {
        authenticate("auth-basic") {
            post {
                donationController.create(call)
            }
            get("/{id}") {
                donationController.findById(call)
            }
            get {
                donationController.getAll(call)
            }
            delete("/{id}") {
                donationController.delete(call)
            }
        }
    }
}