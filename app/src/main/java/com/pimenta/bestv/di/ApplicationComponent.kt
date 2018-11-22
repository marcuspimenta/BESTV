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

import com.pimenta.bestv.feature.boot.broadcast.BootBroadcastReceiver
import com.pimenta.bestv.di.module.ApplicationModule
import com.pimenta.bestv.feature.recommendation.service.RecommendationService
import com.pimenta.bestv.feature.MainActivity
import com.pimenta.bestv.feature.castdetail.ui.CastDetailsFragment
import com.pimenta.bestv.feature.workbrowse.ui.GenreWorkGridFragment
import com.pimenta.bestv.feature.workbrowse.ui.WorkBrowseFragment
import com.pimenta.bestv.feature.workdetail.ui.WorkDetailsFragment
import com.pimenta.bestv.feature.search.ui.SearchFragment
import com.pimenta.bestv.feature.splash.ui.SplashFragment
import com.pimenta.bestv.feature.workbrowse.ui.TopWorkGridFragment

import javax.inject.Singleton

import dagger.Component

/**
 * Created by marcus on 07-02-2018.
 */
@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    val application: Application

    fun inject(receiver: BootBroadcastReceiver)

    fun inject(recommendationService: RecommendationService)

    fun inject(activity: MainActivity)

    fun inject(fragment: SplashFragment)

    fun inject(fragment: WorkBrowseFragment)

    fun inject(fragment: GenreWorkGridFragment)

    fun inject(fragment: TopWorkGridFragment)

    fun inject(fragment: SearchFragment)

    fun inject(fragment: WorkDetailsFragment)

    fun inject(fragment: CastDetailsFragment)

}