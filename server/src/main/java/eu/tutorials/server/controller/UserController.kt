package eu.tutorials.server.controller

import eu.tutorials.domain.model.BlockRequest
import eu.tutorials.domain.model.User
import eu.tutorials.domain.usecase.CreateUserUseCase
import eu.tutorials.domain.usecase.DeleteUserUseCase
import eu.tutorials.domain.usecase.FindUserByEmailUseCase
import eu.tutorials.domain.usecase.FindUserByIdUseCase
import eu.tutorials.domain.usecase.GetAllUsersUseCase
import eu.tutorials.domain.usecase.GetPasswordHashUseCase
import eu.tutorials.domain.usecase.UpdateUserBlockUseCase
import eu.tutorials.domain.usecase.UpdateUserUseCase
import eu.tutorials.server.security.AuthRequest
import eu.tutorials.server.security.AuthResponse
import eu.tutorials.server.security.Base64PhotoEncoder
import eu.tutorials.server.security.UserPrincipal
import eu.tutorials.server.security.UserRequest
import eu.tutorials.server.security.UserResponse
import eu.tutorials.server.security.UserRole
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import org.slf4j.LoggerFactory

class UserController(
    private val createUserUseCase: CreateUserUseCase,
    private val authenticateUserUseCase: GetPasswordHashUseCase,
    private val findUserByIdUseCase: FindUserByIdUseCase,
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val findUserByEmailUseCase: FindUserByEmailUseCase,
    private val updateUserBlockUseCase: UpdateUserBlockUseCase
) {
    suspend fun create(call: ApplicationCall) {
        val request = call.receive<UserRequest>()

        val domainRole = when (request.role) {
            UserRole.Volunteer -> eu.tutorials.domain.model.UserRole.Volunteer
            UserRole.Admin -> eu.tutorials.domain.model.UserRole.Admin
            UserRole.Recipient -> eu.tutorials.domain.model.UserRole.Recipient
        }

        val user = User(
            firstName = request.firstName,
            lastName = request.lastName,
            email = request.email,
            phone = request.phone,
            role = domainRole,
            telegram = request.telegram,
            password = ""
        )

        val created = createUserUseCase(user, request.password)

        val response = UserResponse(
            id = created.id,
            firstName = created.firstName,
            lastName = created.lastName,
            email = created.email,
            phone = created.phone,
            role = created.role.name
        )
        call.respond(response)
    }

    suspend fun authenticate(call: ApplicationCall) {
        val authRequest = call.receive<AuthRequest>()

        val user = authenticateUserUseCase(authRequest.email, authRequest.password)

        val response = if (user != null) {
            AuthResponse(
                success = true,
                message = "Authentication successful",
                role = user.role.name
            )
        } else {
            AuthResponse(
                success = false,
                message = "Invalid email or password",
                role = null
            )
        }

        call.respond(HttpStatusCode.OK, response)
    }

    suspend fun findById(call: ApplicationCall) {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return call.respondText("Invalid user id", status = HttpStatusCode.BadRequest)

        val user = findUserByIdUseCase(id)
            ?: return call.respondText("User not found", status = HttpStatusCode.NotFound)

        call.respond(user)
    }

    suspend fun getAll(call: ApplicationCall) {
        val users = getAllUsersUseCase()
        call.respond(users)
    }

    val logger = LoggerFactory.getLogger("UserService")

    suspend fun update(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)

        val received = call.receive<User>()

        // Встановлення ID з токена
        val user = received.copy(id = principal.user.id)

        // Якщо пароль не змінювався — не передаємо новий
        val newPassword = if (!user.password.isNullOrBlank()) user.password else null
        val newPhoto = user.photoBase64?.takeIf { it.isNotBlank() }

        val photoEncoder = Base64PhotoEncoder()
        val newPhotoBytes = newPhoto?.let { photoEncoder.decode(it) }

        val updated = updateUserUseCase(user, newPassword, newPhotoBytes)

        logger.info("Updating user with ID=${user.id}, success=$updated")

        if (!updated) {
            call.respondText("User not found", status = HttpStatusCode.NotFound)
            return
        }

        call.respondText("User updated successfully", status = HttpStatusCode.OK)
    }

    suspend fun updateBlock(call: ApplicationCall) {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return call.respondText("Invalid user id", status = HttpStatusCode.BadRequest)

        val request = try {
            call.receive<BlockRequest>()
        } catch (e: Exception) {
            return call.respondText("Invalid request body", status = HttpStatusCode.BadRequest)
        }

        val updated = updateUserBlockUseCase(id, request.isBlocked)

        if (!updated) {
            call.respondText("User not found", status = HttpStatusCode.NotFound)
            return
        }

        call.respondText("User block status updated", status = HttpStatusCode.OK)
    }

    suspend fun delete(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)

        val id = call.parameters["id"]?.toIntOrNull()
            ?: return call.respondText("Invalid user id", status = HttpStatusCode.BadRequest)

        val deleted = deleteUserUseCase(id, principal.user)
        if (!deleted) {
            call.respondText("User not found", status = HttpStatusCode.NotFound)
            return
        }

        call.respondText("User deleted successfully", status = HttpStatusCode.OK)
    }

    suspend fun findByEmail(call: ApplicationCall) {
        val email = call.parameters["email"]
            ?: return call.respondText("Email not provided", status = HttpStatusCode.BadRequest)

        val user = findUserByEmailUseCase(email)
            ?: return call.respondText("User not found", status = HttpStatusCode.NotFound)

        call.respond(user)
    }
}