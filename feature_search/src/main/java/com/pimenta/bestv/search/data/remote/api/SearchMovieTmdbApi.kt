package com.pimenta.bestv.search.data.remote.api

import com.pimenta.bestv.data.model.remote.MovieResponse
import com.pimenta.bestv.data.model.remote.PageResponse
import com.pimenta.bestv.network.data.remote.getTmdb
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter

class SearchMovieTmdbApi(private val client: HttpClient) {
    suspend fun searchMoviesByQuery(
        query: String,
        language: String,
        page: Int,
    ): PageResponse<MovieResponse> = client.getTmdb("search/movie") {
        parameter("query", query)
        parameter("language", language)
        parameter("page", page)
    }
}
