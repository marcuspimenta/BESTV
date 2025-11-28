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

package com.pimenta.bestv.recommendation.di

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.WorkManager
import com.pimenta.bestv.recommendation.data.local.provider.RecommendationProvider
import com.pimenta.bestv.recommendation.data.local.provider.channel.RecommendationChannelApi
import com.pimenta.bestv.recommendation.data.local.provider.row.RecommendationRowApi
import com.pimenta.bestv.recommendation.data.local.sharedpreferences.LocalSettings
import com.pimenta.bestv.recommendation.data.remote.api.MovieTmdbApi
import com.pimenta.bestv.recommendation.data.remote.datasource.MovieRemoteDataSource
import com.pimenta.bestv.recommendation.data.repository.MovieRepository
import com.pimenta.bestv.recommendation.data.repository.RecommendationRepository
import com.pimenta.bestv.recommendation.domain.LoadRecommendationUseCase
import com.pimenta.bestv.recommendation.domain.ScheduleRecommendationUseCase
import com.pimenta.bestv.recommendation.presentation.presenter.BootPresenter
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

private const val SHARED_PREFERENCES_NAME = "recommendation_preferences"

val recommendationModule = module {
    // API
    single { get<Retrofit>().create(MovieTmdbApi::class.java) }

    // SharedPreferences
    single {
        androidApplication().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    // NotificationManager
    single {
        androidApplication().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    // WorkManager
    single { WorkManager.getInstance(androidApplication()) }

    // LocalSettings
    factoryOf(::LocalSettings)

    // DataSource
    factory {
        MovieRemoteDataSource(
            tmdbApiKey = get(named("tmdbApiKey")),
            tmdbFilterLanguage = get(named("tmdbFilterLanguage")),
            movieTmdbApi = get()
        )
    }

    // RecommendationProvider
    single<RecommendationProvider> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            RecommendationChannelApi(
                application = androidApplication(),
                localSettings = get(),
                workDetailsRoute = get(),
                workBrowseRoute = get()
            )
        } else {
            RecommendationRowApi(
                application = androidApplication(),
                notificationManager = get(),
                workDetailsRoute = get()
            )
        }
    }

    // Repositories
    factoryOf(::MovieRepository)
    factoryOf(::RecommendationRepository)

    // UseCases
    factoryOf(::LoadRecommendationUseCase)
    factoryOf(::ScheduleRecommendationUseCase)

    // Presenter
    factoryOf(::BootPresenter)
}
