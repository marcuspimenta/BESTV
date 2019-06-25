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

package com.pimenta.bestv.data.repository.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pimenta.bestv.data.entity.Movie
import com.pimenta.bestv.data.entity.TvShow
import com.pimenta.bestv.data.repository.local.database.dao.MovieDao
import com.pimenta.bestv.data.repository.local.database.dao.TvShowDao

/**
 * Created by marcus on 05-03-2018.
 */
@Database(
        entities = [Movie::class, TvShow::class],
        version = 1,
        exportSchema = false
)
abstract class MediaDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao

    abstract fun tvShowDao(): TvShowDao

}