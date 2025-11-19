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

package com.pimenta.bestv.workbrowse.presentation.presenter

import com.pimenta.bestv.presentation.di.annotation.ActivityScope
import com.pimenta.bestv.presentation.dispatcher.CoroutineDispatchers
import com.pimenta.bestv.presentation.presenter.AutoCancelableCoroutineScopePresenter
import javax.inject.Inject

/**
 * Created by marcus on 04-05-2018.
 */
@ActivityScope
class MainPresenter @Inject constructor(
    private val view: View,
    coroutineDispatchers: CoroutineDispatchers
    // private val loadRecommendationUseCase: LoadRecommendationUseCase
) : AutoCancelableCoroutineScopePresenter(coroutineDispatchers) {

    fun viewCreated() {
        view.showWorkBrowseScreen()
    }

    fun loadRecommendations() {
        /*launch {
            try {
                loadRecommendationUseCase()
            } catch (throwable: Throwable) {
                Timber.e(throwable, "Error while loading the recommendations")
            }
        }*/
    }

    interface View {

        fun showWorkBrowseScreen()
        fun close()
    }
}
