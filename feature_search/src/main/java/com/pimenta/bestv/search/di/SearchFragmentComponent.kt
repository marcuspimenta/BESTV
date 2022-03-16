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

package com.pimenta.bestv.search.di

import com.pimenta.bestv.presentation.di.annotation.FragmentScope
import com.pimenta.bestv.search.di.module.MovieRemoteDataSourceModule
import com.pimenta.bestv.search.di.module.TvShowRemoteDataSourceModule
import com.pimenta.bestv.search.presentation.presenter.SearchPresenter
import com.pimenta.bestv.search.presentation.ui.fragment.SearchFragment
import dagger.BindsInstance
import dagger.Subcomponent

/**
 * Created by marcus on 2019-08-28.
 */
@FragmentScope
@Subcomponent(
    modules = [
        MovieRemoteDataSourceModule::class,
        TvShowRemoteDataSourceModule::class
    ]
)
interface SearchFragmentComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance view: SearchPresenter.View): SearchFragmentComponent
    }

    fun inject(searchFragment: SearchFragment)
}
