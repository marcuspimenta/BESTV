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

import androidx.leanback.widget.Presenter
import com.pimenta.bestv.model.presentation.mapper.toViewModel
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.di.annotation.FragmentScope
import com.pimenta.bestv.presentation.extension.addTo
import com.pimenta.bestv.presentation.presenter.AutoDisposablePresenter
import com.pimenta.bestv.presentation.scheduler.RxScheduler
import com.pimenta.bestv.route.Route
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
import com.pimenta.bestv.workbrowse.domain.GetWorkByGenreUseCase
import com.pimenta.bestv.workbrowse.presentation.model.GenreViewModel
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import timber.log.Timber

/**
 * Created by marcus on 28-10-2018.
 */
private const val BACKGROUND_UPDATE_DELAY = 300L

@FragmentScope
class GenreGridPresenter @Inject constructor(
    private val view: View,
    private val getWorkByGenreUseCase: GetWorkByGenreUseCase,
    private val workDetailsRoute: WorkDetailsRoute,
    private val rxScheduler: RxScheduler
) : AutoDisposablePresenter() {

    private var currentPage = 0
    private var totalPages = 0
    private val works by lazy { mutableListOf<WorkViewModel>() }

    private var loadBackdropImageDisposable: Disposable? = null

    override fun dispose() {
        disposeLoadBackdropImage()
        super.dispose()
    }

    fun loadWorkByGenre(genreViewModel: GenreViewModel) {
        if (currentPage != 0 && currentPage + 1 > totalPages) {
            return
        }

        getWorkByGenreUseCase(genreViewModel, currentPage + 1)
                .subscribeOn(rxScheduler.ioScheduler)
                .observeOn(rxScheduler.computationScheduler)
                .map { it.toViewModel() }
                .observeOn(rxScheduler.mainScheduler)
                .doOnSubscribe { view.onShowProgress() }
                .doFinally { view.onHideProgress() }
                .subscribe({ workPage ->
                    workPage?.let {
                        currentPage = it.page
                        totalPages = it.totalPages

                        it.works?.let { worksViewModel ->
                            works.addAll(worksViewModel)
                            view.onWorksLoaded(works)
                        }
                    }
                }, { throwable ->
                    Timber.e(throwable, "Error while loading the works by genre")
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
        val route = workDetailsRoute.buildWorkDetailRoute(workViewModel)
        view.openWorkDetails(itemViewHolder, route)
    }

    private fun disposeLoadBackdropImage() {
        loadBackdropImageDisposable?.run {
            if (!isDisposed) {
                dispose()
            }
        }
    }

    interface View {

        fun onShowProgress()

        fun onHideProgress()

        fun onWorksLoaded(works: List<WorkViewModel>)

        fun loadBackdropImage(workViewModel: WorkViewModel)

        fun onErrorWorksLoaded()

        fun openWorkDetails(itemViewHolder: Presenter.ViewHolder, route: Route)
    }
}
