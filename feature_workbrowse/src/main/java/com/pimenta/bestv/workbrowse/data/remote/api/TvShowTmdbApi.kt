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

package com.pimenta.bestv.workbrowse.data.remote.api

import com.pimenta.bestv.model.data.remote.PageResponse
import com.pimenta.bestv.model.data.remote.TvShowResponse
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by marcus on 20-10-2019.
 */
interface TvShowTmdbApi {

    @GET("tv/{tv_id}")
    fun getTvShow(
        @Path("tv_id") tv_id: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): Call<TvShowResponse>

    @GET("discover/tv")
    fun getTvShowByGenre(
        @Query("with_genres") genreId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("include_adult") includeAdult:
            Boolean,
        @Query("page") page: Int
    ): Single<PageResponse<TvShowResponse>>

    @GET("tv/airing_today")
    fun getAiringTodayTvShows(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): Single<PageResponse<TvShowResponse>>

    @GET("tv/on_the_air")
    fun getOnTheAirTvShows(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): Single<PageResponse<TvShowResponse>>

    @GET("tv/popular")
    fun getPopularTvShows(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): Single<PageResponse<TvShowResponse>>

    @GET("tv/top_rated")
    fun getTopRatedTvShows(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): Single<PageResponse<TvShowResponse>>
}
