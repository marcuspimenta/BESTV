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

import com.pimenta.bestv.BuildConfig
import com.pimenta.bestv.data.remote.api.createAndroidTmdbHttpClient
import com.pimenta.bestv.data.remote.api.TmdbHttpClientConfig
import org.koin.android.ext.koin.androidApplication
import java.io.File

private const val HTTP_CACHE_DIRECTORY = "tmdb-http-cache"
private const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"

val networkModule =
    tmdbNetworkModule {
        createAndroidTmdbHttpClient(
            config = TmdbHttpClientConfig(
                baseUrl = TMDB_BASE_URL,
                accessToken = BuildConfig.TMDB_ACCESS_TOKEN,
                debugLogging = BuildConfig.BUILD_TYPE == "debug",
            ),
            cacheDirectory = File(androidApplication().cacheDir, HTTP_CACHE_DIRECTORY),
        )
    }
