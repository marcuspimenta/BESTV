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

package com.pimenta.bestv.feature.search.di

import android.app.Application
import com.pimenta.bestv.di.module.ApplicationModule
import com.pimenta.bestv.di.module.MediaModule
import com.pimenta.bestv.di.module.SchedulerModule
import com.pimenta.bestv.feature.search.presentation.presenter.SearchPresenter
import com.pimenta.bestv.feature.search.presentation.ui.fragment.SearchFragment
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
            MediaModule::class,
            SchedulerModule::class
        ]
)
interface SearchFragmentComponent {

    fun inject(searchFragment: SearchFragment)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun view(view: SearchPresenter.View): Builder

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): SearchFragmentComponent
    }

    companion object {
        fun build(view: SearchPresenter.View, application: Application): SearchFragmentComponent =
                DaggerSearchFragmentComponent
                        .builder()
                        .view(view)
                        .application(application)
                        .build()
    }
}