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

import com.pimenta.bestv.presentation.di.annotation.ActivityScope
import com.pimenta.bestv.workbrowse.presentation.presenter.MainPresenter
import com.pimenta.bestv.workbrowse.presentation.ui.activity.MainActivity
import dagger.BindsInstance
import dagger.Subcomponent

/**
 * Created by marcus on 2019-08-29.
 */
@ActivityScope
@Subcomponent
interface MainActivityComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance view: MainPresenter.View): MainActivityComponent
    }

    fun inject(activity: MainActivity)

    fun aboutFragmentComponent(): AboutFragmentComponent.Factory

    fun genreWorkGridFragmentComponent(): GenreWorkGridFragmentComponent.Factory

    fun topWorkGridFragmentComponent(): TopWorkGridFragmentComponent.Factory

    fun workBrowseFragmentComponent(): WorkBrowseFragmentComponent.Factory
}
