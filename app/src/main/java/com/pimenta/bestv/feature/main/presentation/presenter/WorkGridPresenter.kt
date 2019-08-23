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

import com.pimenta.bestv.common.extension.addTo
import com.pimenta.bestv.common.mvp.AutoDisposablePresenter
import com.pimenta.bestv.common.presentation.model.GenreViewModel
import com.pimenta.bestv.common.presentation.model.WorkViewModel
import com.pimenta.bestv.common.domain.WorkUseCase
import com.pimenta.bestv.data.MediaRepository
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
class WorkGridPresenter @Inject constructor(
        private val view: View,
        private val workUseCase: WorkUseCase,
        private val rxScheduler: RxScheduler
) : AutoDisposablePresenter() {

    private var currentPage = 0
    private var loadBackdropImageDisposable: Disposable? = null
    private val works = mutableListOf<WorkViewModel>()

    override fun dispose() {
        disposeLoadBackdropImage()
        super.dispose()
    }

    fun loadWorksByType(movieListType: MediaRepository.WorkType) {
        when (movieListType) {
            MediaRepository.WorkType.FAVORITES_MOVIES ->
                workUseCase.getFavorites()
                        .subscribeOn(rxScheduler.ioScheduler)
                        .observeOn(rxScheduler.mainScheduler)
                        .doOnSubscribe { view.onShowProgress() }
                        .doOnTerminate { view.onHideProgress() }
                        .subscribe({ movies ->
                            view.onWorksLoaded(movies)
                        }, { throwable ->
                            Timber.e(throwable, "Error while loading the favorite works")
                            view.onErrorWorksLoaded()
                        }).addTo(compositeDisposable)
            else -> {
                workUseCase.loadWorkByType(currentPage + 1, movieListType)
                        .subscribeOn(rxScheduler.ioScheduler)
                        .observeOn(rxScheduler.mainScheduler)
                        .doOnSubscribe { view.onShowProgress() }
                        .doOnTerminate { view.onHideProgress() }
                        .subscribe({ workPage ->
                            if (workPage != null && workPage.page <= workPage.totalPages) {
                                currentPage = workPage.page
                                workPage.works?.let {
                                    works.addAll(it)
                                    view.onWorksLoaded(works)
                                }
                            }
                        }, { throwable ->
                            Timber.e(throwable, "Error while loading the works by type")
                            view.onErrorWorksLoaded()
                        }).addTo(compositeDisposable)
            }
        }
    }

    fun loadWorkByGenre(genreViewModel: GenreViewModel) {
        workUseCase.getWorkByGenre(genreViewModel, currentPage + 1)
                .subscribeOn(rxScheduler.ioScheduler)
                .observeOn(rxScheduler.mainScheduler)
                .doOnSubscribe { view.onShowProgress() }
                .doOnTerminate { view.onHideProgress() }
                .subscribe({ workPage ->
                    if (workPage != null && workPage.page <= workPage.totalPages) {
                        currentPage = workPage.page
                        workPage.works?.let {
                            works.addAll(it)
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

    }
}