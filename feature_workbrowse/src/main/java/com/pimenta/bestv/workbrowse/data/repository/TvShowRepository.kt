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

package com.pimenta.bestv.workbrowse.data.repository

import com.pimenta.bestv.data.local.datasource.TvShowLocalDataSource
import com.pimenta.bestv.model.data.mapper.toDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.presentation.platform.Resource
import com.pimenta.bestv.workbrowse.R
import com.pimenta.bestv.workbrowse.data.remote.datasource.TvShowRemoteDataSource
import javax.inject.Inject

/**
 * Created by marcus on 20-10-2019.
 */
class TvShowRepository @Inject constructor(
    private val resource: Resource,
    private val tvShowLocalDataSource: TvShowLocalDataSource,
    private val tvShowRemoteDataSource: TvShowRemoteDataSource
) {

    fun getFavoriteTvShows() =
            tvShowLocalDataSource.getTvShows()
                    .map {
                        val tvShows = mutableListOf<WorkDomainModel>()
                        it.forEach { tvShowDbModel ->
                            tvShowRemoteDataSource.getTvShow(tvShowDbModel.id)?.let { work ->
                                val source = resource.getStringResource(R.string.source_tmdb)
                                val workDomainModel = work.toDomainModel(source).apply {
                                    isFavorite = true
                                }

                                tvShows.add(workDomainModel)
                            }
                        }
                        tvShows.toList()
                    }

    fun getTvShowByGenre(genreId: Int, page: Int) =
            tvShowRemoteDataSource.getTvShowByGenre(genreId, page)
                    .map {
                        val source = resource.getStringResource(R.string.source_tmdb)
                        it.toDomainModel(source)
                    }

    fun getAiringTodayTvShows(page: Int) =
            tvShowRemoteDataSource.getAiringTodayTvShows(page)
                    .map {
                        val source = resource.getStringResource(R.string.source_tmdb)
                        it.toDomainModel(source)
                    }

    fun getOnTheAirTvShows(page: Int) =
            tvShowRemoteDataSource.getOnTheAirTvShows(page)
                    .map {
                        val source = resource.getStringResource(R.string.source_tmdb)
                        it.toDomainModel(source)
                    }

    fun getPopularTvShows(page: Int) =
            tvShowRemoteDataSource.getPopularTvShows(page)
                    .map {
                        val source = resource.getStringResource(R.string.source_tmdb)
                        it.toDomainModel(source)
                    }

    fun getTopRatedTvShows(page: Int) =
            tvShowRemoteDataSource.getTopRatedTvShows(page)
                    .map {
                        val source = resource.getStringResource(R.string.source_tmdb)
                        it.toDomainModel(source)
                    }
}
