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
import com.pimenta.bestv.workbrowse.data.remote.datasource.TvShowRemoteDataSource
import com.pimenta.bestv.workbrowse.domain.repository.TvShowRepository as TvShowRepositoryContract

/**
 * Created by marcus on 20-10-2019.
 */
class TvShowRepository(
    private val tvShowLocalDataSource: TvShowLocalDataSource,
    private val tvShowRemoteDataSource: TvShowRemoteDataSource,
) : TvShowRepositoryContract {
    override suspend fun getFavoriteTvShows() =
        tvShowLocalDataSource.getTvShows().mapNotNull { tvShowDbModel ->
            tvShowRemoteDataSource.getTvShow(tvShowDbModel.id)?.let { work ->
                work.toDomainModel().apply {
                    isFavorite = true
                }
            }
        }

    override suspend fun getTvShowByGenre(
        genreId: Int,
        page: Int,
    ): PageDomainModel<WorkDomainModel> {
        return tvShowRemoteDataSource.getTvShowByGenre(genreId, page).toDomainModel()
    }

    override suspend fun getAiringTodayTvShows(page: Int): PageDomainModel<WorkDomainModel> {
        return tvShowRemoteDataSource.getAiringTodayTvShows(page).toDomainModel()
    }

    override suspend fun getOnTheAirTvShows(page: Int): PageDomainModel<WorkDomainModel> {
        return tvShowRemoteDataSource.getOnTheAirTvShows(page).toDomainModel()
    }

    override suspend fun getPopularTvShows(page: Int): PageDomainModel<WorkDomainModel> {
        return tvShowRemoteDataSource.getPopularTvShows(page).toDomainModel()
    }

    override suspend fun getTopRatedTvShows(page: Int): PageDomainModel<WorkDomainModel> {
        return tvShowRemoteDataSource.getTopRatedTvShows(page).toDomainModel()
    }
}
