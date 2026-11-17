package com.pimenta.bestv.data.network

import com.pimenta.bestv.network.createAndroidTmdbHttpClient
import com.pimenta.bestv.network.data.remote.TmdbHttpClientConfig
import com.pimenta.bestv.workbrowse.data.remote.api.MovieTmdbApi
import kotlinx.coroutines.test.runTest
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class TmdbHttpCacheTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun `cacheable TMDb response is reused through the Ktor client`() = runTest {
        val server = MockWebServer()
        server.start()
        server.enqueue(
            MockResponse
                .Builder()
                .code(200)
                .addHeader("Cache-Control", "public, max-age=60")
                .addHeader("Content-Type", "application/json")
                .body("""{"page":1,"results":[]}""")
                .build(),
        )
        val client = createClient(server)

        val first = MovieTmdbApi(client).getPopularMovies("en-US", 1)
        val second = MovieTmdbApi(client).getPopularMovies("en-US", 1)

        assertEquals(1, first.page)
        assertEquals(1, second.page)
        assertEquals(1, server.requestCount)
        assertEquals("Bearer test-token", server.takeRequest().headers["Authorization"])

        client.close()
        server.close()
    }

    @Test
    fun `stale cache entry is conditionally validated through the Ktor client`() = runTest {
        val server = MockWebServer()
        server.start()
        server.enqueue(
            MockResponse
                .Builder()
                .code(200)
                .addHeader("Cache-Control", "public, max-age=0")
                .addHeader("ETag", "genres-v1")
                .addHeader("Content-Type", "application/json")
                .body("""{"page":1,"results":[]}""")
                .build(),
        )
        server.enqueue(
            MockResponse
                .Builder()
                .code(304)
                .addHeader("Cache-Control", "public, max-age=60")
                .build(),
        )
        val client = createClient(server)

        val api = MovieTmdbApi(client)
        api.getPopularMovies("en-US", 1)
        val cached = api.getPopularMovies("en-US", 1)

        assertEquals(1, cached.page)
        assertEquals(2, server.requestCount)
        server.takeRequest()
        val validationRequest = server.takeRequest()
        assertEquals("genres-v1", validationRequest.headers["If-None-Match"])

        client.close()
        server.close()
    }

    private fun createClient(server: MockWebServer) = createAndroidTmdbHttpClient(
        config = TmdbHttpClientConfig(
            baseUrl = server.url("/3/").toString(),
            accessToken = "test-token",
            debugLogging = false,
        ),
        cacheDirectory = temporaryFolder.newFolder(),
    )
}
