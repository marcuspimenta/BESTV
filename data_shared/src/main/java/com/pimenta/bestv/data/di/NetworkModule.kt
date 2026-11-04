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

import com.google.gson.GsonBuilder
import com.pimenta.bestv.data.BuildConfig
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

private const val TIME_OUT_SEC = 60L
private const val HTTP_CACHE_SIZE_BYTES = 20L * 1024 * 1024
private const val HTTP_CACHE_DIRECTORY = "tmdb-http-cache"

val networkModule = module {
    single(named("tmdbApiKey")) { BuildConfig.TMDB_API_KEY }
    single(named("tmdbFilterLanguage")) { BuildConfig.TMDB_FILTER_LANGUAGE }

    single {
        OkHttpClient
            .Builder()
            .cache(
                Cache(
                    File(androidApplication().cacheDir, HTTP_CACHE_DIRECTORY),
                    HTTP_CACHE_SIZE_BYTES
                )
            )
            .apply {
                if (BuildConfig.BUILD_TYPE == "debug") {
                    addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BASIC
                        }
                    )
                }
                readTimeout(TIME_OUT_SEC, TimeUnit.SECONDS)
                writeTimeout(TIME_OUT_SEC, TimeUnit.SECONDS)
                connectTimeout(TIME_OUT_SEC, TimeUnit.SECONDS)
            }.build()
    }

    single {
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.TMDB_BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
    }
}
