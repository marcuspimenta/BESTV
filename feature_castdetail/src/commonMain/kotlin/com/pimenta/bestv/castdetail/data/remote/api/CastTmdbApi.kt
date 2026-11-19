package com.pimenta.bestv.castdetail.data.remote.api

import com.pimenta.bestv.data.remote.model.CastResponse
import com.pimenta.bestv.data.remote.model.CastWorkListResponse
import com.pimenta.bestv.data.remote.model.MovieResponse
import com.pimenta.bestv.data.remote.model.TvShowResponse
import com.pimenta.bestv.data.remote.api.getTmdb
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter

class CastTmdbApi(private val client: HttpClient) {
    suspend fun getCastDetails(personId: Int, language: String): CastResponse =
        client.getTmdb("person/$personId") {
            parameter("language", language)
        }

    suspend fun getMovieCredits(personId: Int, language: String): CastWorkListResponse<MovieResponse> =
        client.getTmdb("person/$personId/movie_credits") {
            parameter("language", language)
        }

    suspend fun getTvShowCredits(personId: Int, language: String): CastWorkListResponse<TvShowResponse> =
        client.getTmdb("person/$personId/tv_credits") {
            parameter("language", language)
        }
}
