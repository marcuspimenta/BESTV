package com.pimenta.bestv.recommendation.data.remote.api

import com.pimenta.bestv.data.model.remote.MovieResponse
import com.pimenta.bestv.data.model.remote.PageResponse
import com.pimenta.bestv.network.data.remote.getTmdb
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter

class MovieTmdbApi(private val client: HttpClient) {
    suspend fun getPopularMovies(language: String, page: Int): PageResponse<MovieResponse> =
        client.getTmdb("movie/popular") {
            parameter("language", language)
            parameter("page", page)
        }
}
