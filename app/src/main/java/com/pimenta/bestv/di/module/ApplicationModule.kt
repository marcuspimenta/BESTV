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

package com.pimenta.bestv.di.module

import android.Manifest
import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.util.DisplayMetrics
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Created by marcus on 07-02-2018.
 */
@Module(includes = [
    ImplModule::class,
    MediaModule::class,
    RecommendationModule::class,
    SchedulerModule::class
])
class ApplicationModule(
        private val application: Application
) {

    @Provides
    @Singleton
    fun provideApplication() = application

    @Provides
    @Singleton
    fun provideDisplayMetrics() = DisplayMetrics()

    @Provides
    @Singleton
    fun provideGson() = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideNotificationManager(application: Application) =
            application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @Provides
    @Singleton
    fun provideAlarmManager(application: Application) =
            application.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    @Provides
    @Singleton
    fun provideOkHttpClient() =
            OkHttpClient.Builder().apply {
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                })
                readTimeout(30, TimeUnit.SECONDS)
                writeTimeout(30, TimeUnit.SECONDS)
                connectTimeout(30, TimeUnit.SECONDS)
            }.build()

    @Provides
    @Singleton
    fun providePermissions(): Map<String, Boolean> =
            object : HashMap<String, Boolean>() {
                init {
                    put(Manifest.permission.INTERNET, false)
                    put(Manifest.permission.RECORD_AUDIO, false)
                    put(Manifest.permission.RECEIVE_BOOT_COMPLETED, false)
                }
            }
}