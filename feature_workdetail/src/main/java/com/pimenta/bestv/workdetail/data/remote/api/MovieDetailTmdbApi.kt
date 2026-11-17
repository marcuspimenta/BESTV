package com.pimenta.bestv.workdetail.data.remote.api

import com.pimenta.bestv.data.model.remote.CastListResponse
import com.pimenta.bestv.data.model.remote.MovieResponse
import com.pimenta.bestv.data.model.remote.PageResponse
import com.pimenta.bestv.network.data.remote.getTmdb
import com.pimenta.bestv.workdetail.data.remote.model.ReviewResponse
import com.pimenta.bestv.workdetail.data.remote.model.VideoListResponse
import com.pimenta.bestv.workdetail.data.remote.model.WatchProvidersResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter

class MovieDetailTmdbApi(private val client: HttpClient) {
    suspend fun getMovieDetails(movieId: Int, language: String): MovieResponse =
        client.getTmdb("movie/$movieId") {
            parameter("language", language)
        }

    suspend fun getCastByMovie(movieId: Int, language: String): CastListResponse =
        client.getTmdb("movie/$movieId/credits") {
            parameter("language", language)
        }

    suspend fun getRecommendationByMovie(
        movieId: Int,
        language: String,
        page: Int,
    ): PageResponse<MovieResponse> = client.getTmdb("movie/$movieId/recommendations") {
        parameter("language", language)
        parameter("page", page)
    }

    suspend fun getSimilarByMovie(movieId: Int, language: String, page: Int): PageResponse<MovieResponse> =
        client.getTmdb("movie/$movieId/similar") {
            parameter("language", language)
            parameter("page", page)
        }

    suspend fun getReviewByMovie(movieId: Int, language: String, page: Int): PageResponse<ReviewResponse> =
        client.getTmdb("movie/$movieId/reviews") {
            parameter("language", language)
            parameter("page", page)
        }

    suspend fun getVideosByMovie(movieId: Int, language: String): VideoListResponse =
        client.getTmdb("movie/$movieId/videos") {
            parameter("language", language)
        }

    suspend fun getWatchProvidersByMovie(movieId: Int): WatchProvidersResponse =
        client.getTmdb("movie/$movieId/watch/providers")
}
