package eu.tutorials.server.routes

import eu.tutorials.domain.model.FondyRequest
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.security.MessageDigest
import java.util.UUID
import kotlin.math.roundToLong

fun Route.fondyRoutes(){
    val httpClient = HttpClient(CIO) {
        install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 300_000
            connectTimeoutMillis = 300_000
            socketTimeoutMillis = 300_000
        }
    }

    val paymentStatusMap = mutableMapOf<String, String>()

    post("/create-fondy-payment") {
        val req = call.receive<FondyRequest>()

//        val merchantId = "1558171"
//        val secretKey = "HYUzpzCp2Sa73a3Mtv97zVxhR6GGk2qi"

        val merchantId = "1396424"
        val secretKey = "test"

        val amountInKopecks = (req.amount * 100).roundToLong()
        require(amountInKopecks > 0)

        val orderDesc = "Donation${amountInKopecks / 100}UAH"
        require(!orderDesc.startsWith(" "))
        require(!orderDesc.endsWith(" "))
        require(orderDesc.matches(Regex("^[A-Za-z0-9]+$")))

        val orderId = UUID.randomUUID().toString()
        val signString = "$secretKey|$amountInKopecks|UAH|$merchantId|$orderDesc|$orderId"
        val signature = MessageDigest.getInstance("SHA-1")
            .digest(signString.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }

        val requestData = buildJsonObject {
            put("request", buildJsonObject {
                put("merchant_id", JsonPrimitive(merchantId))
                put("order_id", JsonPrimitive(orderId))
                put("amount", JsonPrimitive(amountInKopecks))
                put("currency", JsonPrimitive("UAH"))
                put("order_desc", JsonPrimitive(orderDesc))
                put("signature", JsonPrimitive(signature))
            })
        }

        try {
            val httpResponse = httpClient.post("https://api.fondy.eu/api/checkout/url") {
                contentType(ContentType.Application.Json)
                setBody(requestData)
            }

            val responseText = httpResponse.bodyAsText()
            println("FONDY RAW RESPONSE: $responseText")

            val jsonElement = Json.parseToJsonElement(responseText)
            val checkoutUrl = jsonElement.jsonObject["response"]
                ?.jsonObject?.get("checkout_url")?.jsonPrimitive?.content

            if (checkoutUrl != null) {
                call.respond(
                    mapOf(
                        "checkout_url" to checkoutUrl,
                        "order_id" to orderId
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "checkout_url not found in Fondy response")
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to (e.message ?: "Unknown error"))
            )
        }
    }

    get("/fondy-status") {
        val orderId = call.request.queryParameters["order_id"]
        if (orderId == null) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "order_id is required"))
            return@get
        }

        val status = paymentStatusMap[orderId] ?: "pending"
        call.respond(
            mapOf(
                "order_id" to orderId,
                "status" to status
            )
        )
    }
}