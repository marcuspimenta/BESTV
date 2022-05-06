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

import android.content.Intent
import androidx.leanback.widget.Presenter
import androidx.lifecycle.LifecycleOwner
import com.pimenta.bestv.model.presentation.mapper.toViewModel
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.di.annotation.FragmentScope
import com.pimenta.bestv.presentation.dispatcher.CoroutineDispatchers
import com.pimenta.bestv.presentation.extension.cancelIfActive
import com.pimenta.bestv.presentation.extension.hasNoContent
import com.pimenta.bestv.presentation.presenter.AutoCancelableCoroutineScopePresenter
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
import com.pimenta.bestv.search.domain.SearchMoviesByQueryUseCase
import com.pimenta.bestv.search.domain.SearchTvShowsByQueryUseCase
import com.pimenta.bestv.search.domain.SearchWorksByQueryUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val BACKGROUND_UPDATE_DELAY = 300L

/**
 * Created by marcus on 12-03-2018.
 */
@FragmentScope
class SearchPresenter @Inject constructor(
    private val view: View,
    private val searchWorksByQueryUseCase: SearchWorksByQueryUseCase,
    private val searchMoviesByQueryUseCase: SearchMoviesByQueryUseCase,
    private val searchTvShowsByQueryUseCase: SearchTvShowsByQueryUseCase,
    private val workDetailsRoute: WorkDetailsRoute,
    coroutineDispatchers: CoroutineDispatchers
) : AutoCancelableCoroutineScopePresenter(coroutineDispatchers) {

    private var query: String = ""
    private var resultMoviePage = 0
    private var totalMoviePage = 0
    private var resultTvShowPage = 0
    private var totalTvShowPage = 0
    private var searchWorkJob: Job? = null
    private var loadBackdropImageJob: Job? = null

    private val movies by lazy { mutableListOf<WorkViewModel>() }
    private val tvShows by lazy { mutableListOf<WorkViewModel>() }

    override fun onDestroy(owner: LifecycleOwner) {
        searchWorkJob?.cancelIfActive()
        loadBackdropImageJob?.cancelIfActive()
        super.onDestroy(owner)
    }

    fun searchWorksByQuery(text: String?) {
        searchWorkJob?.cancelIfActive()

        if (text == null || text.hasNoContent()) {
            view.onHideProgress()
            view.onClear()
            return
        }

        query = text
        resultMoviePage = 0
        totalMoviePage = 0
        resultTvShowPage = 0
        totalTvShowPage = 0
        movies.clear()
        tvShows.clear()

        searchWorkJob = launch {
            try {
                view.onShowProgress()
                searchWorksByQueryUseCase(query).run {
                    val moviePage = first.toViewModel()
                    val tvShowPage = second.toViewModel()
                    moviePage to tvShowPage
                }.let {
                    with(it.first) {
                        resultMoviePage = page
                        totalMoviePage = totalPages
                        results?.let {
                            movies.addAll(it)
                        }
                    }

                    with(it.second) {
                        resultTvShowPage = page
                        totalTvShowPage = totalPages
                        results?.let {
                            tvShows.addAll(it)
                        }
                    }

                    if (movies.isNotEmpty() || tvShows.isNotEmpty()) {
                        view.onResultLoaded(movies, tvShows)
                    } else {
                        view.onClear()
                    }
                }
                view.onHideProgress()
            } catch (e: Exception) {
                Timber.e(e, "Error while searching by query")
                if (e !is CancellationException) {
                    view.onErrorSearch()
                    view.onHideProgress()
                }
            }
        }
    }

    fun loadMovies() {
        if (resultMoviePage != 0 && resultMoviePage + 1 > totalMoviePage) {
            return
        }

        launch {
            try {
                searchMoviesByQueryUseCase(query, resultMoviePage + 1).run {
                    toViewModel()
                }.let {
                    resultMoviePage = it.page
                    totalMoviePage = it.totalPages
                    it.results?.let { works ->
                        movies.addAll(works)
                        view.onMoviesLoaded(movies)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error while loading movies by query")
            }
        }
    }

    fun loadTvShows() {
        if (resultTvShowPage != 0 && resultTvShowPage + 1 > totalTvShowPage) {
            return
        }

        launch {
            try {
                searchTvShowsByQueryUseCase(query, resultTvShowPage + 1).run {
                    toViewModel()
                }.let {
                    resultTvShowPage = it.page
                    totalTvShowPage = it.totalPages
                    it.results?.let { works ->
                        tvShows.addAll(works)
                        view.onTvShowsLoaded(tvShows)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error while loading tv shows by query")
            }
        }
    }

    fun countTimerLoadBackdropImage(workViewModel: WorkViewModel) {
        loadBackdropImageJob?.cancelIfActive()
        loadBackdropImageJob = launch {
            delay(BACKGROUND_UPDATE_DELAY)
            view.loadBackdropImage(workViewModel)
        }
    }

    fun workClicked(itemViewHolder: Presenter.ViewHolder, workViewModel: WorkViewModel) {
        val intent = workDetailsRoute.buildWorkDetailIntent(workViewModel)
        view.openWorkDetails(itemViewHolder, intent)
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

        fun openWorkDetails(itemViewHolder: Presenter.ViewHolder, intent: Intent)
    }
}
