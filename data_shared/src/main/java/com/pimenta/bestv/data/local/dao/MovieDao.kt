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
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.pimenta.bestv.data.local.model.MovieDbModel
import kotlinx.coroutines.flow.Flow

/**
 * Created by marcus on 15-04-2018.
 */
@Dao
interface MovieDao {

    @Query("SELECT * FROM movie WHERE isFavorite = 1")
    fun observeFavorites(): Flow<List<MovieDbModel>>

    @Query("SELECT * FROM movie WHERE id = :id")
    suspend fun getById(id: Int): MovieDbModel?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(model: MovieDbModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(model: MovieDbModel)

    @Query("UPDATE movie SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Int, isFavorite: Boolean)

    @Transaction
    suspend fun create(id: Int) {
        insertIfAbsent(MovieDbModel.favoritePlaceholder(id))
        updateFavorite(id, true)
    }

    @Transaction
    suspend fun delete(id: Int) {
        updateFavorite(id, false)
    }

    @Transaction
    suspend fun saveCachedWork(model: MovieDbModel) {
        val isFavorite = getById(model.id)?.isFavorite ?: model.isFavorite
        insertOrReplace(model.copy(isFavorite = isFavorite))
    }
}
