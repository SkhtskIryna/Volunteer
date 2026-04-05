package eu.tutorials.volunteerapp.api

import android.util.Log
import eu.tutorials.domain.model.MaterialParticipation
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

class MaterialParticipationAPI(
    private var authHeader: String? = null,
    private val baseUrl: String = "https://volunteer-app.pp.ua"
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
        Log.d("MaterialParticipationAPI", "Auth updated: $authHeader")
    }

    // GET /materialParticipation
    suspend fun getAllMaterialParticipation(): List<MaterialParticipation>? = withContext(Dispatchers.IO) {
        try {
            val response = client.get("$baseUrl/materialParticipation")
            Log.d("MaterialParticipationAPI", "Response status: ${response.status}")
            val bodyText = response.bodyAsText()
            Log.d("MaterialParticipationAPI", "Response body: $bodyText")

            if (response.status == HttpStatusCode.OK) {
                Json.decodeFromString<List<MaterialParticipation>>(bodyText)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("MaterialParticipationAPI", "Failed to get material participations: ${e.message}")
            null
        }
    }

    // POST /materialParticipation
    suspend fun createMaterialParticipation(materialParticipation: MaterialParticipation): MaterialParticipation? = withContext(Dispatchers.IO) {
        try {
            val response = client.post("$baseUrl/materialParticipation") {
                contentType(ContentType.Application.Json)
                setBody(materialParticipation)
            }
            if (response.status.isSuccess()) response.body() else null
        } catch (_: Exception) { null }
    }

    // PUT /materialParticipation/{id}
    suspend fun updateMaterialParticipation(materialParticipation: MaterialParticipation): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = client.put("$baseUrl/materialParticipation/${materialParticipation.id}") {
                contentType(ContentType.Application.Json)
                setBody(materialParticipation)
            }
            response.status == HttpStatusCode.OK
        } catch (_: Exception) { false }
    }

    // DELETE /materialParticipation/{id}
    suspend fun deleteMaterialParticipation(id: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val result: Map<String, Boolean> =
                client.delete("$baseUrl/materialParticipation/$id").body()
            result["deleted"] ?: false
        } catch (_: Exception) { false }
    }

    // GET /materialParticipation/{id}
    suspend fun getMaterialParticipationById(id: Int): MaterialParticipation? = withContext(Dispatchers.IO) {
        try {
            val response = client.get("$baseUrl/materialParticipation/$id")
            if (response.status == HttpStatusCode.OK) response.body()
            else null
        } catch (_: Exception) { null }
    }
}