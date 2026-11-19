package com.pimenta.bestv.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

data class TmdbHttpClientConfig(
    val baseUrl: String,
    val accessToken: String,
    val debugLogging: Boolean,
)

fun HttpClientConfig<*>.configureTmdbClient(config: TmdbHttpClientConfig) {
    expectSuccess = true

    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            },
        )
    }

    install(HttpTimeout) {
        requestTimeoutMillis = 60_000
        connectTimeoutMillis = 60_000
        socketTimeoutMillis = 60_000
    }

    if (config.debugLogging) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.HEADERS
            sanitizeHeader { header -> header == HttpHeaders.Authorization }
        }
    }

    defaultRequest {
        url(config.baseUrl)
        if (config.accessToken.isNotBlank()) {
            headers.append(HttpHeaders.Authorization, "Bearer ${config.accessToken}")
        }
    }
}

suspend inline fun <reified T> HttpClient.getTmdb(
    path: String,
    crossinline configure: HttpRequestBuilder.() -> Unit = {},
): T = get(path) { configure() }.body()
