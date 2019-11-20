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

package com.pimenta.bestv.feature.main.presentation.presenter

import androidx.leanback.widget.Presenter
import com.pimenta.bestv.common.extension.addTo
import com.pimenta.bestv.common.mvp.AutoDisposablePresenter
import com.pimenta.bestv.common.presentation.mapper.toViewModel
import com.pimenta.bestv.common.presentation.model.TopWorkTypeViewModel
import com.pimenta.bestv.common.presentation.model.WorkViewModel
import com.pimenta.bestv.feature.main.domain.LoadWorkByTypeUseCase
import com.pimenta.bestv.scheduler.RxScheduler
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val BACKGROUND_UPDATE_DELAY = 300L

/**
 * Created by marcus on 09-02-2018.
 */
class TopWorkGridPresenter @Inject constructor(
    private val view: View,
    private val loadWorkByTypeUseCase: LoadWorkByTypeUseCase,
    private val rxScheduler: RxScheduler
) : AutoDisposablePresenter() {

    private var currentPage = 0
    private var totalPages = 0
    private val works = mutableListOf<WorkViewModel>()

    private var loadBackdropImageDisposable: Disposable? = null

    override fun dispose() {
        disposeLoadBackdropImage()
        super.dispose()
    }

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

        loadWorkByTypeUseCase(currentPage + 1, topWorkTypeViewModel)
                .subscribeOn(rxScheduler.ioScheduler)
                .observeOn(rxScheduler.mainScheduler)
                .doOnSubscribe { view.onShowProgress() }
                .doFinally { view.onHideProgress() }
                .subscribe({ workPage ->
                    workPage?.let {
                        currentPage = it.page
                        totalPages = it.totalPages
                        it.works?.let { pageWorks ->
                            works.addAll(pageWorks.map { work -> work.toViewModel() })
                            view.onWorksLoaded(works)
                        }
                    }
                }, { throwable ->
                    Timber.e(throwable, "Error while loading the works by type")
                    view.onErrorWorksLoaded()
                }).addTo(compositeDisposable)
    }

    fun countTimerLoadBackdropImage(workViewModel: WorkViewModel) {
        disposeLoadBackdropImage()
        loadBackdropImageDisposable = Completable
                .timer(BACKGROUND_UPDATE_DELAY, TimeUnit.MILLISECONDS)
                .subscribeOn(rxScheduler.ioScheduler)
                .observeOn(rxScheduler.mainScheduler)
                .subscribe({
                    view.loadBackdropImage(workViewModel)
                }, { throwable ->
                    Timber.e(throwable, "Error while loading backdrop image")
                })
    }

    fun workClicked(itemViewHolder: Presenter.ViewHolder, workViewModel: WorkViewModel) {
        view.openWorkDetails(itemViewHolder, workViewModel)
    }

    private fun disposeLoadBackdropImage() {
        loadBackdropImageDisposable?.run {
            if (!isDisposed) {
                dispose()
            }
        }
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

        fun openWorkDetails(itemViewHolder: Presenter.ViewHolder, workViewModel: WorkViewModel)
    }
}