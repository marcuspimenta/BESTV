package com.pimenta.bestv.recommendation.data.remote.api

import com.pimenta.bestv.data.remote.model.MovieResponse
import com.pimenta.bestv.data.remote.model.PageResponse
import com.pimenta.bestv.data.remote.api.getTmdb
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter

class MovieTmdbApi(private val client: HttpClient) {
    suspend fun getPopularMovies(language: String, page: Int): PageResponse<MovieResponse> =
        client.getTmdb("movie/popular") {
            parameter("language", language)
            parameter("page", page)
        }
}
