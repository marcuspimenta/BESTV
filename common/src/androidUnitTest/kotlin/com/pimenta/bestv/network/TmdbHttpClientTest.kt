package com.pimenta.bestv.network

import com.pimenta.bestv.data.remote.model.MovieResponse
import com.pimenta.bestv.data.remote.api.TmdbHttpClientConfig
import com.pimenta.bestv.data.remote.api.configureTmdbClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class TmdbHttpClientTest {
    @Test
    fun `missing nullable and unknown DTO fields are handled`() {
        val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }
        val response = json.decodeFromString<MovieResponse>(
            """{"id":7,"vote_average":null,"unmodeled":"ignored"}""",
        )

        assertEquals(7, response.id)
        assertEquals(0f, response.voteAverage)
        assertNull(response.title)
    }

    @Test
    fun `non-success responses become status exceptions`() = runTest {
        val client = HttpClient(
            MockEngine { respond("{}", status = HttpStatusCode.ServiceUnavailable) },
        ) {
            configureTmdbClient(testConfig)
        }

        val exception = assertFailsWith<ServerResponseException> {
            client.get("probe")
        }

        assertEquals(503, exception.response.status.value)
        client.close()
    }

    @Test
    fun `blank token omits authorization header`() = runTest {
        lateinit var request: HttpRequestData
        val client = HttpClient(
            MockEngine {
                request = it
                respond("{}", headers = jsonHeaders)
            },
        ) {
            configureTmdbClient(testConfig.copy(accessToken = ""))
        }

        client.get("probe")

        assertNull(request.headers[HttpHeaders.Authorization])
        client.close()
    }

    private companion object {
        val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
        val testConfig = TmdbHttpClientConfig(
            baseUrl = "https://example.test/3/",
            accessToken = "test-token",
            debugLogging = false,
        )
    }
}
