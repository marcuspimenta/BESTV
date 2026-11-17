package com.pimenta.bestv.workdetail.data.remote.api

import com.pimenta.bestv.data.model.remote.CastListResponse
import com.pimenta.bestv.data.model.remote.PageResponse
import com.pimenta.bestv.data.model.remote.TvShowResponse
import com.pimenta.bestv.network.data.remote.getTmdb
import com.pimenta.bestv.workdetail.data.remote.model.ReviewResponse
import com.pimenta.bestv.workdetail.data.remote.model.VideoListResponse
import com.pimenta.bestv.workdetail.data.remote.model.WatchProvidersResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter

class TvShowDetailTmdbApi(private val client: HttpClient) {
    suspend fun getTvShowDetails(tvId: Int, language: String): TvShowResponse =
        client.getTmdb("tv/$tvId") {
            parameter("language", language)
        }

    suspend fun getCastByTvShow(tvId: Int, language: String): CastListResponse =
        client.getTmdb("tv/$tvId/credits") {
            parameter("language", language)
        }

    suspend fun getRecommendationByTvShow(
        tvId: Int,
        language: String,
        page: Int,
    ): PageResponse<TvShowResponse> = client.getTmdb("tv/$tvId/recommendations") {
        parameter("language", language)
        parameter("page", page)
    }

    suspend fun getSimilarByTvShow(tvId: Int, language: String, page: Int): PageResponse<TvShowResponse> =
        client.getTmdb("tv/$tvId/similar") {
            parameter("language", language)
            parameter("page", page)
        }

    suspend fun getReviewByTvShow(tvId: Int, language: String, page: Int): PageResponse<ReviewResponse> =
        client.getTmdb("tv/$tvId/reviews") {
            parameter("language", language)
            parameter("page", page)
        }

    suspend fun getVideosByTvShow(tvId: Int, language: String): VideoListResponse =
        client.getTmdb("tv/$tvId/videos") {
            parameter("language", language)
        }

    suspend fun getWatchProvidersByTvShow(tvId: Int): WatchProvidersResponse =
        client.getTmdb("tv/$tvId/watch/providers")
}
