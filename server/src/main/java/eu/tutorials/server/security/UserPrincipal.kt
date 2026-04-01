package eu.tutorials.server.security

import eu.tutorials.domain.model.User
import io.ktor.server.auth.*

data class UserPrincipal(val user: User) : Principal