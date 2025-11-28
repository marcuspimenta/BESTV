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

package com.pimenta.bestv.workdetail.data.remote.datasource

import com.pimenta.bestv.workdetail.data.remote.api.MovieDetailTmdbApi

/**
 * Created by marcus on 20-10-2019.
 */
class MovieRemoteDataSource(
    private val tmdbApiKey: String,
    private val tmdbFilterLanguage: String,
    private val movieDetailTmdbApi: MovieDetailTmdbApi
) {

    suspend fun getCastByMovie(movieId: Int) =
        movieDetailTmdbApi.getCastByMovie(movieId, tmdbApiKey, tmdbFilterLanguage)

    suspend fun getRecommendationByMovie(movieId: Int, page: Int) =
        movieDetailTmdbApi.getRecommendationByMovie(movieId, tmdbApiKey, tmdbFilterLanguage, page)

    suspend fun getSimilarByMovie(movieId: Int, page: Int) =
        movieDetailTmdbApi.getSimilarByMovie(movieId, tmdbApiKey, tmdbFilterLanguage, page)

    suspend fun getReviewByMovie(movieId: Int, page: Int) =
        movieDetailTmdbApi.getReviewByMovie(movieId, tmdbApiKey, tmdbFilterLanguage, page)

    suspend fun getVideosByMovie(movieId: Int) =
        movieDetailTmdbApi.getVideosByMovie(movieId, tmdbApiKey, tmdbFilterLanguage)
}
