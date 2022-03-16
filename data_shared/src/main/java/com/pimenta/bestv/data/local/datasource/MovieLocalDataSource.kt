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

package com.pimenta.bestv.data.local.datasource

import com.pimenta.bestv.data.local.dao.MovieDao
import com.pimenta.bestv.model.data.local.MovieDbModel
import javax.inject.Inject

/**
 * Created by marcus on 21-11-2019.
 */
class MovieLocalDataSource @Inject constructor(
    private val movieDao: MovieDao
) {

    fun saveFavoriteMovie(movieDbModel: MovieDbModel) =
        movieDao.create(movieDbModel)

    fun deleteFavoriteMovie(movieDbModel: MovieDbModel) =
        movieDao.delete(movieDbModel)

    fun getMovies() =
        movieDao.getAll()

    fun getById(movieDbModel: MovieDbModel) =
        movieDao.getById(movieDbModel.id)
}
