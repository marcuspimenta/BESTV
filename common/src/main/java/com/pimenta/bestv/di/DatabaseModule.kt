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

package com.pimenta.bestv.di

import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.pimenta.bestv.data.local.database.MediaDatabase
import com.pimenta.bestv.data.local.datasource.MovieLocalDataSource
import com.pimenta.bestv.data.local.datasource.TvShowLocalDataSource
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

private const val DB_NAME = "bestv.db"

val databaseModule = module {
    single {
        // Keep the existing filename and version-1 tables so saved favorites and cache rows remain available.
        val driver = AndroidSqliteDriver(MediaDatabase.Schema, androidApplication(), DB_NAME)
        MediaDatabase(driver)
    }

    single { MovieLocalDataSource(database = get(), ioDispatcher = get()) }

    single { TvShowLocalDataSource(database = get(), ioDispatcher = get()) }
}
