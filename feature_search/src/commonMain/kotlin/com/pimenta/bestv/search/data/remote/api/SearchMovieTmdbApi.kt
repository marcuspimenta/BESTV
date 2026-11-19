package com.pimenta.bestv.search.data.remote.api

import com.pimenta.bestv.data.remote.model.MovieResponse
import com.pimenta.bestv.data.remote.model.PageResponse
import com.pimenta.bestv.data.remote.api.getTmdb
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
