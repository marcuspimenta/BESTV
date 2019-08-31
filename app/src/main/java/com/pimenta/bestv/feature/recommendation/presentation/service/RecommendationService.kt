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

package com.pimenta.bestv.feature.recommendation.presentation.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.pimenta.bestv.feature.recommendation.di.RecommendationServiceComponent
import com.pimenta.bestv.feature.recommendation.presentation.presenter.RecommendationPresenter
import javax.inject.Inject

/**
 * Created by marcus on 07-03-2018.
 */
class RecommendationService : Service(), RecommendationPresenter.Service {

    @Inject
    lateinit var presenter: RecommendationPresenter

    override fun onCreate() {
        super.onCreate()
        RecommendationServiceComponent.build(this, application)
                .inject(this)
        presenter.loadRecommendations()
    }

    override fun onDestroy() {
        presenter.dispose()
        super.onDestroy()
    }

    override fun onLoadRecommendationFinished() {
        stopSelf()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {

        fun newInstance(context: Context) = Intent(context, RecommendationService::class.java)
    }
}