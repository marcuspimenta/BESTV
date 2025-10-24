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

import com.pimenta.bestv.data.local.datasource.MovieLocalDataSource
import com.pimenta.bestv.model.data.mapper.toDomainModel
import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.presentation.platform.Resource
import com.pimenta.bestv.presentation.R
import com.pimenta.bestv.workbrowse.data.remote.datasource.MovieRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by marcus on 20-10-2019.
 */
class MovieRepository @Inject constructor(
    private val resource: Resource,
    private val movieLocalDataSource: MovieLocalDataSource,
    private val movieRemoteDataSource: MovieRemoteDataSource
) {

    suspend fun getFavoriteMovies() = movieLocalDataSource.getMovies().mapNotNull { movieDbModel ->
        movieRemoteDataSource.getMovie(movieDbModel.id)?.let { work ->
            val source = resource.getStringResource(R.string.source_tmdb)
            work.toDomainModel(source).apply {
                isFavorite = true
            }
        }
    }

    suspend fun getMoviesByGenre(genreId: Int, page: Int): PageDomainModel<WorkDomainModel> {
        val source = resource.getStringResource(R.string.source_tmdb)
        return movieRemoteDataSource.getMoviesByGenre(genreId, page).toDomainModel(source)
    }

    suspend fun getNowPlayingMovies(page: Int): PageDomainModel<WorkDomainModel> {
        val source = resource.getStringResource(R.string.source_tmdb)
        return movieRemoteDataSource.getNowPlayingMovies(page).toDomainModel(source)
    }

    suspend fun getPopularMovies(page: Int): PageDomainModel<WorkDomainModel> {
        val source = resource.getStringResource(R.string.source_tmdb)
        return movieRemoteDataSource.getPopularMovies(page).toDomainModel(source)
    }

    suspend fun getTopRatedMovies(page: Int): PageDomainModel<WorkDomainModel> {
        val source = resource.getStringResource(R.string.source_tmdb)
        return movieRemoteDataSource.getTopRatedMovies(page).toDomainModel(source)
    }

    suspend fun getUpComingMovies(page: Int): PageDomainModel<WorkDomainModel> {
        val source = resource.getStringResource(R.string.source_tmdb)
        return movieRemoteDataSource.getUpComingMovies(page).toDomainModel(source)
    }
}
