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

package com.pimenta.bestv.workbrowse.data.remote.datasource

import com.pimenta.bestv.model.data.remote.TvShowResponse
import com.pimenta.bestv.workbrowse.data.remote.api.TvShowTmdbApi
import timber.log.Timber

/**
 * Created by marcus on 20-10-2019.
 */
class TvShowRemoteDataSource(
    private val tmdbApiKey: String,
    private val tmdbFilterLanguage: String,
    private val tvShowTmdbApi: TvShowTmdbApi
) {

    suspend fun getTvShow(tvId: Int): TvShowResponse? =
        try {
            tvShowTmdbApi.getTvShow(tvId, tmdbApiKey, tmdbFilterLanguage)
        } catch (e: Exception) {
            Timber.e(e, "Error while getting a tv show")
            null
        }

    suspend fun getTvShowByGenre(genreId: Int, page: Int) =
        tvShowTmdbApi.getTvShowByGenre(genreId, tmdbApiKey, tmdbFilterLanguage, false, page)

    suspend fun getAiringTodayTvShows(page: Int) =
        tvShowTmdbApi.getAiringTodayTvShows(tmdbApiKey, tmdbFilterLanguage, page)

    suspend fun getOnTheAirTvShows(page: Int) =
        tvShowTmdbApi.getOnTheAirTvShows(tmdbApiKey, tmdbFilterLanguage, page)

    suspend fun getPopularTvShows(page: Int) =
        tvShowTmdbApi.getPopularTvShows(tmdbApiKey, tmdbFilterLanguage, page)

    suspend fun getTopRatedTvShows(page: Int) =
        tvShowTmdbApi.getTopRatedTvShows(tmdbApiKey, tmdbFilterLanguage, page)
}
