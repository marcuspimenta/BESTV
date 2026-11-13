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
import com.pimenta.bestv.model.data.mapper.toDomainModel
import com.pimenta.bestv.workdetail.data.remote.datasource.MovieRemoteDataSource
import com.pimenta.bestv.workdetail.data.remote.mapper.toDomainModel
import com.pimenta.bestv.workdetail.data.remote.mapper.toDomainModel as watchProvidersToDomainModel
import com.pimenta.bestv.workdetail.data.remote.mapper.toWorkMetadataDomainModel
import com.pimenta.bestv.workdetail.data.remote.mapper.toDomainModel as crewToDomainModel
import com.pimenta.bestv.workdetail.domain.model.WorkCreditsDomainModel
import com.pimenta.bestv.workdetail.domain.repository.MovieRepository as MovieRepositoryContract
import com.pimenta.bestv.workdetail.data.local.mapper.toMovieDbModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Created by marcus on 20-10-2019.
 */
class MovieRepository(
    private val movieLocalDataSource: MovieLocalDataSource,
    private val movieRemoteDataSource: MovieRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : MovieRepositoryContract {

    override suspend fun getWorkMetadata(movieId: Int) = withContext(ioDispatcher) {
        movieRemoteDataSource.getMovieDetails(movieId).let { response ->
            movieLocalDataSource.saveCachedWork(response.toDomainModel().toMovieDbModel())
            response.toWorkMetadataDomainModel()
        }
    }

    override suspend fun saveFavoriteMovie(movieId: Int) = withContext(ioDispatcher) {
        movieLocalDataSource.saveFavoriteMovie(movieId)
    }

    override suspend fun deleteFavoriteMovie(movieId: Int) = withContext(ioDispatcher) {
        movieLocalDataSource.deleteFavoriteMovie(movieId)
    }

    override suspend fun isFavoriteMovie(movieId: Int): Boolean = withContext(ioDispatcher) {
        val movie = movieLocalDataSource.getById(movieId)
        movie?.isFavorite == true
    }

    override suspend fun getCreditsByMovie(movieId: Int) = withContext(ioDispatcher) {
        movieRemoteDataSource.getCastByMovie(movieId).let { response ->
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

    override suspend fun getRecommendationByMovie(
        movieId: Int,
        page: Int,
    ) = withContext(ioDispatcher) {
        movieRemoteDataSource.getRecommendationByMovie(movieId, page).toDomainModel()
    }

    override suspend fun getSimilarByMovie(
        movieId: Int,
        page: Int,
    ) = withContext(ioDispatcher) {
        movieRemoteDataSource.getSimilarByMovie(movieId, page).toDomainModel()
    }

    override suspend fun getReviewByMovie(
        movieId: Int,
        page: Int,
    ) = withContext(ioDispatcher) {
        movieRemoteDataSource.getReviewByMovie(movieId, page).toDomainModel()
    }

    override suspend fun getVideosByMovie(movieId: Int) = withContext(ioDispatcher) {
        movieRemoteDataSource.getVideosByMovie(movieId).let { response ->
            response.videos?.map { video ->
                video.toDomainModel()
            }
        }
    }

    override suspend fun getWatchProvidersByMovie(
        movieId: Int,
        countryCode: String,
    ) = withContext(ioDispatcher) {
        movieRemoteDataSource.getWatchProvidersByMovie(movieId).watchProvidersToDomainModel(countryCode)
    }
}
