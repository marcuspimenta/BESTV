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

import android.app.Application
import android.content.Context
import android.util.DisplayMetrics
import com.pimenta.bestv.data.di.module.MediaLocalModule
import com.pimenta.bestv.data.di.module.MediaRemoteModule
import com.pimenta.bestv.presentation.BuildConfig
import com.pimenta.bestv.presentation.scheduler.RxScheduler
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Singleton

/**
 * Created by marcus on 24-02-2020.
 */
@Module(
        includes = [
            MediaLocalModule::class,
            MediaRemoteModule::class
        ]
)
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
    fun provideRxScheduler() = RxScheduler(
            Schedulers.io(),
            Schedulers.computation(),
            AndroidSchedulers.mainThread()
    )

    @Provides
    @Singleton
    fun provideSharedPreferences(application: Application) =
            application.getSharedPreferences(BuildConfig.PREFERENCE_NAME, Context.MODE_PRIVATE)
}
