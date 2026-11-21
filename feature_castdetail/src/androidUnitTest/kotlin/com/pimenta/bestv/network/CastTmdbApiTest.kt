package com.pimenta.bestv.network

import com.pimenta.bestv.castdetail.data.remote.api.CastTmdbApi
import com.pimenta.bestv.data.remote.api.TmdbHttpClientConfig
import com.pimenta.bestv.data.remote.api.configureTmdbClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CastTmdbApiTest {
    @Test
    fun `cast services preserve TMDb endpoint paths`() = runTest {
        val engine = MockEngine { respond(JSON_EMPTY_OBJECT, headers = jsonHeaders) }
        val client = createClient(engine)
        val personId = 11

        CastTmdbApi(client).apply {
            getCastDetails(personId, "en-US")
            getMovieCredits(personId, "en-US")
            getTvShowCredits(personId, "en-US")
        }

        assertEquals(
            listOf(
                "/3/person/$personId",
                "/3/person/$personId/movie_credits",
                "/3/person/$personId/tv_credits",
            ),
            engine.requestHistory.map { it.url.encodedPath },
        )
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
