package eu.tutorials.volunteerapp.api

import android.util.Base64
import eu.tutorials.volunteerapp.data.AuthRequest
import eu.tutorials.volunteerapp.data.AuthResponse
import eu.tutorials.volunteerapp.data.User
import eu.tutorials.volunteerapp.data.UserResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class UserAPI(
    private var authHeader: String? = null,
    private val baseUrl: String = "https://volunteer-app.pp.ua"
) {
    private var client = createClient()

    private fun createClient() = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }

        defaultRequest {
            authHeader?.let { header("Authorization", it) }
        }
    }

    var currentPassword: String? = null

    fun getAuthHeader(): String? = authHeader

    suspend fun authenticate(authRequest: AuthRequest): AuthResponse {
        val response = client.post("$baseUrl/user/authenticate") {
            contentType(ContentType.Application.Json)
            setBody(authRequest)
        }.body<AuthResponse>()

        // Створення Basic auth
        val email = authRequest.email
        val password = authRequest.password
        authHeader = "Basic " + Base64.encodeToString("$email:$password".toByteArray(), Base64.NO_WRAP)
        currentPassword = password

        client = createClient()

        return response
    }

    suspend fun getAllUsers(): List<User> = withContext(Dispatchers.IO) {
        client.get("$baseUrl/user").body()
    }

    suspend fun getUserById(id: Int): User? = withContext(Dispatchers.IO) {
        val response = client.get("$baseUrl/user/$id")
        if (response.status == HttpStatusCode.OK) response.body() else null
    }

    suspend fun createUser(user: User): UserResponse? = withContext(Dispatchers.IO) {
        val response = client.post("$baseUrl/user") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }

        if (response.status.isSuccess()) response.body<UserResponse>() else null
    }

    suspend fun updateUser(user: User): Boolean {
        val response = client.put("$baseUrl/user/${user.id}") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }

        return response.status == HttpStatusCode.OK
    }

    suspend fun updateUserBlock(userId: Int, isBlocked: Boolean): Boolean {
        val response = client.put("$baseUrl/user/$userId/block") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("isBlocked" to isBlocked))
        }

        return response.status == HttpStatusCode.OK
    }

    suspend fun deleteUser(id: Int): Boolean = withContext(Dispatchers.IO) {
        val response: Map<String, Boolean> = client.delete("$baseUrl/user/$id").body()
        response["deleted"] ?: false
    }

    suspend fun findUserByEmail(email: String): User? = withContext(Dispatchers.IO) {
        val response = client.get("$baseUrl/user/email/$email")
        if (response.status == HttpStatusCode.OK) response.body() else null
    }
}