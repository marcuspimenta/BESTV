/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pimenta.bestv.ioscompose.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.pimenta.bestv.data.local.database.MediaDatabase
import com.pimenta.bestv.data.remote.api.TMDB_API_BASE_URL
import com.pimenta.bestv.data.remote.api.TmdbHttpClientConfig
import com.pimenta.bestv.data.remote.api.configureTmdbClient
import com.pimenta.bestv.di.tmdbNetworkModule
import com.pimenta.bestv.domain.model.ContentSource
import com.pimenta.bestv.presentation.platform.ContentSourceLabelProvider
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.module.Module
import org.koin.dsl.module

internal fun platformModule(accessToken: String): Module = module {
    single<ContentSourceLabelProvider> {
        ContentSourceLabelProvider { source ->
            when (source) {
                ContentSource.TMDB -> "TMDb"
            }
        }
    }
    single<SqlDriver> { NativeSqliteDriver(MediaDatabase.Schema, "bestv.db") }

    includes(
        tmdbNetworkModule {
            HttpClient(Darwin) {
                configureTmdbClient(
                    TmdbHttpClientConfig(
                        baseUrl = TMDB_API_BASE_URL,
                        accessToken = accessToken,
                        debugLogging = false,
                    ),
                )
            }
        },
    )
}
