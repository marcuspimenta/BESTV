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

import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.di.annotation.FragmentScope
import com.pimenta.bestv.workdetail.di.module.MovieRemoteDataSourceModule
import com.pimenta.bestv.workdetail.di.module.TvShowRemoteDataSourceModule
import com.pimenta.bestv.workdetail.presentation.ui.fragment.WorkDetailsFragment
import dagger.BindsInstance
import dagger.Subcomponent

/**
 * Created by marcus on 2019-08-28.
 */
@FragmentScope
@Subcomponent(
    modules = [
        MovieRemoteDataSourceModule::class,
        TvShowRemoteDataSourceModule::class,
    ]
)
interface WorkDetailsFragmentComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance workViewModel: WorkViewModel): WorkDetailsFragmentComponent
    }

    fun inject(workDetailsFragment: WorkDetailsFragment)
}
