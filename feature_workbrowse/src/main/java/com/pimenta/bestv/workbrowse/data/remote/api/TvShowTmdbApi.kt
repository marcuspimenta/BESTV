package com.pimenta.bestv.workbrowse.data.remote.api

import com.pimenta.bestv.data.model.remote.PageResponse
import com.pimenta.bestv.data.model.remote.TvShowResponse
import com.pimenta.bestv.network.data.remote.getTmdb
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter

class TvShowTmdbApi(private val client: HttpClient) {
    suspend fun getTvShowByGenre(
        genreId: Int,
        language: String,
        includeAdult: Boolean,
        page: Int,
    ): PageResponse<TvShowResponse> = client.getTmdb("discover/tv") {
        parameter("with_genres", genreId)
        parameter("language", language)
        parameter("include_adult", includeAdult)
        parameter("page", page)
    }

    suspend fun getAiringTodayTvShows(language: String, page: Int): PageResponse<TvShowResponse> =
        client.getTmdb("tv/airing_today") {
            parameter("language", language)
            parameter("page", page)
        }

    suspend fun getOnTheAirTvShows(language: String, page: Int): PageResponse<TvShowResponse> =
        client.getTmdb("tv/on_the_air") {
            parameter("language", language)
            parameter("page", page)
        }

    suspend fun getPopularTvShows(language: String, page: Int): PageResponse<TvShowResponse> =
        client.getTmdb("tv/popular") {
            parameter("language", language)
            parameter("page", page)
        }

    suspend fun getTopRatedTvShows(language: String, page: Int): PageResponse<TvShowResponse> =
        client.getTmdb("tv/top_rated") {
            parameter("language", language)
            parameter("page", page)
        }
}
