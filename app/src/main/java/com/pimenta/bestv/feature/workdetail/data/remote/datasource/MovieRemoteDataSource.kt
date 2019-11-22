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

package com.pimenta.bestv.feature.workdetail.data.remote.datasource

import com.pimenta.bestv.BuildConfig
import com.pimenta.bestv.feature.workdetail.data.remote.api.MovieDetailTmdbApi
import javax.inject.Inject

/**
 * Created by marcus on 20-10-2019.
 */
class MovieRemoteDataSource @Inject constructor(
    private val movieDetailTmdbApi: MovieDetailTmdbApi
) {

    fun getCastByMovie(movieId: Int) =
            movieDetailTmdbApi.getCastByMovie(movieId, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE)

    fun getRecommendationByMovie(movieId: Int, page: Int) =
            movieDetailTmdbApi.getRecommendationByMovie(movieId, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    fun getSimilarByMovie(movieId: Int, page: Int) =
            movieDetailTmdbApi.getSimilarByMovie(movieId, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    fun getVideosByMovie(movieId: Int) =
            movieDetailTmdbApi.getVideosByMovie(movieId, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE)
}