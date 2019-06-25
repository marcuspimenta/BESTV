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

package com.pimenta.bestv.data.repository.local

import com.pimenta.bestv.data.entity.Movie
import com.pimenta.bestv.data.entity.TvShow
import com.pimenta.bestv.data.entity.Work
import com.pimenta.bestv.data.repository.local.database.dao.MovieDao
import com.pimenta.bestv.data.repository.local.database.dao.TvShowDao
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by marcus on 20-05-2018.
 */
class MediaLocalRepositoryImpl @Inject constructor(
        private val movieDao: MovieDao,
        private val tvShowDao: TvShowDao
) : MediaLocalRepository {

    override fun isFavorite(work: Work): Single<Boolean> =
            Single.fromCallable {
                val workSaved: Work? = when (work) {
                    is Movie -> movieDao.getById(work.id)
                    is TvShow -> tvShowDao.getById(work.id)
                    else -> null
                }
                workSaved != null
            }

    override fun hasFavorite(): Single<Boolean> =
            Single.fromCallable {
                val favoritesMovies = movieDao.getAll()
                val favoritesTvShows = tvShowDao.getAll()
                favoritesMovies.isNotEmpty() || favoritesTvShows.isNotEmpty()
            }

    override fun saveFavorite(work: Work): Completable =
            Completable.create {
                when (work) {
                    is Movie -> movieDao.create(work)
                    is TvShow -> tvShowDao.create(work)
                }
            }

    override fun deleteFavorite(work: Work): Completable =
            Completable.create {
                when (work) {
                    is Movie -> movieDao.delete(work)
                    is TvShow -> tvShowDao.delete(work)
                }
            }

    override fun getMovies(): Single<List<Movie>> =
            Single.fromCallable { movieDao.getAll() }

    override fun getTvShows(): Single<List<TvShow>> =
            Single.fromCallable { tvShowDao.getAll() }
}