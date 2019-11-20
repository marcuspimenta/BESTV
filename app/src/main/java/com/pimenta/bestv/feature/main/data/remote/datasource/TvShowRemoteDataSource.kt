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

package com.pimenta.bestv.feature.main.data.remote.datasource

import com.pimenta.bestv.BuildConfig
import com.pimenta.bestv.feature.main.data.remote.api.TvShowTmdbApi
import javax.inject.Inject

/**
 * Created by marcus on 20-10-2019.
 */
class TvShowRemoteDataSource @Inject constructor(
    private val tvShowTmdbApi: TvShowTmdbApi
) {

    fun getTvShowByGenre(genreId: Int, page: Int) =
            tvShowTmdbApi.getTvShowByGenre(genreId, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, false, page)

    fun getAiringTodayTvShows(page: Int) =
            tvShowTmdbApi.getAiringTodayTvShows(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    fun getOnTheAirTvShows(page: Int) =
            tvShowTmdbApi.getOnTheAirTvShows(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    fun getPopularTvShows(page: Int) =
            tvShowTmdbApi.getPopularTvShows(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)

    fun getTopRatedTvShows(page: Int) =
            tvShowTmdbApi.getTopRatedTvShows(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page)
}