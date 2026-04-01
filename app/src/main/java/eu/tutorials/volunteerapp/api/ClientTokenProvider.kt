package eu.tutorials.volunteerapp.api

import android.util.Log
import eu.tutorials.domain.model.FondyRequest
import eu.tutorials.domain.model.FondyResponse
import eu.tutorials.volunteerapp.data.FondyStatusResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ClientTokenProvider(private val baseUrl: String = "http://10.0.2.2:8080") {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun fetchFondyCheckoutUrl(amount: Double): FondyResponse? {
        return try {
            client.post("$baseUrl/create-fondy-payment") {
                contentType(ContentType.Application.Json)
                setBody(FondyRequest(amount = amount))
            }.body()
        } catch (e: Exception) {
            Log.e("Fondy", "Failed to fetch checkout URL", e)
            null
        }
    }

    suspend fun fetchFondyPaymentStatus(orderId: String): String? {
        return try {
            val response: FondyStatusResponse = client.get("$baseUrl/fondy-status") {
                parameter("order_id", orderId)
            }.body()

            response.status
        } catch (e: Exception) {
            Log.e("Fondy", "Failed to fetch payment status", e)
            null
        }
    }
}