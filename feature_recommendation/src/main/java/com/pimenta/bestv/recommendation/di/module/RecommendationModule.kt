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

package com.pimenta.bestv.recommendation.di.module

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import com.pimenta.bestv.recommendation.data.local.provider.RecommendationProvider
import com.pimenta.bestv.recommendation.data.local.provider.channel.RecommendationChannelApi
import com.pimenta.bestv.recommendation.data.local.provider.row.RecommendationRowApi
import com.pimenta.bestv.recommendation.data.local.sharedpreferences.LocalSettings
import com.pimenta.bestv.route.workbrowse.WorkBrowseRoute
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by marcus on 23-04-2019.
 */
@Module
@InstallIn(SingletonComponent::class)
object RecommendationModule {
    private const val SHARED_PREFERENCES_NAME = "recommendation_preferences"

    @Provides
    @Singleton
    fun provideSharedPreferences(application: Application): SharedPreferences =
        application.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideNotificationManager(application: Application): NotificationManager =
        application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @Provides
    @Singleton
    fun provideRecommendationProvider(
        application: Application,
        localSettings: LocalSettings,
        notificationManager: NotificationManager,
        workDetailsRoute: WorkDetailsRoute,
        workBrowseRoute: WorkBrowseRoute,
    ): RecommendationProvider =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            RecommendationChannelApi(application, localSettings, workDetailsRoute, workBrowseRoute)
        } else {
            RecommendationRowApi(application, notificationManager, workDetailsRoute)
        }
}
