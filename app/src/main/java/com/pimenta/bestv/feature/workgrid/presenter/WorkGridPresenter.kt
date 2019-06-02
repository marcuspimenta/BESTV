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

package com.pimenta.bestv.feature.workgrid.presenter

import com.pimenta.bestv.common.mvp.AutoDisposablePresenter
import com.pimenta.bestv.common.presentation.mapper.toGenre
import com.pimenta.bestv.common.presentation.mapper.toSingle
import com.pimenta.bestv.common.presentation.model.GenreViewModel
import com.pimenta.bestv.common.presentation.model.WorkViewModel
import com.pimenta.bestv.common.usecase.WorkUseCase
import com.pimenta.bestv.data.repository.MediaRepository
import com.pimenta.bestv.extension.addTo
import com.pimenta.bestv.scheduler.RxScheduler
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by marcus on 09-02-2018.
 */
class WorkGridPresenter @Inject constructor(
        private val view: View,
        private val workUseCase: WorkUseCase,
        private val rxScheduler: RxScheduler
) : AutoDisposablePresenter() {

    private var currentPage = 0
    private var loadBackdropImageDisposable: Disposable? = null
    private val works = mutableListOf<WorkViewModel>()

    override fun dispose() {
        view.onHideProgress()
        disposeLoadBackdropImage()
        super.dispose()
    }

    fun loadWorksByType(movieListType: MediaRepository.WorkType) {
        view.onShowProgress()
        when (movieListType) {
            MediaRepository.WorkType.FAVORITES_MOVIES ->
                workUseCase.getFavorites()
                        .subscribeOn(rxScheduler.ioScheduler)
                        .observeOn(rxScheduler.mainScheduler)
                        .subscribe({ movies ->
                            view.onWorksLoaded(movies)
                            view.onHideProgress()
                        }, { throwable ->
                            Timber.e(throwable, "Error while loading the favorite works")
                            view.onHideProgress()
                            view.onErrorWorksLoaded()
                        }).addTo(compositeDisposable)
            else -> {
                workUseCase.loadWorkByType(currentPage + 1, movieListType)
                        .subscribeOn(rxScheduler.ioScheduler)
                        .observeOn(rxScheduler.mainScheduler)
                        .subscribe({ workPage ->
                            if (workPage != null && workPage.page <= workPage.totalPages) {
                                currentPage = workPage.page
                                workPage.works?.let {
                                    works.addAll(it)
                                    view.onWorksLoaded(works)
                                }
                            }
                            view.onHideProgress()
                        }, { throwable ->
                            Timber.e(throwable, "Error while loading the works by type")
                            view.onHideProgress()
                            view.onErrorWorksLoaded()
                        }).addTo(compositeDisposable)
            }
        }
    }

    fun loadWorkByGenre(genreViewModel: GenreViewModel) {
        view.onShowProgress()
        genreViewModel.toGenre().toSingle()
                .flatMap { workUseCase.getWorkByGenre(it, currentPage + 1) }
                .subscribeOn(rxScheduler.ioScheduler)
                .observeOn(rxScheduler.mainScheduler)
                .subscribe({ workPage ->
                    if (workPage != null && workPage.page <= workPage.totalPages) {
                        currentPage = workPage.page
                        workPage.works?.let {
                            works.addAll(it)
                            view.onWorksLoaded(works)
                        }
                    }
                    view.onHideProgress()
                }, { throwable ->
                    Timber.e(throwable, "Error while loading the works by genre")
                    view.onHideProgress()
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

    private fun disposeLoadBackdropImage() {
        loadBackdropImageDisposable?.run {
            if (!isDisposed) {
                dispose()
            }
        }
    }

    companion object {

        private const val BACKGROUND_UPDATE_DELAY = 300L
    }

    interface View {

        fun onShowProgress()

        fun onHideProgress()

        fun onWorksLoaded(works: List<WorkViewModel>)

        fun loadBackdropImage(workViewModel: WorkViewModel)

        fun onErrorWorksLoaded()

    }
}