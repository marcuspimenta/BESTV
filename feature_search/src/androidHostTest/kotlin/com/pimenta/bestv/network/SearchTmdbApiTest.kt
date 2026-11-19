package com.pimenta.bestv.network

import com.pimenta.bestv.data.remote.api.TmdbHttpClientConfig
import com.pimenta.bestv.data.remote.api.configureTmdbClient
import com.pimenta.bestv.search.data.remote.api.SearchMovieTmdbApi
import com.pimenta.bestv.search.data.remote.api.SearchTvShowTmdbApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SearchTmdbApiTest {
    @Test
    fun `search services preserve endpoint paths`() = runTest {
        val engine = MockEngine { respond(JSON_EMPTY_OBJECT, headers = jsonHeaders) }
        val client = createClient(engine)

        SearchMovieTmdbApi(client).searchMoviesByQuery("The Matrix", "en-US", 3)
        SearchTvShowTmdbApi(client).searchTvShowsByQuery("The Office", "en-US", 3)

        assertEquals(
            listOf("/3/search/movie", "/3/search/tv"),
            engine.requestHistory.map { it.url.encodedPath },
        )
        client.close()
    }

    @Test
    fun `raw search text is encoded as a query parameter`() = runTest {
        lateinit var request: HttpRequestData
        val client = createClient(
            MockEngine {
                request = it
                respond(JSON_EMPTY_OBJECT, headers = jsonHeaders)
            },
        )

        SearchMovieTmdbApi(client).searchMoviesByQuery("A+B & café", "pt-PT", 2)

        assertEquals("/3/search/movie", request.url.encodedPath)
        assertEquals("A+B & café", request.url.parameters["query"])
        assertTrue(request.url.encodedQuery.contains("query=A%2BB"))
        assertTrue(request.url.encodedQuery.contains("%26"))
        assertTrue(request.url.encodedQuery.contains("caf%C3%A9"))
        assertEquals("pt-PT", request.url.parameters["language"])
        assertEquals("2", request.url.parameters["page"])
        assertEquals("Bearer test-token", request.headers[HttpHeaders.Authorization])
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
