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

package com.pimenta.bestv.search.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.pimenta.bestv.model.presentation.mapper.toViewModel
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.extension.hasNoContent
import com.pimenta.bestv.presentation.presenter.BaseViewModel
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
import com.pimenta.bestv.search.domain.SearchMoviesByQueryUseCase
import com.pimenta.bestv.search.domain.SearchTvShowsByQueryUseCase
import com.pimenta.bestv.search.domain.SearchWorksByQueryUseCase
import com.pimenta.bestv.search.presentation.model.PaginationState
import com.pimenta.bestv.search.presentation.model.SearchEffect
import com.pimenta.bestv.search.presentation.model.SearchEvent
import com.pimenta.bestv.search.presentation.model.SearchState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val BACKGROUND_UPDATE_DELAY = 300L

/**
 * ViewModel for the Search screen following MVI architecture.
 * Manages the state and handles user events.
 *
 * Created by marcus on 23-10-2025.
 */
class SearchViewModel @Inject constructor(
    private val searchWorksByQueryUseCase: SearchWorksByQueryUseCase,
    private val searchMoviesByQueryUseCase: SearchMoviesByQueryUseCase,
    private val searchTvShowsByQueryUseCase: SearchTvShowsByQueryUseCase,
    private val workDetailsRoute: WorkDetailsRoute
) : BaseViewModel<SearchState, SearchEffect>(SearchState()) {

    private var searchJob: Job? = null
    private var backdropJob: Job? = null

    /**
     * Handle user events
     */
    fun handleEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.SearchQueryChanged -> handleSearchQueryChanged(event.query)
            is SearchEvent.SearchQuerySubmitted -> handleSearchQuerySubmitted(event.query)
            is SearchEvent.ClearSearch -> handleClearSearch()
            is SearchEvent.LoadMoreMovies -> loadMoreMovies()
            is SearchEvent.LoadMoreTvShows -> loadMoreTvShows()
            is SearchEvent.WorkItemSelected -> handleWorkItemSelected(event.work)
            is SearchEvent.WorkClicked -> handleWorkClicked(event.work)
        }
    }

    private fun handleSearchQueryChanged(query: String) {
        searchWorksByQuery(query)
    }

    private fun handleSearchQuerySubmitted(query: String) {
        searchWorksByQuery(query)
    }

    private fun handleClearSearch() {
        searchJob?.cancel()
        backdropJob?.cancel()
        updateState {
            SearchState()
        }
    }

    private fun searchWorksByQuery(query: String) {
        // Cancel previous search
        searchJob?.cancel()

        if (query.hasNoContent()) {
            updateState { SearchState() }
            return
        }

        updateState {
            it.copy(
                query = query,
                isSearching = true,
                error = null
            )
        }

        searchJob = viewModelScope.launch {
            try {
                val result = searchWorksByQueryUseCase(query)
                val moviePage = result.first.toViewModel()
                val tvShowPage = result.second.toViewModel()

                val movies = moviePage.results ?: emptyList()
                val tvShows = tvShowPage.results ?: emptyList()

                updateState {
                    it.copy(
                        isSearching = false,
                        movies = movies,
                        tvShows = tvShows,
                        moviePagination = PaginationState(
                            currentPage = moviePage.page,
                            totalPages = moviePage.totalPages
                        ),
                        tvShowPagination = PaginationState(
                            currentPage = tvShowPage.page,
                            totalPages = tvShowPage.totalPages
                        ),
                        hasResults = movies.isNotEmpty() || tvShows.isNotEmpty()
                    )
                }
            } catch (throwable: Throwable) {
                if (throwable !is CancellationException) {
                    Timber.e(throwable, "Error while searching by query")
                    updateState {
                        it.copy(
                            isSearching = false,
                            error = "Failed to search. Please try again."
                        )
                    }
                    emitEvent(SearchEffect.ShowError("Failed to search. Please try again."))
                }
            }
        }
    }

    private fun loadMoreMovies() {
        val state = currentState

        if (!state.moviePagination.hasMore || state.isSearching) {
            return
        }

        updateState { it.copy(isSearching = true) }

        viewModelScope.launch {
            try {
                val nextPage = state.moviePagination.currentPage + 1
                val moviePage = searchMoviesByQueryUseCase(state.query, nextPage).toViewModel()

                updateState { currentState ->
                    currentState.copy(
                        isSearching = false,
                        movies = currentState.movies + moviePage.results.orEmpty(),
                        moviePagination = PaginationState(
                            currentPage = moviePage.page,
                            totalPages = moviePage.totalPages,
                        )
                    )
                }
            } catch (throwable: Throwable) {
                Timber.e(throwable, "Error while loading more movies")
                updateState { it.copy(isSearching = false) }
            }
        }
    }

    private fun loadMoreTvShows() {
        val state = currentState

        if (!state.tvShowPagination.hasMore || state.isSearching) {
            return
        }

        updateState { it.copy(isSearching = true) }

        viewModelScope.launch {
            try {
                val nextPage = state.tvShowPagination.currentPage + 1
                val tvShowPage = searchTvShowsByQueryUseCase(state.query, nextPage).toViewModel()

                updateState { currentState ->
                    currentState.copy(
                        isSearching = true,
                        tvShows = currentState.tvShows + tvShowPage.results.orEmpty(),
                        tvShowPagination = PaginationState(
                            currentPage = tvShowPage.page,
                            totalPages = tvShowPage.totalPages,
                        )
                    )
                }
            } catch (throwable: Throwable) {
                Timber.e(throwable, "Error while loading more TV shows")
                updateState { it.copy(isSearching = true) }
            }
        }
    }

    private fun handleWorkItemSelected(work: WorkViewModel?) {
        // Cancel previous backdrop loading
        backdropJob?.cancel()

        if (work == null) {
            updateState { it.copy(selectedWork = null) }
            return
        }

        // Delay backdrop loading to avoid excessive updates
        backdropJob = viewModelScope.launch {
            delay(BACKGROUND_UPDATE_DELAY)
            updateState { it.copy(selectedWork = work) }
        }
    }

    private fun handleWorkClicked(work: WorkViewModel) {
        val intent = workDetailsRoute.buildWorkDetailIntent(work)
        emitEvent(SearchEffect.OpenWorkDetails(intent))
    }
}
