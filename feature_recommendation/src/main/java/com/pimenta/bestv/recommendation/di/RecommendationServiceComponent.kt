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

import android.app.Application
import com.pimenta.bestv.presentation.di.module.ApplicationModule
import com.pimenta.bestv.presentation.di.module.SchedulerModule
import com.pimenta.bestv.recommendation.di.module.MovieApiModule
import com.pimenta.bestv.recommendation.di.module.RecommendationModule
import com.pimenta.bestv.recommendation.presentation.presenter.RecommendationPresenter
import com.pimenta.bestv.recommendation.presentation.service.RecommendationService
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

/**
 * Created by marcus on 2019-08-28.
 */
@Singleton
@Component(
        modules = [
            ApplicationModule::class,
            MovieApiModule::class,
            RecommendationModule::class,
            SchedulerModule::class
        ]
)
interface RecommendationServiceComponent {

    fun inject(recommendationService: RecommendationService)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance service: RecommendationPresenter.Service,
            @BindsInstance application: Application
        ): RecommendationServiceComponent
    }

    companion object {
        fun create(service: RecommendationPresenter.Service, application: Application): RecommendationServiceComponent =
                DaggerRecommendationServiceComponent.factory()
                        .create(service, application)
    }
}
