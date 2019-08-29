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

package com.pimenta.bestv.feature.workdetail.di

import android.app.Application
import com.pimenta.bestv.di.module.ApplicationModule
import com.pimenta.bestv.feature.workdetail.presentation.presenter.WorkDetailsPresenter
import com.pimenta.bestv.feature.workdetail.presentation.ui.fragment.WorkDetailsFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

/**
 * Created by marcus on 2019-08-28.
 */
@Singleton
@Component(
        modules = [
            ApplicationModule::class
        ]
)
interface WorkDetailsFragmentComponent {

    fun inject(workDetailsFragment: WorkDetailsFragment)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun view(view: WorkDetailsPresenter.View): Builder

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): WorkDetailsFragmentComponent
    }

    companion object {
        fun build(view: WorkDetailsPresenter.View, application: Application): WorkDetailsFragmentComponent =
                DaggerWorkDetailsFragmentComponent
                        .builder()
                        .view(view)
                        .application(application)
                        .build()
    }
}