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

import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.data.local.datasource.MovieLocalDataSource
import com.pimenta.bestv.model.data.mapper.toDomainModel
import com.pimenta.bestv.presentation.platform.Resource
import com.pimenta.bestv.workbrowse.R
import com.pimenta.bestv.workbrowse.data.remote.datasource.MovieRemoteDataSource
import javax.inject.Inject

/**
 * Created by marcus on 20-10-2019.
 */
class MovieRepository @Inject constructor(
    private val resource: Resource,
    private val movieLocalDataSource: MovieLocalDataSource,
    private val movieRemoteDataSource: MovieRemoteDataSource
) {

    fun getFavoriteMovies() =
            movieLocalDataSource.getMovies()
                    .map {
                        val movies = mutableListOf<WorkDomainModel>()
                        it.forEach { movieDbModel ->
                            movieRemoteDataSource.getMovie(movieDbModel.id)?.let { work ->
                                val source = resource.getStringResource(R.string.source_tmdb)
                                val workDomainModel = work.toDomainModel(source).apply {
                                    isFavorite = true
                                }

                                movies.add(workDomainModel)
                            }
                        }
                        movies.toList()
                    }

    fun getMoviesByGenre(genreId: Int, page: Int) =
            movieRemoteDataSource.getMoviesByGenre(genreId, page)
                    .map {
                        val source = resource.getStringResource(R.string.source_tmdb)
                        it.toDomainModel(source)
                    }

    fun getNowPlayingMovies(page: Int) =
            movieRemoteDataSource.getNowPlayingMovies(page)
                    .map {
                        val source = resource.getStringResource(R.string.source_tmdb)
                        it.toDomainModel(source)
                    }

    fun getPopularMovies(page: Int) =
            movieRemoteDataSource.getPopularMovies(page)
                    .map {
                        val source = resource.getStringResource(R.string.source_tmdb)
                        it.toDomainModel(source)
                    }

    fun getTopRatedMovies(page: Int) =
            movieRemoteDataSource.getTopRatedMovies(page)
                    .map {
                        val source = resource.getStringResource(R.string.source_tmdb)
                        it.toDomainModel(source)
                    }

    fun getUpComingMovies(page: Int) =
            movieRemoteDataSource.getUpComingMovies(page)
                    .map {
                        val source = resource.getStringResource(R.string.source_tmdb)
                        it.toDomainModel(source)
                    }
}