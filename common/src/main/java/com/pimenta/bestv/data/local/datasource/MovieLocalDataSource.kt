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
import com.pimenta.bestv.data.local.model.MovieDbModel

/**
 * Created by marcus on 21-11-2019.
 */
class MovieLocalDataSource(
    private val movieDao: MovieDao
) {

    suspend fun saveFavoriteMovie(movieId: Int) =
        movieDao.create(movieId)

    suspend fun deleteFavoriteMovie(movieId: Int) =
        movieDao.delete(movieId)

    suspend fun getById(movieId: Int) =
        movieDao.getById(movieId)

    suspend fun saveCachedWork(movieDbModel: MovieDbModel) =
        movieDao.saveCachedWork(movieDbModel)
}
