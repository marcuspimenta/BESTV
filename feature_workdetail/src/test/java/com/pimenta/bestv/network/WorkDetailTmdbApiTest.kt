package com.pimenta.bestv.network

import com.pimenta.bestv.network.data.remote.TmdbHttpClientConfig
import com.pimenta.bestv.network.data.remote.configureTmdbClient
import com.pimenta.bestv.workdetail.data.remote.api.MovieDetailTmdbApi
import com.pimenta.bestv.workdetail.data.remote.api.TvShowDetailTmdbApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class WorkDetailTmdbApiTest {
    @Test
    fun `movie and TV detail services preserve TMDb endpoint paths`() = runTest {
        val engine = MockEngine { respond(JSON_EMPTY_OBJECT, headers = jsonHeaders) }
        val client = createClient(engine)
        val workId = 42
        val language = "en-US"
        val page = 3

        MovieDetailTmdbApi(client).apply {
            getMovieDetails(workId, language)
            getCastByMovie(workId, language)
            getRecommendationByMovie(workId, language, page)
            getSimilarByMovie(workId, language, page)
            getReviewByMovie(workId, language, page)
            getVideosByMovie(workId, language)
            getWatchProvidersByMovie(workId)
        }
        TvShowDetailTmdbApi(client).apply {
            getTvShowDetails(workId, language)
            getCastByTvShow(workId, language)
            getRecommendationByTvShow(workId, language, page)
            getSimilarByTvShow(workId, language, page)
            getReviewByTvShow(workId, language, page)
            getVideosByTvShow(workId, language)
            getWatchProvidersByTvShow(workId)
        }

        assertEquals(
            listOf(
                "/3/movie/$workId",
                "/3/movie/$workId/credits",
                "/3/movie/$workId/recommendations",
                "/3/movie/$workId/similar",
                "/3/movie/$workId/reviews",
                "/3/movie/$workId/videos",
                "/3/movie/$workId/watch/providers",
                "/3/tv/$workId",
                "/3/tv/$workId/credits",
                "/3/tv/$workId/recommendations",
                "/3/tv/$workId/similar",
                "/3/tv/$workId/reviews",
                "/3/tv/$workId/videos",
                "/3/tv/$workId/watch/providers",
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
