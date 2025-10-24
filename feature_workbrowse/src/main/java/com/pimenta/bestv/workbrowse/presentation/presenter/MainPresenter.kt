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

import android.app.Activity
import android.content.Intent
import com.pimenta.bestv.presentation.di.annotation.ActivityScope
import com.pimenta.bestv.presentation.dispatcher.CoroutineDispatchers
import com.pimenta.bestv.presentation.presenter.AutoCancelableCoroutineScopePresenter
import com.pimenta.bestv.route.splash.SplashRoute
import javax.inject.Inject

private const val SPLASH_ACTIVITY_REQUEST_CODE = 1

/**
 * Created by marcus on 04-05-2018.
 */
@ActivityScope
class MainPresenter @Inject constructor(
    private val view: View,
    private val splashRoute: SplashRoute,
    coroutineDispatchers: CoroutineDispatchers
    // private val loadRecommendationUseCase: LoadRecommendationUseCase
) : AutoCancelableCoroutineScopePresenter(coroutineDispatchers) {

    fun viewCreated(hasSavedInstanceState: Boolean) {
        if (hasSavedInstanceState) {
            val intent = splashRoute.buildSplashIntent()
            view.openSplashScreen(intent, SPLASH_ACTIVITY_REQUEST_CODE)
        } else {
            view.showWorkBrowseScreen()
        }
    }

    fun checkActivityResult(requestCode: Int, resultCode: Int) {
        when (requestCode) {
            SPLASH_ACTIVITY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    view.showWorkBrowseScreen()
                } else {
                    view.close()
                }
            }
        }
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

        fun openSplashScreen(intent: Intent, requestCode: Int)
        fun showWorkBrowseScreen()
        fun close()
    }
}
