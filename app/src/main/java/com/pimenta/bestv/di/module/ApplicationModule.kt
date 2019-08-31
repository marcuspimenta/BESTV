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

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import com.pimenta.bestv.BuildConfig
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by marcus on 07-02-2018.
 */
@Module
class ApplicationModule {

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
    fun provideSharedPreferences(application: Application) =
            application.getSharedPreferences(BuildConfig.PREFERENCE_NAME, Context.MODE_PRIVATE)
}