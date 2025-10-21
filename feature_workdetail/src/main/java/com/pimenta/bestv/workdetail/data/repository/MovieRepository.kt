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

import com.pimenta.bestv.data.local.datasource.MovieLocalDataSource
import com.pimenta.bestv.model.data.local.MovieDbModel
import com.pimenta.bestv.model.data.mapper.toDomainModel
import com.pimenta.bestv.presentation.R
import com.pimenta.bestv.presentation.platform.Resource
import com.pimenta.bestv.workdetail.data.remote.datasource.MovieRemoteDataSource
import com.pimenta.bestv.workdetail.data.remote.mapper.toDomainModel
import javax.inject.Inject

/**
 * Created by marcus on 20-10-2019.
 */
class MovieRepository @Inject constructor(
    private val resource: Resource,
    private val movieLocalDataSource: MovieLocalDataSource,
    private val movieRemoteDataSource: MovieRemoteDataSource
) {

    suspend fun saveFavoriteMovie(movieDbModel: MovieDbModel) =
        movieLocalDataSource.saveFavoriteMovie(movieDbModel)

    suspend fun deleteFavoriteMovie(movieDbModel: MovieDbModel) =
        movieLocalDataSource.deleteFavoriteMovie(movieDbModel)

    suspend fun isFavoriteMove(movieDbModel: MovieDbModel): Boolean {
        val movie = movieLocalDataSource.getById(movieDbModel)
        return movie != null
    }

    suspend fun getCastByMovie(movieId: Int) =
        movieRemoteDataSource.getCastByMovie(movieId).let { response ->
            val source = resource.getStringResource(R.string.source_tmdb)
            response.casts?.map { cast ->
                cast.toDomainModel(source)
            }
        }

    suspend fun getRecommendationByMovie(movieId: Int, page: Int) =
        movieRemoteDataSource.getRecommendationByMovie(movieId, page).let { response ->
            val source = resource.getStringResource(R.string.source_tmdb)
            response.toDomainModel(source)
        }

    suspend fun getSimilarByMovie(movieId: Int, page: Int) =
        movieRemoteDataSource.getSimilarByMovie(movieId, page).let { response ->
            val source = resource.getStringResource(R.string.source_tmdb)
            response.toDomainModel(source)
        }

    suspend fun getReviewByMovie(tvShowId: Int, page: Int) =
        movieRemoteDataSource.getReviewByMovie(tvShowId, page).toDomainModel()

    suspend fun getVideosByMovie(movieId: Int) =
        movieRemoteDataSource.getVideosByMovie(movieId).let { response ->
            response.videos?.map { video ->
                video.toDomainModel()
            }
        }
}
