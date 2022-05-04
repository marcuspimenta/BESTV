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

package com.pimenta.bestv.castdetail.presentation.presenter

import android.content.Intent
import androidx.leanback.widget.Presenter
import com.pimenta.bestv.castdetail.domain.GetCastDetailsUseCase
import com.pimenta.bestv.model.presentation.mapper.toViewModel
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.di.annotation.FragmentScope
import com.pimenta.bestv.presentation.dispatcher.CoroutineDispatchers
import com.pimenta.bestv.presentation.presenter.AutoCancelableCoroutineScopePresenter
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by marcus on 05-04-2018.
 */
@FragmentScope
class CastDetailsPresenter @Inject constructor(
    private val view: View,
    private val getCastDetailsUseCase: GetCastDetailsUseCase,
    private val workDetailsRoute: WorkDetailsRoute,
    private val coroutineDispatchers: CoroutineDispatchers
) : AutoCancelableCoroutineScopePresenter() {

    fun loadCastDetails(castViewModel: CastViewModel) {
        launch(coroutineDispatchers.mainDispatcher) {
            view.onShowProgress()
            try {
                withContext(coroutineDispatchers.ioDispatcher) {
                    getCastDetailsUseCase(castViewModel.id).run {
                        val cast = first.toViewModel()
                        val movies = second?.map { it.toViewModel() }
                        val tvShow = third?.map { it.toViewModel() }
                        Triple(cast, movies, tvShow)
                    }
                }.also {
                    view.onCastLoaded(it.first, it.second, it.third)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error while getting the cast details")
                view.onErrorCastDetailsLoaded()
            } finally {
                view.onHideProgress()
            }
        }
    }

    fun workClicked(itemViewHolder: Presenter.ViewHolder, workViewModel: WorkViewModel) {
        val intent = workDetailsRoute.buildWorkDetailIntent(workViewModel)
        view.openWorkDetails(itemViewHolder, intent)
    }

    interface View {

        fun onShowProgress()

        fun onHideProgress()

        fun onCastLoaded(
            castViewModel: CastViewModel?,
            movies: List<WorkViewModel>?,
            tvShow: List<WorkViewModel>?
        )

        fun onErrorCastDetailsLoaded()

        fun openWorkDetails(itemViewHolder: Presenter.ViewHolder, intent: Intent)
    }
}
