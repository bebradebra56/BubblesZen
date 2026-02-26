package com.bubbzeniac.apssof.egrf.data.repo

import android.util.Log
import com.bubbzeniac.apssof.egrf.domain.model.BubblesZenEntity
import com.bubbzeniac.apssof.egrf.domain.model.BubblesZenParam
import com.bubbzeniac.apssof.egrf.presentation.app.BubblesZenApplication.Companion.BUBBLES_ZEN_MAIN_TAG
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.plugin
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.serializer



private const val BUBBLES_ZEN_MAIN = "https://bubbleszen.com/config.php"

class BubblesZenRepository {


    private val bubblesZenKtorClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }
        install(HttpTimeout) {
            connectTimeoutMillis = 30000
            socketTimeoutMillis = 30000
            requestTimeoutMillis = 30000
        }

    }

    suspend fun bubblesZenGetClient(
        bubblesZenParam: BubblesZenParam,
        bubblesZenConversion: MutableMap<String, Any>?
    ): BubblesZenEntity? =
        withContext(Dispatchers.IO) {
            bubblesZenKtorClient.plugin(HttpSend).intercept { request ->
                Log.d(BUBBLES_ZEN_MAIN_TAG, "Ktor: Intercept body ${request.body}")
                execute(request)
            }
            val bubblesZenJson = Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            }
            Log.d(
                BUBBLES_ZEN_MAIN_TAG,
                "Ktor: conversation json: ${bubblesZenConversion.toString()}"
            )
            val bubblesZenBody = bubblesZenMergeToFlatJson(
                json = bubblesZenJson,
                param = bubblesZenParam,
                conversation = bubblesZenConversion
            )
            Log.d(
                BUBBLES_ZEN_MAIN_TAG,
                "Ktor: request json: $bubblesZenBody"
            )
            return@withContext try {
                val response = bubblesZenKtorClient.post(BUBBLES_ZEN_MAIN) {
                    contentType(ContentType.Application.Json) // обязательно JSON
                    accept(ContentType.Application.Json)
                    setBody(bubblesZenBody) // JsonObject
                }
                val code = response.status.value
                Log.d(BUBBLES_ZEN_MAIN_TAG, "Ktor: Request status code: $code")
                if (code == 200) {
                    val rawBody = response.bodyAsText() // читаем ответ как текст
                    val bubblesZenEntity = Json { ignoreUnknownKeys = true }
                        .decodeFromString(BubblesZenEntity.serializer(), rawBody)
                    Log.d(BUBBLES_ZEN_MAIN_TAG, "Ktor: Get request success")
                    Log.d(BUBBLES_ZEN_MAIN_TAG, "Ktor: $bubblesZenEntity")
                    bubblesZenEntity
                } else {
                    Log.d(BUBBLES_ZEN_MAIN_TAG, "Ktor: Status code invalid, return null")
                    Log.d(BUBBLES_ZEN_MAIN_TAG, "Ktor: ${response.body<String>()}")
                    null
                }

            } catch (e: Exception) {
                Log.d(BUBBLES_ZEN_MAIN_TAG, "Ktor: Get request failed")
                Log.d(BUBBLES_ZEN_MAIN_TAG, "Ktor: ${e.message}")
                null
            }
        }

    private inline fun <reified T> Json.bubblesZenEncodeToJsonObject(value: T): JsonObject =
        encodeToJsonElement(serializer(), value).jsonObject

    private inline fun <reified T> bubblesZenMergeToFlatJson(
        json: Json,
        param: T,
        conversation: Map<String, Any>?
    ): JsonObject {

        val paramJson = json.bubblesZenEncodeToJsonObject(param)

        return buildJsonObject {
            // поля из param
            paramJson.forEach { (key, value) ->
                put(key, value)
            }

            // динамические поля
            conversation?.forEach { (key, value) ->
                put(key, bubblesZenAnyToJsonElement(value))
            }
        }
    }

    private fun bubblesZenAnyToJsonElement(value: Any?): JsonElement {
        return when (value) {
            null -> JsonNull
            is String -> JsonPrimitive(value)
            is Number -> JsonPrimitive(value)
            is Boolean -> JsonPrimitive(value)
            is Map<*, *> -> buildJsonObject {
                value.forEach { (k, v) ->
                    if (k is String) {
                        put(k, bubblesZenAnyToJsonElement(v))
                    }
                }
            }
            is List<*> -> buildJsonArray {
                value.forEach {
                    add(bubblesZenAnyToJsonElement(it))
                }
            }
            else -> JsonPrimitive(value.toString())
        }
    }


}
