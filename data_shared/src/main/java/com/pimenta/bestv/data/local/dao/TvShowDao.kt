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

package com.pimenta.bestv.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.pimenta.bestv.model.data.local.TvShowDbModel

/**
 * Created by marcus on 10/07/18.
 */
@Dao
interface TvShowDao {

    @Query("SELECT * FROM tv_show")
    suspend fun getAll(): List<TvShowDbModel>

    @Query("SELECT * FROM tv_show WHERE id = :id")
    suspend fun getById(id: Int): TvShowDbModel?

    @Insert
    suspend fun create(model: TvShowDbModel)

    @Update
    suspend fun update(model: TvShowDbModel)

    @Delete
    suspend fun delete(model: TvShowDbModel)
}
