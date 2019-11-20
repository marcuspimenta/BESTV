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

package com.pimenta.bestv.data.remote

import com.pimenta.bestv.BuildConfig
import com.pimenta.bestv.common.data.model.remote.MovieResponse
import com.pimenta.bestv.common.data.model.remote.TvShowResponse
import com.pimenta.bestv.data.remote.api.MovieApi
import com.pimenta.bestv.data.remote.api.TvShowApi
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

/**
 * Created by marcus on 08-02-2018.
 */
class MediaRemoteRepositoryImpl @Inject constructor(
    private val movieApi: MovieApi,
    private val tvShowApi: TvShowApi
) : MediaRemoteRepository {

    override fun getMovie(movieId: Int): MovieResponse? =
            try {
                movieApi.getMovie(movieId, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE).execute().body()
            } catch (e: IOException) {
                Timber.e(e, "Error while getting a movie")
                null
            }

    override fun getTvShow(tvId: Int): TvShowResponse? =
            try {
                tvShowApi.getTvShow(tvId, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE).execute().body()
            } catch (e: IOException) {
                Timber.e(e, "Error while getting a tv show")
                null
            }
}