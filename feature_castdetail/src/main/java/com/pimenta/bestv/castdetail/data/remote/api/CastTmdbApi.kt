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

package com.pimenta.bestv.castdetail.data.remote.api

import com.pimenta.bestv.model.data.remote.CastResponse
import com.pimenta.bestv.model.data.remote.CastWorkListResponse
import com.pimenta.bestv.model.data.remote.MovieResponse
import com.pimenta.bestv.model.data.remote.TvShowResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by marcus on 04-04-2018.
 */
interface CastTmdbApi {

    @GET("person/{person_id}")
    suspend fun getCastDetails(
        @Path("person_id") personId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): CastResponse

    @GET("person/{person_id}/movie_credits")
    suspend fun getMovieCredits(
        @Path("person_id") personId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): CastWorkListResponse<MovieResponse>

    @GET("person/{person_id}/tv_credits")
    suspend fun getTvShowCredits(
        @Path("person_id") personId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): CastWorkListResponse<TvShowResponse>
}
