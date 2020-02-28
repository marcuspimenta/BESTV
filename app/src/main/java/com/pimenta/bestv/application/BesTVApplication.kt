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

package com.pimenta.bestv.application

import android.app.Application
import android.os.StrictMode
import com.pimenta.bestv.BuildConfig
import com.pimenta.bestv.castdetail.di.CastDetailsActivityComponentProvider
import com.pimenta.bestv.di.ApplicationComponent
import com.pimenta.bestv.di.DaggerApplicationComponent
import com.pimenta.bestv.di.module.ApplicationModule
import com.pimenta.bestv.recommendation.di.BootBroadcastReceiverComponentProvider
import com.pimenta.bestv.recommendation.di.RecommendationWorkerComponentProvider
import com.pimenta.bestv.search.di.SearchActivityComponentProvider
import com.pimenta.bestv.workbrowse.di.MainActivityComponentProvider
import com.pimenta.bestv.workdetail.di.WorkDetailsActivityComponentProvider
import timber.log.Timber

/**
 * Created by marcus on 07-02-2018.
 */
class BesTVApplication : Application(),
        CastDetailsActivityComponentProvider,
        SearchActivityComponentProvider,
        WorkDetailsActivityComponentProvider,
        MainActivityComponentProvider,
        BootBroadcastReceiverComponentProvider,
        RecommendationWorkerComponentProvider {

    private lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        when (BuildConfig.BUILD_TYPE) {
            "debug" -> {
                Timber.plant(Timber.DebugTree())
                StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                        .detectAll()
                        .penaltyLog()
                        .build())
                StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                        .detectLeakedSqlLiteObjects()
                        .detectLeakedClosableObjects()
                        .penaltyLog()
                        .build())
            }
        }

        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(ApplicationModule(this))
                .build()
    }

    override fun castDetailsActivityComponent() =
            applicationComponent.castDetailsActivityComponent().create()

    override fun searchActivityComponent() =
            applicationComponent.searchActivityComponent().create()

    override fun workDetailsActivityComponent() =
            applicationComponent.workDetailsActivityComponent().create()

    override fun mainActivityComponent() =
            applicationComponent.mainActivityComponent().create()

    override fun bootBroadcastReceiverComponent() =
            applicationComponent.bootBroadcastReceiverComponent().create()

    override fun recommendationWorkerComponent() =
            applicationComponent.recommendationWorkerComponent().create()
}
