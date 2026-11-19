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

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.pimenta.bestv.data.local.database.MediaDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

private const val DB_NAME = "bestv.db"

val databaseDriverModule = module {
    single<SqlDriver> {
        AndroidSqliteDriver(MediaDatabase.Schema, androidApplication(), DB_NAME)
    }
}
