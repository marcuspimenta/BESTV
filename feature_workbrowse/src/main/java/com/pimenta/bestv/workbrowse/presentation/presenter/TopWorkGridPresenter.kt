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

import android.content.Intent
import androidx.leanback.widget.Presenter
import com.pimenta.bestv.model.presentation.mapper.toViewModel
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.di.annotation.FragmentScope
import com.pimenta.bestv.presentation.dispatcher.CoroutineDispatchers
import com.pimenta.bestv.presentation.presenter.AutoCancelableCoroutineScopePresenter
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
import com.pimenta.bestv.workbrowse.domain.LoadWorkByTypeUseCase
import com.pimenta.bestv.workbrowse.presentation.model.TopWorkTypeViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val BACKGROUND_UPDATE_DELAY = 300L

/**
 * Created by marcus on 09-02-2018.
 */
@FragmentScope
class TopWorkGridPresenter @Inject constructor(
    private val view: View,
    private val loadWorkByTypeUseCase: LoadWorkByTypeUseCase,
    private val workDetailsRoute: WorkDetailsRoute,
    coroutineDispatchers: CoroutineDispatchers
) : AutoCancelableCoroutineScopePresenter(coroutineDispatchers) {

    private var currentPage = 0
    private var totalPages = 0
    private val works by lazy { mutableListOf<WorkViewModel>() }

    private var loadBackdropImageJob: Job? = null

    fun refreshPage(topWorkTypeViewModel: TopWorkTypeViewModel) {
        if (topWorkTypeViewModel == TopWorkTypeViewModel.FAVORITES_MOVIES) {
            resetPage()
            loadWorkPageByType(topWorkTypeViewModel)
        }
    }

    fun loadWorkPageByType(topWorkTypeViewModel: TopWorkTypeViewModel) {
        if (currentPage != 0 && currentPage + 1 > totalPages) {
            return
        }

        launch {
            view.onShowProgress()
            try {
                loadWorkByTypeUseCase(currentPage + 1, topWorkTypeViewModel)?.toViewModel()?.let { workPage ->
                    currentPage = workPage.page
                    totalPages = workPage.totalPages

                    workPage.results?.let { worksViewModel ->
                        works.addAll(worksViewModel)
                        view.onWorksLoaded(works)
                    }
                }
            } catch (throwable: Throwable) {
                Timber.e(throwable, "Error while loading the works by type")
                view.onErrorWorksLoaded()
            } finally {
                view.onHideProgress()
            }
        }
    }

    fun countTimerLoadBackdropImage(workViewModel: WorkViewModel) {
        loadBackdropImageJob?.cancel()
        loadBackdropImageJob = launch {
            try {
                delay(BACKGROUND_UPDATE_DELAY)
                view.loadBackdropImage(workViewModel)
            } catch (throwable: Throwable) {
                Timber.e(throwable, "Error while loading backdrop image")
            }
        }
    }

    fun workClicked(itemViewHolder: Presenter.ViewHolder, workViewModel: WorkViewModel) {
        val intent = workDetailsRoute.buildWorkDetailIntent(workViewModel)
        view.openWorkDetails(itemViewHolder, intent)
    }

    private fun resetPage() {
        currentPage = 0
        totalPages = 0
        works.clear()
    }

    interface View {

        fun onShowProgress()

        fun onHideProgress()

        fun onWorksLoaded(works: List<WorkViewModel>)

        fun loadBackdropImage(workViewModel: WorkViewModel)

        fun onErrorWorksLoaded()

        fun openWorkDetails(itemViewHolder: Presenter.ViewHolder, intent: Intent)
    }
}
