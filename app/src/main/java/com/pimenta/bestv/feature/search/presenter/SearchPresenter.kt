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

package com.pimenta.bestv.feature.search.presenter

import com.pimenta.bestv.common.mvp.AutoDisposablePresenter
import com.pimenta.bestv.common.presentation.model.WorkViewModel
import com.pimenta.bestv.common.usecase.WorkUseCase
import com.pimenta.bestv.extension.addTo
import com.pimenta.bestv.feature.search.usecase.SearchWorksByQueryUseCase
import com.pimenta.bestv.scheduler.RxScheduler
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by marcus on 12-03-2018.
 */
class SearchPresenter @Inject constructor(
        private val view: View,
        private val workUseCase: WorkUseCase,
        private val searchWorksByQueryUseCase: SearchWorksByQueryUseCase,
        private val rxScheduler: RxScheduler
) : AutoDisposablePresenter() {

    private var query: String = ""
    private var resultMoviePage = 0
    private var resultTvShowPage = 0
    private var searchWorkDisposable: Disposable? = null
    private var loadBackdropImageDisposable: Disposable? = null

    override fun dispose() {
        disposeSearchWork()
        disposeLoadBackdropImage()
        super.dispose()
    }

    fun searchWorksByQuery(text: String) {
        disposeSearchWork()
        resultMoviePage = 0
        resultTvShowPage = 0
        query = text
        searchWorkDisposable = searchWorksByQueryUseCase(text)
                .subscribeOn(rxScheduler.ioScheduler)
                .observeOn(rxScheduler.mainScheduler)
                .subscribe({ pair ->
                    var movies: List<WorkViewModel>? = null
                    if (pair.first.page <= pair.first.totalPages) {
                        this.resultMoviePage = pair.first.page
                        movies = pair.first.works
                    }

                    var tvShows: List<WorkViewModel>? = null
                    if (pair.second.page <= pair.second.totalPages) {
                        this.resultTvShowPage = pair.second.page
                        tvShows = pair.second.works
                    }
                    view.onResultLoaded(movies, tvShows)
                }, { throwable ->
                    Timber.e(throwable, "Error while searching movies by query")
                    view.onResultLoaded(null, null)
                })
    }

    fun loadMovies() {
        workUseCase.searchMoviesByQuery(query, resultMoviePage + 1)
                .subscribeOn(rxScheduler.ioScheduler)
                .observeOn(rxScheduler.mainScheduler)
                .subscribe({ moviePage ->
                    if (moviePage != null && moviePage.page <= moviePage.totalPages) {
                        this.resultMoviePage = moviePage.page
                        view.onMoviesLoaded(moviePage.works)
                    } else {
                        view.onMoviesLoaded(null)
                    }
                }, { throwable ->
                    Timber.e(throwable, "Error while loading movies by query")
                    view.onMoviesLoaded(null)
                }).addTo(compositeDisposable)
    }

    fun loadTvShows() {
        workUseCase.searchTvShowsByQuery(query, resultTvShowPage + 1)
                .subscribeOn(rxScheduler.ioScheduler)
                .observeOn(rxScheduler.mainScheduler)
                .subscribe({ tvShowPage ->
                    if (tvShowPage != null && tvShowPage.page <= tvShowPage.totalPages) {
                        this.resultTvShowPage = tvShowPage.page
                        view.onTvShowsLoaded(tvShowPage.works)
                    } else {
                        view.onTvShowsLoaded(null)
                    }
                }, { throwable ->
                    Timber.e(throwable, "Error while loading tv shows by query")
                    view.onTvShowsLoaded(null)
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

    private fun disposeSearchWork() {
        searchWorkDisposable?.run {
            if (!isDisposed) {
                dispose()
            }
        }
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

        fun onResultLoaded(movies: List<WorkViewModel>?, tvShows: List<WorkViewModel>?)

        fun onMoviesLoaded(movies: List<WorkViewModel>?)

        fun onTvShowsLoaded(tvShows: List<WorkViewModel>?)

        fun loadBackdropImage(workViewModel: WorkViewModel)

    }
}