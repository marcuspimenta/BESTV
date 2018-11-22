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

package com.pimenta.bestv

import android.app.Application
import android.os.StrictMode
import android.util.Log

import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.pimenta.bestv.di.ApplicationComponent
import com.pimenta.bestv.di.DaggerApplicationComponent
import com.pimenta.bestv.di.module.ApplicationModule

import io.fabric.sdk.android.Fabric

/**
 * Created by marcus on 07-02-2018.
 */
class BesTV : Application() {

    override fun onCreate() {
        Log.d(TAG, "[onCreate]")
        super.onCreate()

        when (BuildConfig.BUILD_TYPE) {
            "debug" -> {
                StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                        .detectAll()
                        .penaltyLog()
                        .build())
                StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                        .detectLeakedSqlLiteObjects()
                        .detectLeakedClosableObjects()
                        .penaltyLog()
                        .penaltyDeath()
                        .build())
            }
            else -> Fabric.with(this, Answers(), Crashlytics())
        }

        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(ApplicationModule(this))
                .build()
    }

    companion object {

        private val TAG = BesTV::class.java.simpleName

        lateinit var applicationComponent: ApplicationComponent
    }
}