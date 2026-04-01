package eu.tutorials.volunteerapp.api

import android.util.Log
import eu.tutorials.domain.model.Bin
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
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

class BinAPI (
    private var authHeader: String? = null,
    private val baseUrl: String = "http://10.0.2.2:8080"
) {

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
        Log.d("BinAPI", "Auth updated: $authHeader")
    }

    // GET /bin
    suspend fun getAllBin(): List<Bin>? = withContext(Dispatchers.IO) {
        try {
            val response = client.get("$baseUrl/bin")
            Log.d("BinAPI", "Response status: ${response.status}")
            val bodyText = response.bodyAsText()
            Log.d("BinAPI", "Response body: $bodyText")

            if (response.status == HttpStatusCode.OK) {
                Json.decodeFromString<List<Bin>>(bodyText)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("BinAPI", "Failed to get bin: ${e.message}")
            null
        }
    }

    // POST /bin
    suspend fun createBin(bin: Bin): Bin? = withContext(Dispatchers.IO) {
        try {
            val response = client.post("$baseUrl/bin") {
                contentType(ContentType.Application.Json)
                setBody(bin)
            }
            if (response.status.isSuccess()) response.body() else null
        } catch (_: Exception) { null }
    }

    // DELETE /bin/{id}
    suspend fun deleteBin(id: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val result: Map<String, Boolean> =
                client.delete("$baseUrl/bin/$id").body()
            result["deleted"] ?: false
        } catch (_: Exception) { false }
    }
}