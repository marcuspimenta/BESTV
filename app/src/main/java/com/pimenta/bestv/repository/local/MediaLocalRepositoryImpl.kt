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

import com.pimenta.bestv.repository.entity.Movie
import com.pimenta.bestv.repository.entity.TvShow
import com.pimenta.bestv.repository.entity.Work
import com.pimenta.bestv.repository.local.database.dao.MovieDao
import com.pimenta.bestv.repository.local.database.dao.TvShowDao
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by marcus on 20-05-2018.
 */
class MediaLocalRepositoryImpl @Inject constructor(
        private val movieDao: MovieDao,
        private val tvShowDao: TvShowDao
) : MediaLocalRepository {

    override fun isFavorite(work: Work): Boolean {
        var workSaved: Work? = null
        when (work) {
            is Movie -> workSaved = movieDao.getById(work.id)
            is TvShow -> workSaved = tvShowDao.getById(work.id)
        }
        if (workSaved != null) {
            work.id = workSaved.id
            return true
        }
        return false
    }

    override fun hasFavorite(): Single<Boolean> =
            Single.fromCallable {
                val favoritesMovies = movieDao.getAll()
                val favoritesTvShows = tvShowDao.getAll()
                favoritesMovies.isNotEmpty() || favoritesTvShows.isNotEmpty()
            }

    override fun saveFavorite(work: Work): Boolean =
            when (work) {
                is Movie -> movieDao.create(work)
                is TvShow -> tvShowDao.create(work)
                else -> false
            }

    override fun deleteFavorite(work: Work): Boolean =
            when (work) {
                is Movie -> movieDao.delete(work)
                is TvShow -> tvShowDao.delete(work)
                else -> false
            }

    override fun getMovies(): List<Movie> =
            movieDao.getAll()

    override fun getTvShows(): List<TvShow> =
            tvShowDao.getAll()
}