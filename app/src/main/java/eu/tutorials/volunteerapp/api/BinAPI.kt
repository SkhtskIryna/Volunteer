package eu.tutorials.volunteerapp.api

import android.util.Log
import eu.tutorials.domain.model.Bin
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class BinAPI(
    private val baseUrl: String = "https://volunteer-app.pp.ua"
) {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        expectSuccess = false
    }

    // GET /bin
    suspend fun getAllBin(token: String): List<Bin>? = withContext(Dispatchers.IO) {
        try {
            val response = client.get("$baseUrl/bin") {
                header("Authorization", token)
            }

            val bodyText = response.bodyAsText()
            Log.d("BIN_DEBUG", "GET STATUS = ${response.status}")
            Log.d("BIN_DEBUG", "GET BODY = $bodyText")

            if (response.status.isSuccess()) {
                Json.decodeFromString(bodyText)
            } else null

        } catch (e: Exception) {
            Log.e("BIN_DEBUG", "GET ERROR: ${e.message}")
            null
        }
    }

    // POST /bin
    suspend fun createBin(bin: Bin, token: String): Bin? = withContext(Dispatchers.IO) {
        try {
            val response = client.post("$baseUrl/bin") {
                contentType(ContentType.Application.Json)
                header("Authorization", token)
                setBody(bin)
            }

            val bodyText = response.bodyAsText()

            Log.d("BIN_DEBUG", "CREATE TOKEN = $token")
            Log.d("BIN_DEBUG", "CREATE STATUS = ${response.status}")
            Log.d("BIN_DEBUG", "CREATE BODY = $bodyText")

            return@withContext if (response.status.isSuccess()) {
                Json.decodeFromString(bodyText)
            } else null

        } catch (e: Exception) {
            Log.e("BIN_DEBUG", "CREATE ERROR: ${e.message}")
            null
        }
    }

    // DELETE /bin/{id}
    suspend fun deleteBin(id: Int, token: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = client.delete("$baseUrl/bin/$id") {
                header("Authorization", token)
            }

            Log.d("BIN_DEBUG", "DELETE STATUS = ${response.status}")

            response.status.isSuccess()
        } catch (e: Exception) {
            Log.e("BIN_DEBUG", "DELETE ERROR: ${e.message}")
            false
        }
    }
}