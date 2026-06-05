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
import com.pimenta.bestv.model.presentation.model.PageViewModel
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.presenter.BaseViewModel
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
import com.pimenta.bestv.search.domain.SearchMoviesByQueryUseCase
import com.pimenta.bestv.search.domain.SearchTvShowsByQueryUseCase
import com.pimenta.bestv.search.domain.SearchWorksByQueryUseCase
import com.pimenta.bestv.search.presentation.model.SearchEffect
import com.pimenta.bestv.search.presentation.model.SearchEvent
import com.pimenta.bestv.search.presentation.model.SearchState
import com.pimenta.bestv.search.presentation.model.SearchState.Content.Movies
import com.pimenta.bestv.search.presentation.model.SearchState.Content.TvShows
import com.pimenta.bestv.search.presentation.model.SearchState.State.Loaded
import com.pimenta.bestv.search.presentation.viewmodel.SearchRequestProcessor.SearchAction
import com.pimenta.bestv.search.presentation.viewmodel.SelectedWorkRequestProcessor.SelectedWorkAction
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

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
    private val workDetailsRoute: WorkDetailsRoute,
    private val searchRequestProcessor: SearchRequestProcessor,
    private val selectedWorkRequestProcessor: SelectedWorkRequestProcessor
) : BaseViewModel<SearchState, SearchEffect>(SearchState()) {

    init {
        observeSearchRequests()
        observeSelectedWorkRequests()
    }

    /**
     * Handle user events
     */
    fun handleEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.SearchQueryChanged -> handleSearchQuery(event.query)
            is SearchEvent.SearchQuerySubmitted -> handleSearchQuery(event.query)
            is SearchEvent.ClearSearch -> handleClearSearch()
            is SearchEvent.LoadMoreMovies -> loadMoreMovies()
            is SearchEvent.LoadMoreTvShows -> loadMoreTvShows()
            is SearchEvent.WorkItemSelected -> handleWorkItemSelected(event.work)
            is SearchEvent.WorkClicked -> handleWorkClicked(event.work)
        }
    }

    private fun handleSearchQuery(query: String) {
        updateState { currentState -> currentState.searchStarted(query) }
        emitSearchRequest(query)
    }

    private fun handleClearSearch() {
        updateState { clearSearch() }
        emitSearchRequest("")
    }

    private fun loadMoreMovies() {
        val moviesContent = currentState.moviesContent() ?: return
        if (!moviesContent.page.canLoadMore) {
            return
        }

        updateState { state -> state.moviesPaginationStarted() }
        loadMoreContent(
            nextPage = moviesContent.page.currentPage + 1,
            errorMessage = "Error while loading more movies",
            loadPage = { query, page -> searchMoviesByQueryUseCase(query, page).toViewModel() },
            onSuccess = SearchState::moviesPaginationSucceeded,
            onFailure = SearchState::moviesPaginationFailed
        )
    }

    private fun loadMoreTvShows() {
        val tvShowsContent = currentState.tvShowsContent() ?: return
        if (!tvShowsContent.page.canLoadMore) {
            return
        }

        updateState { state -> state.tvShowsPaginationStarted() }
        loadMoreContent(
            nextPage = tvShowsContent.page.currentPage + 1,
            errorMessage = "Error while loading more TV shows",
            loadPage = { query, page -> searchTvShowsByQueryUseCase(query, page).toViewModel() },
            onSuccess = SearchState::tvShowsPaginationSucceeded,
            onFailure = SearchState::tvShowsPaginationFailed
        )
    }

    private fun handleWorkItemSelected(work: WorkViewModel?) {
        if (currentState.state !is Loaded) return
        emitSelectedWorkRequest(work)
    }

    private fun handleWorkClicked(work: WorkViewModel) {
        val intent = workDetailsRoute.buildWorkDetailIntent(work)
        emitEffect(SearchEffect.OpenWorkDetails(intent))
    }

    private fun loadMoreContent(
        nextPage: Int,
        errorMessage: String,
        loadPage: suspend (query: String, page: Int) -> PageViewModel<WorkViewModel>,
        onSuccess: (SearchState, PageViewModel<WorkViewModel>) -> SearchState,
        onFailure: (SearchState) -> SearchState
    ) {
        viewModelScope.launch {
            try {
                val page = loadPage(currentState.query, nextPage)
                updateState { state -> onSuccess(state, page) }
            } catch (throwable: Throwable) {
                Timber.e(throwable, errorMessage)
                updateState { state -> onFailure(state) }
            }
        }
    }

    private fun observeSearchRequests() {
        viewModelScope.launch {
            searchRequestProcessor.observe()
                .collectLatest { action ->
                    when (action) {
                        is SearchAction.Clear -> updateState { clearSearch() }
                        is SearchAction.Search -> performSearch(action.query)
                    }
                }
        }
    }

    private fun observeSelectedWorkRequests() {
        viewModelScope.launch {
            selectedWorkRequestProcessor.observe()
                .collectLatest { action ->
                    if (currentState.state !is Loaded) return@collectLatest
                    when (action) {
                        is SelectedWorkAction.Clear -> updateState { state -> state.workSelected(null) }
                        is SelectedWorkAction.Select -> updateState { state -> state.workSelected(action.work) }
                    }
                }
        }
    }

    private fun emitSearchRequest(query: String) {
        viewModelScope.launch {
            searchRequestProcessor.emitSearchRequest(query)
        }
    }

    private fun emitSelectedWorkRequest(work: WorkViewModel?) {
        viewModelScope.launch {
            selectedWorkRequestProcessor.emitSelectedWorkRequest(work)
        }
    }

    private suspend fun performSearch(query: String) {
        try {
            val result = searchWorksByQueryUseCase(query)
            val moviePage = result.first.toViewModel()
            val tvShowPage = result.second.toViewModel()
            val contents = create(query, moviePage, tvShowPage)

            updateState { state -> state.searchLoaded(contents) }
        } catch (throwable: Throwable) {
            if (throwable !is CancellationException) {
                Timber.e(throwable, "Error while searching by query")
                updateState { state -> state.searchFailed() }
            }
        }
    }

    private fun SearchState.moviesContent() = (state as? Loaded)
        ?.contents
        ?.filterIsInstance<Movies>()
        ?.firstOrNull()

    private fun SearchState.tvShowsContent() = (state as? Loaded)
        ?.contents
        ?.filterIsInstance<TvShows>()
        ?.firstOrNull()
}
