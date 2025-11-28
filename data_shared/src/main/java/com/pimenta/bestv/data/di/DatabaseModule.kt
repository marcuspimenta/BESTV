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

package com.pimenta.bestv.data.di

import androidx.room.Room
import com.pimenta.bestv.data.local.database.MediaDb
import com.pimenta.bestv.data.local.datasource.MovieLocalDataSource
import com.pimenta.bestv.data.local.datasource.TvShowLocalDataSource
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

private const val DB_NAME = "bestv.db"

val databaseModule = module {
    single {
        Room.databaseBuilder(androidApplication(), MediaDb::class.java, DB_NAME)
            .build()
    }

    single { get<MediaDb>().movieDao() }

    single { get<MediaDb>().tvShowDao() }

    single { MovieLocalDataSource(get()) }

    single { TvShowLocalDataSource(get()) }
}