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

package com.pimenta.bestv.repository.local.dao

import androidx.room.*
import com.pimenta.bestv.repository.local.entity.TvShowDbModel
import io.reactivex.Completable
import io.reactivex.Single

/**
 * Created by marcus on 10/07/18.
 */
@Dao
interface TvShowDao {

    @Query("SELECT * FROM tv_show")
    fun getAll(): Single<List<TvShowDbModel>>

    @Query("SELECT COUNT(*) FROM tv_show WHERE id LIKE :id")
    fun getById(id: Int?): Single<Int>

    @Insert
    fun create(model: TvShowDbModel): Completable

    @Update
    fun update(model: TvShowDbModel): Completable

    @Delete
    fun delete(model: TvShowDbModel): Completable
}