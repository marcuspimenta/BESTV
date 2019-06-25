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

import androidx.room.*
import com.pimenta.bestv.data.entity.Movie

/**
 * Created by marcus on 15-04-2018.
 */
@Dao
interface MovieDao {

    @Query("SELECT * FROM movie")
    fun getAll(): List<Movie>

    @Query("SELECT * FROM movie WHERE id LIKE :id")
    fun getById(id: Int?): Movie?

    @Insert
    fun create(model: Movie)

    @Update
    fun update(model: Movie)

    @Delete
    fun delete(model: Movie)
}