package com.pimenta.bestv.search.data.remote.api

import com.pimenta.bestv.data.remote.model.PageResponse
import com.pimenta.bestv.data.remote.model.TvShowResponse
import com.pimenta.bestv.data.remote.api.getTmdb
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter

class SearchTvShowTmdbApi(private val client: HttpClient) {
    suspend fun searchTvShowsByQuery(
        query: String,
        language: String,
        page: Int,
    ): PageResponse<TvShowResponse> = client.getTmdb("search/tv") {
        parameter("query", query)
        parameter("language", language)
        parameter("page", page)
    }
}
