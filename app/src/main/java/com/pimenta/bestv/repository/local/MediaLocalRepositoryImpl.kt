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

package com.pimenta.bestv.repository.local

import com.pimenta.bestv.repository.local.dao.MovieDao
import com.pimenta.bestv.repository.local.dao.TvShowDao
import com.pimenta.bestv.repository.local.entity.MovieDbModel
import com.pimenta.bestv.repository.local.entity.TvShowDbModel
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import javax.inject.Inject

/**
 * Created by marcus on 20-05-2018.
 */
class MediaLocalRepositoryImpl @Inject constructor(
        private val movieDao: MovieDao,
        private val tvShowDao: TvShowDao
) : MediaLocalRepository {

    override fun hasFavorite(): Single<Boolean> =
            Single.zip<List<MovieDbModel>, List<TvShowDbModel>, Pair<List<MovieDbModel>, List<TvShowDbModel>>>(
                    movieDao.getAll(),
                    tvShowDao.getAll(),
                    BiFunction { first, second -> Pair(first, second) }
            ).map {
                it.first.isNotEmpty() || it.second.isNotEmpty()
            }

    override fun isFavoriteMovie(movieId: Int): Single<Boolean> =
            movieDao.getById(movieId)
                    .map { it == 1 }

    override fun isFavoriteTvShow(tvShowId: Int): Single<Boolean> =
            tvShowDao.getById(tvShowId)
                    .map { it == 1 }

    override fun saveFavoriteMovie(movieDbModel: MovieDbModel): Completable =
            movieDao.create(movieDbModel)

    override fun saveFavoriteTvShow(tvShowDbModel: TvShowDbModel): Completable =
            tvShowDao.create(tvShowDbModel)

    override fun deleteFavoriteMovie(movieDbModel: MovieDbModel): Completable =
            movieDao.delete(movieDbModel)

    override fun deleteFavoriteTvShow(tvShowDbModel: TvShowDbModel): Completable =
            tvShowDao.delete(tvShowDbModel)

    override fun getMovies(): Single<List<MovieDbModel>> =
            movieDao.getAll()

    override fun getTvShows(): Single<List<TvShowDbModel>> =
            tvShowDao.getAll()
}