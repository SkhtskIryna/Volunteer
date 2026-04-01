package eu.tutorials.server.routes

import eu.tutorials.server.controller.UserController
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.userRoutes(userController: UserController){
    route("/user"){
        post { userController.create(call) }
        get { userController.getAll(call) }
        post("/authenticate") { userController.authenticate(call) }
        get("/{id}") { userController.findById(call) }
        get("/email/{email}") { userController.findByEmail(call) }

        authenticate("auth-basic") {
            put("/{id}") { userController.update(call) }
            delete("/{id}") { userController.delete(call) }
            put("/{id}/block") {
                userController.updateBlock(call)
            }
        }
    }
}