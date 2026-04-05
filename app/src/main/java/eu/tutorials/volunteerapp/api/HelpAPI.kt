package eu.tutorials.volunteerapp.api

import android.util.Log
import eu.tutorials.domain.model.Help
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

class HelpAPI(
    private var authHeader: String? = null,
    private val baseUrl: String = "https://volunteer-app.pp.ua") { //http://10.0.2.2:8080

    private var client = createClient()

    private fun createClient() = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        expectSuccess = false

        defaultRequest {
            authHeader?.let { header("Authorization", it) }
        }
    }

    fun updateAuthHeader(header: String) {
        authHeader = header
        client = createClient()
        Log.d("HelpAPI", "Auth updated: $authHeader")
    }

    // GET /help
    suspend fun getAllHelps(): List<Help>? = withContext(Dispatchers.IO) {
        try {
            val response = client.get("$baseUrl/help")
            if (response.status == HttpStatusCode.OK) response.body() else null
        } catch (_: Exception) { null }
    }

    // POST /help
    suspend fun createHelp(help: Help): Help? = withContext(Dispatchers.IO) {
        try {
            val response = client.post("$baseUrl/help") {
                contentType(ContentType.Application.Json)
                setBody(help)
            }
            if (response.status.isSuccess()) response.body() else null
        } catch (_: Exception) { null }
    }

    // PUT /help/{id}
    suspend fun updateHelp(help: Help): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = client.put("$baseUrl/help/${help.id}") {
                contentType(ContentType.Application.Json)
                setBody(help)
            }
            response.status == HttpStatusCode.OK
        } catch (_: Exception) { false }
    }

    // DELETE /help/{id}
    suspend fun deleteHelp(id: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val result: Map<String, Boolean> =
                client.delete("$baseUrl/help/$id").body()
            result["deleted"] ?: false
        } catch (_: Exception) { false }
    }

    // GET /help/{id}
    suspend fun getHelpById(id: Int): Help? = withContext(Dispatchers.IO) {
        try {
            val response = client.get("$baseUrl/help/$id")
            if (response.status == HttpStatusCode.OK) response.body()
            else null
        } catch (_: Exception) { null }
    }
}