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
import com.pimenta.bestv.model.data.local.TvShowDbModel
import com.pimenta.bestv.model.data.mapper.toDomainModel
import com.pimenta.bestv.presentation.R
import com.pimenta.bestv.presentation.platform.Resource
import com.pimenta.bestv.workdetail.data.remote.datasource.TvShowRemoteDataSource
import com.pimenta.bestv.workdetail.data.remote.mapper.toDomainModel
import com.pimenta.bestv.workdetail.data.remote.mapper.toDomainModel as watchProvidersToDomainModel

/**
 * Created by marcus on 20-10-2019.
 */
class TvShowRepository(
    private val resource: Resource,
    private val tvShowLocalDataSource: TvShowLocalDataSource,
    private val tvShowRemoteDataSource: TvShowRemoteDataSource
) {

    suspend fun saveFavoriteTvShow(tvShowDbModel: TvShowDbModel) =
        tvShowLocalDataSource.saveFavoriteTvShow(tvShowDbModel)

    suspend fun deleteFavoriteTvShow(tvShowDbModel: TvShowDbModel) =
        tvShowLocalDataSource.deleteFavoriteTvShow(tvShowDbModel)

    suspend fun isFavoriteTvShow(tvShowDbModel: TvShowDbModel): Boolean {
        val tvShow = tvShowLocalDataSource.getById(tvShowDbModel)
        return tvShow != null
    }

    suspend fun getCastByTvShow(tvShowId: Int) =
        tvShowRemoteDataSource.getCastByTvShow(tvShowId).let { response ->
            val source = resource.getStringResource(R.string.source_tmdb)
            response.casts?.map { cast ->
                cast.toDomainModel(source)
            }
        }

    suspend fun getRecommendationByTvShow(tvShowId: Int, page: Int) =
        tvShowRemoteDataSource.getRecommendationByTvShow(tvShowId, page).let { response ->
            val source = resource.getStringResource(R.string.source_tmdb)
            response.toDomainModel(source)
        }

    suspend fun getSimilarByTvShow(tvShowId: Int, page: Int) =
        tvShowRemoteDataSource.getSimilarByTvShow(tvShowId, page).let { response ->
            val source = resource.getStringResource(R.string.source_tmdb)
            response.toDomainModel(source)
        }

    suspend fun getReviewByTvShow(tvShowId: Int, page: Int) =
        tvShowRemoteDataSource.getReviewByTvShow(tvShowId, page).toDomainModel()

    suspend fun getVideosByTvShow(tvShowId: Int) =
        tvShowRemoteDataSource.getVideosByTvShow(tvShowId).let { response ->
            response.videos?.map { video ->
                video.toDomainModel()
            }
        }

    suspend fun getWatchProvidersByTvShow(tvShowId: Int, countryCode: String) =
        tvShowRemoteDataSource.getWatchProvidersByTvShow(tvShowId).watchProvidersToDomainModel(countryCode)
}
