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

package com.pimenta.bestv.recommendation.presentation.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.pimenta.bestv.recommendation.presentation.presenter.BootPresenter
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import timber.log.Timber

/**
 * Created by marcus on 06-03-2018.
 */
class BootBroadcastReceiver : BroadcastReceiver() {
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface BootBroadcastReceiverEntryPoint {
        fun bootPresenter(): BootPresenter
    }

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        Timber.d("Boot initiated")

        val appContext = context.applicationContext
        val entryPoint =
            EntryPointAccessors.fromApplication(
                appContext,
                BootBroadcastReceiverEntryPoint::class.java,
            )
        val presenter = entryPoint.bootPresenter()

        presenter.scheduleRecommendationUpdate()
    }
}
