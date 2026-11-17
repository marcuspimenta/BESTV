package com.pimenta.bestv.workbrowse.data.remote.api

import com.pimenta.bestv.data.model.remote.MovieResponse
import com.pimenta.bestv.data.model.remote.PageResponse
import com.pimenta.bestv.network.data.remote.getTmdb
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter

class MovieTmdbApi(private val client: HttpClient) {
    suspend fun getMoviesByGenre(
        genreId: Int,
        language: String,
        includeAdult: Boolean,
        page: Int,
    ): PageResponse<MovieResponse> = client.getTmdb("discover/movie") {
        parameter("with_genres", genreId)
        parameter("language", language)
        parameter("include_adult", includeAdult)
        parameter("page", page)
    }

    suspend fun getNowPlayingMovies(language: String, page: Int): PageResponse<MovieResponse> =
        client.getTmdb("movie/now_playing") {
            parameter("language", language)
            parameter("page", page)
        }

    suspend fun getPopularMovies(language: String, page: Int): PageResponse<MovieResponse> =
        client.getTmdb("movie/popular") {
            parameter("language", language)
            parameter("page", page)
        }

    suspend fun getTopRatedMovies(language: String, page: Int): PageResponse<MovieResponse> =
        client.getTmdb("movie/top_rated") {
            parameter("language", language)
            parameter("page", page)
        }

    suspend fun getUpComingMovies(language: String, page: Int): PageResponse<MovieResponse> =
        client.getTmdb("movie/upcoming") {
            parameter("language", language)
            parameter("page", page)
        }
}
