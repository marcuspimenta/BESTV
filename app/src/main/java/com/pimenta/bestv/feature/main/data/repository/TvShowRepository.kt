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

package com.pimenta.bestv.feature.main.data.repository

import com.pimenta.bestv.common.data.mapper.toDomainModel
import com.pimenta.bestv.feature.main.data.remote.datasource.TvShowRemoteDataSource
import javax.inject.Inject

/**
 * Created by marcus on 20-10-2019.
 */
class TvShowRepository @Inject constructor(
    private val tvShowRemoteDataSource: TvShowRemoteDataSource
) {

    fun getTvShowByGenre(genreId: Int, page: Int) =
            tvShowRemoteDataSource.getTvShowByGenre(genreId, page)
                    .map { it.toDomainModel() }

    fun getAiringTodayTvShows(page: Int) =
            tvShowRemoteDataSource.getAiringTodayTvShows(page)
                    .map { it.toDomainModel() }

    fun getOnTheAirTvShows(page: Int) =
            tvShowRemoteDataSource.getOnTheAirTvShows(page)
                    .map { it.toDomainModel() }

    fun getPopularTvShows(page: Int) =
            tvShowRemoteDataSource.getPopularTvShows(page)
                    .map { it.toDomainModel() }

    fun getTopRatedTvShows(page: Int) =
            tvShowRemoteDataSource.getTopRatedTvShows(page)
                    .map { it.toDomainModel() }
}