package eu.tutorials.volunteerapp.api

import android.util.Log
import eu.tutorials.volunteerapp.data.Card
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
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class CardAPI(
    private var authHeader: String? = null,
    private val baseUrl: String = "https://volunteer-app.pp.ua" // "http://192.168.0.181:8080"
) {

    private var client = createClient()

    private fun createClient() = HttpClient(Android) {
        defaultRequest {
            authHeader?.let { header("Authorization", it) }
        }

        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        expectSuccess = false
    }

    fun updateAuthHeader(header: String) {
        authHeader = header
        client = createClient()
        Log.d("CardAPI", "authHeader updated: $authHeader")
    }

    // GET /card
    suspend fun getAllCards(): List<Card>? = withContext(Dispatchers.IO) {
        try {
            val response = client.get("$baseUrl/card")
            Log.d("CardAPI", "GET /card status: ${response.status}, body: ${response.bodyAsText()}")
            when (response.status) {
                HttpStatusCode.OK -> response.body()
                HttpStatusCode.Unauthorized -> {
                    Log.d("CardAPI", "Unauthorized: user is not logged in")
                    null
                }
                HttpStatusCode.NotFound -> {
                    Log.e("CardAPI", "Endpoint not found")
                    null
                }
                else -> {
                    Log.e("CardAPI", "Network error: ${response.status}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("CardAPI", "Network error: ${e.message}")
            null
        }
    }

    // POST /card
    suspend fun createCard(card: Card): Card? = withContext(Dispatchers.IO) {
        try {
            val response = client.post("$baseUrl/card") {
                contentType(ContentType.Application.Json)
                setBody(card)
            }
            Log.d("CardAPI", "POST /card status: ${response.status}, body: ${response.bodyAsText()}")
            if (response.status.isSuccess()) response.body() else null
        } catch (e: Exception) {
            Log.e("CardAPI", "Create card error: ${e.message}")
            null
        }
    }

    // PUT /card/{id}
    suspend fun updateCard(card: Card): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = client.put("$baseUrl/card/${card.id}") {
                contentType(ContentType.Application.Json)
                setBody(card)
            }
            Log.d("CardAPI", "PUT /card/${card.id} status: ${response.status}, body: ${response.bodyAsText()}")
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e("CardAPI", "Update card error: ${e.message}")
            false
        }
    }

    // DELETE /card/{id}
    suspend fun deleteCard(id: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val response: Map<String, Boolean> = client.delete("$baseUrl/card/$id").body()
            Log.d("CardAPI", "DELETE /card/$id response: $response")
            response["deleted"] ?: false
        } catch (e: Exception) {
            Log.e("CardAPI", "Delete card error: ${e.message}")
            false
        }
    }
}
