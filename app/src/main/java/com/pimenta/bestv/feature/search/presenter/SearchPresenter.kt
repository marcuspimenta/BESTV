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

import com.pimenta.bestv.common.presentation.model.WorkViewModel
import com.pimenta.bestv.common.usecase.SearchWorksByQueryUseCase
import com.pimenta.bestv.common.usecase.WorkUseCase
import com.pimenta.bestv.extension.addTo
import com.pimenta.bestv.feature.base.AutoDisposablePresenter
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by marcus on 12-03-2018.
 */
class SearchPresenter @Inject constructor(
        private val view: View,
        private val workUseCase: WorkUseCase,
        private val searchWorksByQueryUseCase: SearchWorksByQueryUseCase
) : AutoDisposablePresenter() {

    private var resultMoviePage = 0
    private var resultTvShowPage = 0
    private var query: String = ""
    private var searchWorkDisposable: Disposable? = null
    private var loadBackdropImageDisposable: Disposable? = null

    override fun dispose() {
        disposeSearchWork()
        disposeLoadBackdropImage()
        super.dispose()
    }

    /**
     * Searches the movies by a query
     *
     * @param text Query to search the movies
     */
    fun searchWorksByQuery(text: String) {
        disposeSearchWork()
        resultMoviePage = 0
        resultTvShowPage = 0
        query = text
        searchWorkDisposable = searchWorksByQueryUseCase(text)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ pair ->
                    var movies: List<WorkViewModel>? = null
                    if (pair.first != null && pair.first.page <= pair.first.totalPages) {
                        this.resultMoviePage = pair.first.page
                        movies = pair.first.works
                    }

                    var tvShows: List<WorkViewModel>? = null
                    if (pair.second != null && pair.second.page <= pair.second.totalPages) {
                        this.resultTvShowPage = pair.second.page
                        tvShows = pair.second.works
                    }
                    view.onResultLoaded(movies, tvShows)
                }, { throwable ->
                    Timber.e(throwable, "Error while searching movies by query")
                    view.onResultLoaded(null, null)
                })
    }

    /**
     * Load the movies by a query
     */
    fun loadMovies() {
        workUseCase.searchMoviesByQuery(query, resultMoviePage + 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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

    /**
     * Load the tv shows by a query
     */
    fun loadTvShows() {
        workUseCase.searchTvShowsByQuery(query, resultTvShowPage + 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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

    /**
     * Set timer to load the backdrop image
     *
     * @param workViewModel [WorkViewModel]
     */
    fun countTimerLoadBackdropImage(workViewModel: WorkViewModel) {
        disposeLoadBackdropImage()
        loadBackdropImageDisposable = Completable
                .timer(BACKGROUND_UPDATE_DELAY, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.loadBackdropImage(workViewModel)
                }, { throwable ->
                    Timber.e(throwable, "Error while loading backdrop image")
                })
    }

    /**
     * Disposes the search works.
     */
    private fun disposeSearchWork() {
        searchWorkDisposable?.let {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
    }

    /**
     * Disposes the load backdrop image.
     */
    private fun disposeLoadBackdropImage() {
        loadBackdropImageDisposable?.let {
            if (!it.isDisposed) {
                it.dispose()
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