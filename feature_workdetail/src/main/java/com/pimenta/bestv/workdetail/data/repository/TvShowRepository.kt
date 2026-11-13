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

package com.pimenta.bestv.workdetail.data.repository

import com.pimenta.bestv.data.local.datasource.TvShowLocalDataSource
import com.pimenta.bestv.model.data.mapper.toDomainModel
import com.pimenta.bestv.workdetail.data.remote.datasource.TvShowRemoteDataSource
import com.pimenta.bestv.workdetail.data.remote.mapper.toDomainModel
import com.pimenta.bestv.workdetail.data.remote.mapper.toDomainModel as watchProvidersToDomainModel
import com.pimenta.bestv.workdetail.data.remote.mapper.toWorkMetadataDomainModel
import com.pimenta.bestv.workdetail.data.remote.mapper.toDomainModel as crewToDomainModel
import com.pimenta.bestv.workdetail.domain.model.WorkCreditsDomainModel
import com.pimenta.bestv.workdetail.domain.repository.TvShowRepository as TvShowRepositoryContract
import com.pimenta.bestv.workdetail.data.local.mapper.toTvShowDbModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Created by marcus on 20-10-2019.
 */
class TvShowRepository(
    private val tvShowLocalDataSource: TvShowLocalDataSource,
    private val tvShowRemoteDataSource: TvShowRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : TvShowRepositoryContract {

    override suspend fun getWorkMetadata(tvShowId: Int) = withContext(ioDispatcher) {
        tvShowRemoteDataSource.getTvShowDetails(tvShowId).let { response ->
            tvShowLocalDataSource.saveCachedWork(response.toDomainModel().toTvShowDbModel())
            response.toWorkMetadataDomainModel()
        }
    }

    override suspend fun saveFavoriteTvShow(tvShowId: Int) = withContext(ioDispatcher) {
        tvShowLocalDataSource.saveFavoriteTvShow(tvShowId)
    }

    override suspend fun deleteFavoriteTvShow(tvShowId: Int) = withContext(ioDispatcher) {
        tvShowLocalDataSource.deleteFavoriteTvShow(tvShowId)
    }

    override suspend fun isFavoriteTvShow(tvShowId: Int): Boolean = withContext(ioDispatcher) {
        val tvShow = tvShowLocalDataSource.getById(tvShowId)
        tvShow?.isFavorite == true
    }

    override suspend fun getCreditsByTvShow(tvShowId: Int) = withContext(ioDispatcher) {
        tvShowRemoteDataSource.getCastByTvShow(tvShowId).let { response ->
            WorkCreditsDomainModel(
                casts = response.casts?.map { cast -> cast.toDomainModel() },
                crew = response.crew
                    .orEmpty()
                    .mapNotNull { it.crewToDomainModel() }
                    .filter { it.role in DISPLAYED_CREW_ROLES }
                    .sortedBy { DISPLAYED_CREW_ROLES.indexOf(it.role) },
            )
        }
    }

    override suspend fun getRecommendationByTvShow(
        tvShowId: Int,
        page: Int,
    ) = withContext(ioDispatcher) {
        tvShowRemoteDataSource.getRecommendationByTvShow(tvShowId, page).let { response ->
            response.toDomainModel()
        }
    }

    override suspend fun getSimilarByTvShow(
        tvShowId: Int,
        page: Int,
    ) = withContext(ioDispatcher) {
        tvShowRemoteDataSource.getSimilarByTvShow(tvShowId, page).let { response ->
            response.toDomainModel()
        }
    }

    override suspend fun getReviewByTvShow(
        tvShowId: Int,
        page: Int,
    ) = withContext(ioDispatcher) {
        tvShowRemoteDataSource.getReviewByTvShow(tvShowId, page).toDomainModel()
    }

    override suspend fun getVideosByTvShow(tvShowId: Int) = withContext(ioDispatcher) {
        tvShowRemoteDataSource.getVideosByTvShow(tvShowId).let { response ->
            response.videos?.map { video ->
                video.toDomainModel()
            }
        }
    }

    override suspend fun getWatchProvidersByTvShow(
        tvShowId: Int,
        countryCode: String,
    ) = withContext(ioDispatcher) {
        tvShowRemoteDataSource.getWatchProvidersByTvShow(tvShowId).watchProvidersToDomainModel(countryCode)
    }
}
