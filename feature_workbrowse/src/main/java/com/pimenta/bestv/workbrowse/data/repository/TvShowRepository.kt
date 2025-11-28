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
import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.presentation.R
import com.pimenta.bestv.presentation.platform.Resource
import com.pimenta.bestv.workbrowse.data.remote.datasource.TvShowRemoteDataSource

/**
 * Created by marcus on 20-10-2019.
 */
class TvShowRepository(
    private val resource: Resource,
    private val tvShowLocalDataSource: TvShowLocalDataSource,
    private val tvShowRemoteDataSource: TvShowRemoteDataSource
) {

    suspend fun getFavoriteTvShows() =
        tvShowLocalDataSource.getTvShows().mapNotNull { tvShowDbModel ->
            tvShowRemoteDataSource.getTvShow(tvShowDbModel.id)?.let { work ->
                val source = resource.getStringResource(R.string.source_tmdb)
                work.toDomainModel(source).apply {
                    isFavorite = true
                }
            }
        }

    suspend fun getTvShowByGenre(genreId: Int, page: Int): PageDomainModel<WorkDomainModel> {
        val source = resource.getStringResource(R.string.source_tmdb)
        return tvShowRemoteDataSource.getTvShowByGenre(genreId, page).toDomainModel(source)
    }

    suspend fun getAiringTodayTvShows(page: Int): PageDomainModel<WorkDomainModel> {
        val source = resource.getStringResource(R.string.source_tmdb)
        return tvShowRemoteDataSource.getAiringTodayTvShows(page).toDomainModel(source)
    }

    suspend fun getOnTheAirTvShows(page: Int): PageDomainModel<WorkDomainModel> {
        val source = resource.getStringResource(R.string.source_tmdb)
        return tvShowRemoteDataSource.getOnTheAirTvShows(page).toDomainModel(source)
    }

    suspend fun getPopularTvShows(page: Int): PageDomainModel<WorkDomainModel> {
        val source = resource.getStringResource(R.string.source_tmdb)
        return tvShowRemoteDataSource.getPopularTvShows(page).toDomainModel(source)
    }

    suspend fun getTopRatedTvShows(page: Int): PageDomainModel<WorkDomainModel> {
        val source = resource.getStringResource(R.string.source_tmdb)
        return tvShowRemoteDataSource.getTopRatedTvShows(page).toDomainModel(source)
    }
}
