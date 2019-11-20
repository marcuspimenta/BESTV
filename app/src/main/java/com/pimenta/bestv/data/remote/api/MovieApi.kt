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

package com.pimenta.bestv.data.remote.api

import com.pimenta.bestv.common.data.model.remote.MovieResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by marcus on 11-02-2018.
 */
interface MovieApi {

    @GET("movie/{movie_id}")
    fun getMovie(
        @Path("movie_id") movie_id: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): Call<MovieResponse>
}