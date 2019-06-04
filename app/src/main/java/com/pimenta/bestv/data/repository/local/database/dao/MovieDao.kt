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

package com.pimenta.bestv.data.repository.local.database.dao

import com.j256.ormlite.dao.RuntimeExceptionDao
import com.pimenta.bestv.data.entity.Movie
import com.pimenta.bestv.data.repository.local.database.DatabaseHelper
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by marcus on 15-04-2018.
 */
@Singleton
class MovieDao @Inject constructor(
        private val databaseHelper: DatabaseHelper
) : BaseDao<Movie> {

    private val movieDao: RuntimeExceptionDao<Movie, Int> = databaseHelper.getRuntimeExceptionDao(Movie::class.java)

    override fun getAll(): List<Movie> = movieDao.queryForAll()

    override fun getById(id: Int?): Movie? = movieDao.queryForId(id)

    override fun create(model: Movie): Boolean = movieDao.create(model) > 0

    override fun update(model: Movie): Boolean = movieDao.update(model) > 0

    override fun delete(model: Movie): Boolean = movieDao.delete(model) > 0
}