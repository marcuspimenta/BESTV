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

import com.pimenta.bestv.model.data.local.TvShowDbModel
import com.pimenta.bestv.data.local.datasource.TvShowLocalDataSource
import com.pimenta.bestv.model.data.mapper.toDomainModel
import com.pimenta.bestv.workdetail.data.remote.mapper.toDomainModel
import com.pimenta.bestv.workdetail.data.remote.datasource.TvShowRemoteDataSource
import javax.inject.Inject

/**
 * Created by marcus on 20-10-2019.
 */
class TvShowRepository @Inject constructor(
    private val tvShowLocalDataSource: TvShowLocalDataSource,
    private val tvShowRemoteDataSource: TvShowRemoteDataSource
) {

    fun saveFavoriteTvShow(tvShowDbModel: TvShowDbModel) =
            tvShowLocalDataSource.saveFavoriteTvShow(tvShowDbModel)

    fun deleteFavoriteTvShow(tvShowDbModel: TvShowDbModel) =
            tvShowLocalDataSource.deleteFavoriteTvShow(tvShowDbModel)

    fun getCastByTvShow(tvShowId: Int) =
            tvShowRemoteDataSource.getCastByTvShow(tvShowId)
                    .map {
                        it.casts?.map { cast ->
                            cast.toDomainModel()
                        }
                    }

    fun getRecommendationByTvShow(tvShowId: Int, page: Int) =
            tvShowRemoteDataSource.getRecommendationByTvShow(tvShowId, page)
                    .map { it.toDomainModel() }

    fun getSimilarByTvShow(tvShowId: Int, page: Int) =
            tvShowRemoteDataSource.getSimilarByTvShow(tvShowId, page)
                    .map { it.toDomainModel() }

    fun getVideosByTvShow(tvShowId: Int) =
            tvShowRemoteDataSource.getVideosByTvShow(tvShowId)
                    .map {
                        it.videos?.map { video ->
                            video.toDomainModel()
                        }
                    }
}