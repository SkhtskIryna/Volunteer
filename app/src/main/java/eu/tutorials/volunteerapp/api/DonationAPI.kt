package eu.tutorials.volunteerapp.api

import android.util.Log
import eu.tutorials.domain.model.Donation
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

class DonationAPI(
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
        Log.d("DonationAPI", "authHeader updated: $authHeader")
    }

    // GET /donation
    suspend fun getAllDonations(): List<Donation>? = withContext(Dispatchers.IO) {
        try {
            Log.d("DonationAPI", "Current authHeader: $authHeader")

            val response = client.get("$baseUrl/donation")

            Log.d(
                "DonationAPI",
                "GET /donation status: ${response.status}, body: ${response.bodyAsText()}"
            )

            if (response.status == HttpStatusCode.OK) {
                response.body()
            } else {
                null
            }

        } catch (e: Exception) {
            Log.e("DonationAPI", "Network error: ${e.message}")
            null
        }
    }

    // POST /donation
    suspend fun createDonation(donation: Donation): Donation? = withContext(Dispatchers.IO) {
        try {
            val response = client.post("$baseUrl/donation") {
                contentType(ContentType.Application.Json)
                setBody(donation)
            }
            Log.d("DonationAPI", "POST /donation status: ${response.status}, body: ${response.bodyAsText()}")
            if (response.status.isSuccess()) response.body() else null
        } catch (e: Exception) {
            Log.e("DonationAPI", "Create donation error: ${e.message}")
            null
        }
    }

    // DELETE /donation/{id}
    suspend fun deleteDonation(id: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val response: Map<String, Boolean> = client.delete("$baseUrl/donation/$id").body()
            Log.d("DonationAPI", "DELETE /donation/$id response: $response")
            response["deleted"] ?: false
        } catch (e: Exception) {
            Log.e("DonationAPI", "Delete donation error: ${e.message}")
            false
        }
    }
}
