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

package com.pimenta.bestv.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pimenta.bestv.data.local.db.dao.MovieDao
import com.pimenta.bestv.data.local.db.dao.TvShowDao
import com.pimenta.bestv.data.local.entity.MovieDbModel
import com.pimenta.bestv.data.local.entity.TvShowDbModel

/**
 * Created by marcus on 05-03-2018.
 */
@Database(
        entities = [
            MovieDbModel::class,
            TvShowDbModel::class
        ],
        version = 1,
        exportSchema = false
)
abstract class MediaDb : RoomDatabase() {

    abstract fun movieDao(): MovieDao

    abstract fun tvShowDao(): TvShowDao
}