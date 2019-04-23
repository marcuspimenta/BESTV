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

package com.pimenta.bestv.di

import android.app.Application
import com.pimenta.bestv.di.module.ApplicationModule
import com.pimenta.bestv.feature.MainActivity
import com.pimenta.bestv.feature.boot.broadcast.BootBroadcastReceiver
import com.pimenta.bestv.feature.recommendation.service.RecommendationService
import dagger.Component
import javax.inject.Singleton

/**
 * Created by marcus on 07-02-2018.
 */
@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    val application: Application

    fun inject(receiver: BootBroadcastReceiver)

    fun inject(activity: MainActivity)

    fun getCastDetailsFragmentComponent(): CastDetailsFragmentComponent.Builder

    fun getSearchFragmentComponent(): SearchFragmentComponent.Builder

    fun getSplashFragmentComponent(): SplashFragmentComponent.Builder

    fun getWorkDetailsFragmentComponent(): WorkDetailsFragmentComponent.Builder

    fun getWorkBrowseFragmentComponent(): WorkBrowseFragmentComponent.Builder

    fun getTopWorkGridFragmentComponent(): TopWorkGridFragmentComponent.Builder

    fun getGenreWorkGridFragmentComponent(): GenreWorkGridFragmentComponent.Builder

    fun getRecommendationServiceComponent(): RecommendationServiceComponent.Builder

}