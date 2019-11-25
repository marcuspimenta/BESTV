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

package com.pimenta.bestv.workdetail.di

import android.app.Application
import com.pimenta.bestv.presentation.di.module.ApplicationModule
import com.pimenta.bestv.data.di.module.MediaLocalModule
import com.pimenta.bestv.presentation.di.module.SchedulerModule
import com.pimenta.bestv.workdetail.di.module.MovieRemoteDataSourceModule
import com.pimenta.bestv.workdetail.di.module.TvShowRemoteDataSourceModule
import com.pimenta.bestv.workdetail.presentation.presenter.WorkDetailsPresenter
import com.pimenta.bestv.workdetail.presentation.ui.fragment.WorkDetailsFragment
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
            MediaLocalModule::class,
            MovieRemoteDataSourceModule::class,
            TvShowRemoteDataSourceModule::class,
            SchedulerModule::class
        ]
)
interface WorkDetailsFragmentComponent {

    fun inject(workDetailsFragment: WorkDetailsFragment)

    @Component.Factory
    interface Factory {
        fun create(
                @BindsInstance view: WorkDetailsPresenter.View,
                @BindsInstance application: Application
        ): WorkDetailsFragmentComponent
    }

    companion object {
        fun create(view: WorkDetailsPresenter.View, application: Application): WorkDetailsFragmentComponent =
                DaggerWorkDetailsFragmentComponent.factory()
                        .create(view, application)
    }
}