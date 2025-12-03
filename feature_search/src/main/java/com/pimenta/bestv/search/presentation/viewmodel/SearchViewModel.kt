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
import com.pimenta.bestv.presentation.model.PaginationState
import com.pimenta.bestv.search.presentation.model.SearchEffect
import com.pimenta.bestv.search.presentation.model.SearchEvent
import com.pimenta.bestv.search.presentation.model.SearchState
import com.pimenta.bestv.search.presentation.model.SearchState.Content
import com.pimenta.bestv.search.presentation.model.SearchState.Content.Movies
import com.pimenta.bestv.search.presentation.model.SearchState.Content.TvShows
import com.pimenta.bestv.search.presentation.model.SearchState.State.Empty
import com.pimenta.bestv.search.presentation.model.SearchState.State.Error
import com.pimenta.bestv.search.presentation.model.SearchState.State.Loaded
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

private const val BACKGROUND_UPDATE_DELAY = 300L
private const val SEARCH_DELAY = 500L

/**
 * ViewModel for the Search screen following MVI architecture.
 * Manages the state and handles user events.
 *
 * Created by marcus on 23-10-2025.
 */
class SearchViewModel(
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
            SearchState(query = "", isSearching = false, state = Empty)
        }
    }

    private fun searchWorksByQuery(query: String) {
        // Cancel previous search
        searchJob?.cancel()

        if (query.hasNoContent()) {
            updateState { SearchState(query = "", isSearching = false, state = Empty) }
            return
        }

        // Set searching state at top level
        updateState { currentState ->
            currentState.copy(
                query = query,
                isSearching = true
            )
        }

        searchJob = viewModelScope.launch {
            try {
                delay(SEARCH_DELAY)

                val result = searchWorksByQueryUseCase(query)
                val moviePage = result.first.toViewModel()
                val tvShowPage = result.second.toViewModel()

                val contents = mutableListOf<Content>()
                if (moviePage.results.isNotEmpty()) {
                    contents.add(
                        Movies(
                            query = query,
                            movies = moviePage.results,
                            page = PaginationState(
                                currentPage = moviePage.page,
                                totalPages = moviePage.totalPages
                            )
                        )
                    )
                }
                if (tvShowPage.results.isNotEmpty()) {
                    contents.add(
                        TvShows(
                            query = query,
                            tvShows = tvShowPage.results,
                            page = PaginationState(
                                currentPage = tvShowPage.page,
                                totalPages = tvShowPage.totalPages
                            )
                        )
                    )
                }

                updateState {
                    it.copy(
                        isSearching = false,
                        state = Loaded(
                            contents = contents
                        )
                    )
                }
            } catch (throwable: Throwable) {
                if (throwable !is CancellationException) {
                    Timber.e(throwable, "Error while searching by query")
                    updateState {
                        it.copy(
                            isSearching = false,
                            state = Error
                        )
                    }
                }
            }
        }
    }

    private fun loadMoreMovies() {
        val loadedState = currentState.state as? Loaded ?: return
        val moviesContent = loadedState.contents.filterIsInstance<Movies>().firstOrNull() ?: return

        if (!moviesContent.page.canLoadMore) {
            return
        }

        // Set loading state
        updateState { state ->
            val currentLoadedState = state.state as? Loaded ?: return@updateState state
            val updatedContents = currentLoadedState.contents.map { content ->
                if (content is Movies) {
                    content.copy(page = content.page.copy(isLoadingMore = true))
                } else {
                    content
                }
            }
            state.copy(
                state = currentLoadedState.copy(contents = updatedContents)
            )
        }

        viewModelScope.launch {
            try {
                val nextPage = moviesContent.page.currentPage + 1
                val moviePage = searchMoviesByQueryUseCase(this@SearchViewModel.currentState.query, nextPage).toViewModel()

                updateState { state ->
                    val currentLoadedState = state.state as? Loaded ?: return@updateState state
                    val updatedContents = currentLoadedState.contents.map { content ->
                        if (content is Movies) {
                            content.copy(
                                movies = content.movies + moviePage.results.orEmpty(),
                                page = PaginationState(
                                    currentPage = moviePage.page,
                                    totalPages = moviePage.totalPages,
                                    isLoadingMore = false
                                )
                            )
                        } else {
                            content
                        }
                    }
                    state.copy(
                        state = currentLoadedState.copy(contents = updatedContents)
                    )
                }
            } catch (throwable: Throwable) {
                Timber.e(throwable, "Error while loading more movies")
                updateState { state ->
                    val currentLoadedState = state.state as? Loaded ?: return@updateState state
                    val updatedContents = currentLoadedState.contents.map { content ->
                        if (content is Movies) {
                            content.copy(page = content.page.copy(isLoadingMore = false))
                        } else {
                            content
                        }
                    }
                    state.copy(
                        state = currentLoadedState.copy(contents = updatedContents)
                    )
                }
            }
        }
    }

    private fun loadMoreTvShows() {
        val loadedState = currentState.state as? Loaded ?: return
        val tvShowsContent = loadedState.contents.filterIsInstance<TvShows>().firstOrNull() ?: return

        if (!tvShowsContent.page.canLoadMore) {
            return
        }

        // Set loading state
        updateState { state ->
            val currentLoadedState = state.state as? Loaded ?: return@updateState state
            val updatedContents = currentLoadedState.contents.map { content ->
                if (content is TvShows) {
                    content.copy(page = content.page.copy(isLoadingMore = true))
                } else {
                    content
                }
            }
            state.copy(
                state = currentLoadedState.copy(contents = updatedContents)
            )
        }

        viewModelScope.launch {
            try {
                val nextPage = tvShowsContent.page.currentPage + 1
                val tvShowPage = searchTvShowsByQueryUseCase(this@SearchViewModel.currentState.query, nextPage).toViewModel()

                updateState { state ->
                    val currentLoadedState = state.state as? Loaded ?: return@updateState state
                    val updatedContents = currentLoadedState.contents.map { content ->
                        if (content is TvShows) {
                            content.copy(
                                tvShows = content.tvShows + tvShowPage.results.orEmpty(),
                                page = PaginationState(
                                    currentPage = tvShowPage.page,
                                    totalPages = tvShowPage.totalPages,
                                    isLoadingMore = false
                                )
                            )
                        } else {
                            content
                        }
                    }
                    state.copy(
                        state = currentLoadedState.copy(contents = updatedContents)
                    )
                }
            } catch (throwable: Throwable) {
                Timber.e(throwable, "Error while loading more TV shows")
                updateState { state ->
                    val currentLoadedState = state.state as? Loaded ?: return@updateState state
                    val updatedContents = currentLoadedState.contents.map { content ->
                        if (content is TvShows) {
                            content.copy(page = content.page.copy(isLoadingMore = false))
                        } else {
                            content
                        }
                    }
                    state.copy(
                        state = currentLoadedState.copy(contents = updatedContents)
                    )
                }
            }
        }
    }

    private fun handleWorkItemSelected(work: WorkViewModel?) {
        // Cancel previous backdrop loading
        backdropJob?.cancel()

        val currentState = currentState.state as? Loaded ?: return

        if (work == null) {
            updateState { it.copy(state = currentState.copy(selectedWork = null)) }
            return
        }

        // Delay backdrop loading to avoid excessive updates
        backdropJob = viewModelScope.launch {
            delay(BACKGROUND_UPDATE_DELAY)
            val loadedState = this@SearchViewModel.currentState.state as? Loaded ?: return@launch
            updateState { state ->
                state.copy(
                    state = loadedState.copy(selectedWork = work)
                )
            }
        }
    }

    private fun handleWorkClicked(work: WorkViewModel) {
        val intent = workDetailsRoute.buildWorkDetailIntent(work)
        emitEffect(SearchEffect.OpenWorkDetails(intent))
    }
}
