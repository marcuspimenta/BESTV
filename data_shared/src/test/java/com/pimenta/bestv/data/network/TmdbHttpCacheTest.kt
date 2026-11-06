package com.pimenta.bestv.data.network

import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class TmdbHttpCacheTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun `cacheable TMDb style GET response is reused`() {
        val server = MockWebServer()
        server.start()
        val cache = Cache(temporaryFolder.newFolder(), 1024L * 1024L)
        val client = OkHttpClient.Builder().cache(cache).build()
        val request = Request
            .Builder()
            .url(server.url("/3/movie/popular?api_key=test-key&language=en-US"))
            .build()
        server.enqueue(
            MockResponse
                .Builder()
                .code(200)
                .addHeader("Cache-Control", "max-age=60")
                .body("popular movies")
                .build(),
        )

        client.newCall(request).execute().use { response ->
            assertEquals("popular movies", response.body.string())
        }
        client.newCall(request).execute().use { response ->
            assertEquals("popular movies", response.body.string())
            assertNotNull(response.cacheResponse)
            assertNull(response.networkResponse)
        }

        assertEquals(1, server.requestCount)
        client.cache?.close()
        client.dispatcher.executorService.shutdown()
        client.connectionPool.evictAll()
        server.close()
    }

    @Test
    fun `stale cached response is conditionally validated`() {
        val server = MockWebServer()
        server.start()
        val cache = Cache(temporaryFolder.newFolder(), 1024L * 1024L)
        val client = OkHttpClient.Builder().cache(cache).build()
        val request = Request
            .Builder()
            .url(server.url("/3/genre/movie/list?api_key=test-key&language=en-US"))
            .build()
        server.enqueue(
            MockResponse
                .Builder()
                .code(200)
                .addHeader("Cache-Control", "max-age=0")
                .addHeader("ETag", "genres-v1")
                .body("genres")
                .build(),
        )
        server.enqueue(
            MockResponse
                .Builder()
                .code(304)
                .addHeader("Cache-Control", "max-age=60")
                .build(),
        )

        client.newCall(request).execute().use { response -> response.body.string() }
        client.newCall(request).execute().use { response ->
            assertEquals("genres", response.body.string())
            assertNotNull(response.cacheResponse)
            assertNotNull(response.networkResponse)
        }

        assertEquals(2, server.requestCount)
        client.cache?.close()
        client.dispatcher.executorService.shutdown()
        client.connectionPool.evictAll()
        server.close()
    }
}
