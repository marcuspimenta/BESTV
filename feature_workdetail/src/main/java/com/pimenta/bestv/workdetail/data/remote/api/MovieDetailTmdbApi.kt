/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.pimenta.bestv.workdetail.data.remote.api

import com.pimenta.bestv.model.data.remote.CastListResponse
import com.pimenta.bestv.model.data.remote.MovieResponse
import com.pimenta.bestv.model.data.remote.PageResponse
import com.pimenta.bestv.workdetail.data.remote.model.ReviewResponse
import com.pimenta.bestv.workdetail.data.remote.model.VideoListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by marcus on 20-10-2019.
 */
interface MovieDetailTmdbApi {

    @GET("movie/{movie_id}/credits")
    suspend fun getCastByMovie(
        @Path("movie_id") movie_id: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): CastListResponse

    @GET("movie/{movie_id}/recommendations")
    suspend fun getRecommendationByMovie(
        @Path("movie_id") movie_id: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): PageResponse<MovieResponse>

    @GET("movie/{movie_id}/similar")
    suspend fun getSimilarByMovie(
        @Path("movie_id") movie_id: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): PageResponse<MovieResponse>

    @GET("movie/{movie_id}/reviews")
    suspend fun getReviewByMovie(
        @Path("movie_id") movie_id: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): PageResponse<ReviewResponse>

    @GET("movie/{movie_id}/videos")
    suspend fun getVideosByMovie(
        @Path("movie_id") movie_id: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): VideoListResponse
}
