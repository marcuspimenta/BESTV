package com.pimenta.bestv.search.data.remote.api

import com.pimenta.bestv.data.model.remote.PageResponse
import com.pimenta.bestv.data.model.remote.TvShowResponse
import com.pimenta.bestv.network.data.remote.getTmdb
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
