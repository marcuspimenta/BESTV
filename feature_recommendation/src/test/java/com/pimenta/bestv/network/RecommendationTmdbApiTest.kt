package com.pimenta.bestv.network

import com.pimenta.bestv.data.remote.api.TmdbHttpClientConfig
import com.pimenta.bestv.data.remote.api.configureTmdbClient
import com.pimenta.bestv.recommendation.data.remote.api.MovieTmdbApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RecommendationTmdbApiTest {
    @Test
    fun `recommendation service preserves TMDb endpoint path`() = runTest {
        val engine = MockEngine { respond(JSON_EMPTY_OBJECT, headers = jsonHeaders) }
        val client = createClient(engine)

        MovieTmdbApi(client).getPopularMovies("en-US", 3)

        assertEquals(listOf("/3/movie/popular"), engine.requestHistory.map { it.url.encodedPath })
        client.close()
    }

    private fun createClient(engine: MockEngine) = HttpClient(engine) {
        configureTmdbClient(
            TmdbHttpClientConfig(
                baseUrl = "https://example.test/3/",
                accessToken = "test-token",
                debugLogging = false,
            ),
        )
    }

    private companion object {
        const val JSON_EMPTY_OBJECT = "{}"
        val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
    }
}
