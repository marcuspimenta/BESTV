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

package com.pimenta.bestv.search.data.remote.api

import com.pimenta.bestv.model.data.remote.MovieResponse
import com.pimenta.bestv.model.data.remote.PageResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by marcus on 11-02-2018.
 */
interface SearchMovieTmdbApi {

    @GET("search/movie")
    suspend fun searchMoviesByQuery(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): PageResponse<MovieResponse>
}
