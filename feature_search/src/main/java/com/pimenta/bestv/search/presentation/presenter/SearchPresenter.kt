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

package com.pimenta.bestv.search.presentation.presenter

import androidx.leanback.widget.Presenter
import com.pimenta.bestv.search.domain.SearchMoviesByQueryUseCase
import com.pimenta.bestv.search.domain.SearchTvShowsByQueryUseCase
import com.pimenta.bestv.search.domain.SearchWorksByQueryUseCase
import com.pimenta.bestv.model.presentation.mapper.toViewModel
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.extension.addTo
import com.pimenta.bestv.presentation.extension.hasNoContent
import com.pimenta.bestv.presentation.presenter.AutoDisposablePresenter
import com.pimenta.bestv.presentation.scheduler.RxScheduler
import com.pimenta.bestv.route.Route
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val BACKGROUND_UPDATE_DELAY = 300L

/**
 * Created by marcus on 12-03-2018.
 */
class SearchPresenter @Inject constructor(
    private val view: View,
    private val searchWorksByQueryUseCase: SearchWorksByQueryUseCase,
    private val searchMoviesByQueryUseCase: SearchMoviesByQueryUseCase,
    private val searchTvShowsByQueryUseCase: SearchTvShowsByQueryUseCase,
    private val workDetailsRoute: WorkDetailsRoute,
    private val rxScheduler: RxScheduler
) : AutoDisposablePresenter() {

    private var query: String = ""
    private var resultMoviePage = 0
    private var resultTvShowPage = 0
    private var searchWorkDisposable: Disposable? = null
    private var loadBackdropImageDisposable: Disposable? = null

    private val movies = mutableListOf<WorkViewModel>()
    private val tvShows = mutableListOf<WorkViewModel>()

    override fun dispose() {
        disposeSearchWork()
        disposeLoadBackdropImage()
        super.dispose()
    }

    fun searchWorksByQuery(text: String?) {
        disposeSearchWork()

        if (text == null || text.hasNoContent()) {
            view.onHideProgress()
            view.onClear()
            return
        }

        query = text
        resultMoviePage = 0
        resultTvShowPage = 0
        movies.clear()
        tvShows.clear()

        searchWorkDisposable = searchWorksByQueryUseCase(query)
                .subscribeOn(rxScheduler.ioScheduler)
                .observeOn(rxScheduler.mainScheduler)
                .doOnSubscribe { view.onShowProgress() }
                .doFinally { view.onHideProgress() }
                .subscribe({ pair ->
                    if (pair.first.page <= pair.first.totalPages) {
                        this.resultMoviePage = pair.first.page
                        pair.first.works?.let {
                            movies.addAll(it.map { work -> work.toViewModel() })
                        }
                    }

                    if (pair.second.page <= pair.second.totalPages) {
                        this.resultTvShowPage = pair.second.page
                        pair.second.works?.let {
                            movies.addAll(it.map { work -> work.toViewModel() })
                        }
                    }

                    if (movies.isNotEmpty() || tvShows.isNotEmpty()) {
                        view.onResultLoaded(movies, tvShows)
                    } else {
                        view.onClear()
                    }
                }, { throwable ->
                    Timber.e(throwable, "Error while searching by query")
                    view.onErrorSearch()
                })
    }

    fun loadMovies() {
        searchMoviesByQueryUseCase(query, resultMoviePage + 1)
                .subscribeOn(rxScheduler.ioScheduler)
                .observeOn(rxScheduler.mainScheduler)
                .subscribe({ moviePage ->
                    if (moviePage != null && moviePage.page <= moviePage.totalPages) {
                        this.resultMoviePage = moviePage.page
                        moviePage.works?.let {
                            movies.addAll(it.map { work -> work.toViewModel() })
                            view.onMoviesLoaded(movies)
                        }
                    }
                }, { throwable ->
                    Timber.e(throwable, "Error while loading movies by query")
                }).addTo(compositeDisposable)
    }

    fun loadTvShows() {
        searchTvShowsByQueryUseCase(query, resultTvShowPage + 1)
                .subscribeOn(rxScheduler.ioScheduler)
                .observeOn(rxScheduler.mainScheduler)
                .subscribe({ tvShowPage ->
                    if (tvShowPage != null && tvShowPage.page <= tvShowPage.totalPages) {
                        this.resultTvShowPage = tvShowPage.page
                        tvShowPage.works?.let {
                            movies.addAll(it.map { work -> work.toViewModel() })
                            view.onTvShowsLoaded(tvShows)
                        }
                    }
                }, { throwable ->
                    Timber.e(throwable, "Error while loading tv shows by query")
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

    private fun disposeSearchWork() {
        searchWorkDisposable?.run {
            if (!isDisposed) {
                dispose()
            }
        }
    }

    interface View {

        fun onShowProgress()

        fun onHideProgress()

        fun onClear()

        fun onResultLoaded(movies: List<WorkViewModel>, tvShows: List<WorkViewModel>)

        fun onMoviesLoaded(movies: List<WorkViewModel>)

        fun onTvShowsLoaded(tvShows: List<WorkViewModel>)

        fun loadBackdropImage(workViewModel: WorkViewModel)

        fun onErrorSearch()

        fun openWorkDetails(itemViewHolder: Presenter.ViewHolder, route: Route)
    }
}