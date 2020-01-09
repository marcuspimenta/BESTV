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

package com.pimenta.bestv.workbrowse.di

import com.pimenta.bestv.workbrowse.presentation.presenter.AboutPresenter
import com.pimenta.bestv.workbrowse.presentation.ui.fragment.AboutFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

/**
 * Created by marcus on 10-12-2019.
 */
@Singleton
@Component
interface AboutFragmentComponent {

    fun inject(aboutFragment: AboutFragment)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance view: AboutPresenter.View
        ): AboutFragmentComponent
    }

    companion object {
        fun create(view: AboutPresenter.View): AboutFragmentComponent =
                DaggerAboutFragmentComponent.factory()
                        .create(view)
    }
}
