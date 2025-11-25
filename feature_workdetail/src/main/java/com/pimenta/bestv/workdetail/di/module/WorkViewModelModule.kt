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

package com.pimenta.bestv.workdetail.di.module

import android.app.Activity
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.route.workdetail.getWorkDetail
import com.pimenta.bestv.workdetail.presentation.ui.activity.WorkDetailsActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

/**
 * Module to provide WorkViewModel from the activity intent
 */
@Module
@InstallIn(ActivityComponent::class)
object WorkViewModelModule {

    @Provides
    fun provideWorkViewModel(activity: Activity): WorkViewModel {
        require(activity is WorkDetailsActivity) { "Activity must be WorkDetailsActivity" }
        return activity.intent.getWorkDetail()
            ?: throw IllegalStateException("WorkViewModel not found in intent")
    }
}
