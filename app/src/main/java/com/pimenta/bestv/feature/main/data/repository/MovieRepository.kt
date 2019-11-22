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

package com.pimenta.bestv.feature.main.data.repository

import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.data.local.datasource.MovieLocalDataSource
import com.pimenta.bestv.feature.main.data.remote.datasource.MovieRemoteDataSource
import com.pimenta.bestv.model.data.mapper.toDomainModel
import javax.inject.Inject

/**
 * Created by marcus on 20-10-2019.
 */
class MovieRepository @Inject constructor(
    private val movieLocalDataSource: MovieLocalDataSource,
    private val movieRemoteDataSource: MovieRemoteDataSource
) {

    fun getFavoriteMovies() =
            movieLocalDataSource.getMovies()
                    .map {
                        val movies = mutableListOf<WorkDomainModel>()
                        it.forEach { movieDbModel ->
                            movieRemoteDataSource.getMovie(movieDbModel.id)?.let { work ->
                                work.isFavorite = true
                                movies.add(work.toDomainModel())
                            }
                        }
                        movies.toList()
                    }

    fun getMoviesByGenre(genreId: Int, page: Int) =
            movieRemoteDataSource.getMoviesByGenre(genreId, page)
                    .map { it.toDomainModel() }

    fun getNowPlayingMovies(page: Int) =
            movieRemoteDataSource.getNowPlayingMovies(page)
                    .map { it.toDomainModel() }

    fun getPopularMovies(page: Int) =
            movieRemoteDataSource.getPopularMovies(page)
                    .map { it.toDomainModel() }

    fun getTopRatedMovies(page: Int) =
            movieRemoteDataSource.getTopRatedMovies(page)
                    .map { it.toDomainModel() }

    fun getUpComingMovies(page: Int) =
            movieRemoteDataSource.getUpComingMovies(page)
                    .map { it.toDomainModel() }
}