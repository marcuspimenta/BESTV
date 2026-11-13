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

import com.pimenta.bestv.model.data.mapper.toDomainModel
import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.workbrowse.data.local.datasource.MovieLocalDataSource
import com.pimenta.bestv.workbrowse.data.remote.datasource.MovieRemoteDataSource
import com.pimenta.bestv.workbrowse.domain.repository.MovieRepository as MovieRepositoryContract
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Created by marcus on 20-10-2019.
 */
class MovieRepository(
    private val movieLocalDataSource: MovieLocalDataSource,
    private val movieRemoteDataSource: MovieRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : MovieRepositoryContract {

    override fun observeFavoriteMovies() = movieLocalDataSource.observeFavoriteMovies().flowOn(ioDispatcher)

    override suspend fun getMoviesByGenre(
        genreId: Int,
        page: Int,
    ): PageDomainModel<WorkDomainModel> = withContext(ioDispatcher) {
        movieRemoteDataSource.getMoviesByGenre(genreId, page).toDomainModel()
    }

    override suspend fun getNowPlayingMovies(page: Int): PageDomainModel<WorkDomainModel> = withContext(ioDispatcher) {
        movieRemoteDataSource.getNowPlayingMovies(page).toDomainModel()
    }

    override suspend fun getPopularMovies(page: Int): PageDomainModel<WorkDomainModel> = withContext(ioDispatcher) {
        movieRemoteDataSource.getPopularMovies(page).toDomainModel()
    }

    override suspend fun getTopRatedMovies(page: Int): PageDomainModel<WorkDomainModel> = withContext(ioDispatcher) {
        movieRemoteDataSource.getTopRatedMovies(page).toDomainModel()
    }

    override suspend fun getUpComingMovies(page: Int): PageDomainModel<WorkDomainModel> = withContext(ioDispatcher) {
        movieRemoteDataSource.getUpComingMovies(page).toDomainModel()
    }
}
