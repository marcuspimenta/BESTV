package com.pimenta.bestv.network

import com.pimenta.bestv.network.data.remote.TmdbHttpClientConfig
import com.pimenta.bestv.network.data.remote.configureTmdbClient
import com.pimenta.bestv.workbrowse.data.remote.api.GenreTmdbApi
import com.pimenta.bestv.workbrowse.data.remote.api.MovieTmdbApi
import com.pimenta.bestv.workbrowse.data.remote.api.TvShowTmdbApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class WorkBrowseTmdbApiTest {
    @Test
    fun `browse services preserve TMDb endpoint paths`() = runTest {
        val engine = MockEngine { respond(JSON_EMPTY_OBJECT, headers = jsonHeaders) }
        val client = createClient(engine)
        val language = "en-US"
        val page = 3

        GenreTmdbApi(client).apply {
            getMovieGenres(language)
            getTvShowGenres(language)
        }
        MovieTmdbApi(client).apply {
            getMoviesByGenre(28, language, false, page)
            getNowPlayingMovies(language, page)
            getPopularMovies(language, page)
            getTopRatedMovies(language, page)
            getUpComingMovies(language, page)
        }
        TvShowTmdbApi(client).apply {
            getTvShowByGenre(10759, language, false, page)
            getAiringTodayTvShows(language, page)
            getOnTheAirTvShows(language, page)
            getPopularTvShows(language, page)
            getTopRatedTvShows(language, page)
        }

        assertEquals(
            listOf(
                "/3/genre/movie/list",
                "/3/genre/tv/list",
                "/3/discover/movie",
                "/3/movie/now_playing",
                "/3/movie/popular",
                "/3/movie/top_rated",
                "/3/movie/upcoming",
                "/3/discover/tv",
                "/3/tv/airing_today",
                "/3/tv/on_the_air",
                "/3/tv/popular",
                "/3/tv/top_rated",
            ),
            engine.requestHistory.map { it.url.encodedPath },
        )
        assertEquals("28", engine.requestHistory[2].url.parameters["with_genres"])
        assertEquals("false", engine.requestHistory[2].url.parameters["include_adult"])
        assertEquals("3", engine.requestHistory[2].url.parameters["page"])
        assertEquals("Bearer test-token", engine.requestHistory[2].headers[HttpHeaders.Authorization])
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
