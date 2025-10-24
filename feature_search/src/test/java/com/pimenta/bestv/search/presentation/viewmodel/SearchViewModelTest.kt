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

import android.content.Intent
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
import com.pimenta.bestv.search.domain.SearchMoviesByQueryUseCase
import com.pimenta.bestv.search.domain.SearchTvShowsByQueryUseCase
import com.pimenta.bestv.search.domain.SearchWorksByQueryUseCase
import com.pimenta.bestv.search.presentation.model.SearchEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Created by marcus on 23-10-2025.
 */
private const val QUERY = "Batman"

private val WORK_VIEW_MODEL = WorkViewModel(
    id = 1,
    title = "Game of thrones",
    originalTitle = "Game of thrones",
    type = WorkType.TV_SHOW
)

private val MOVIE_VIEW_MODEL_LIST = listOf(
    WorkViewModel(
        id = 1,
        title = "Batman",
        originalTitle = "Batman",
        type = WorkType.MOVIE
    )
)

private val MOVIE_DOMAIN_MODEL_LIST = listOf(
    WorkDomainModel(
        id = 1,
        title = "Batman",
        originalTitle = "Batman",
        type = WorkDomainModel.Type.MOVIE
    )
)

private val MOVIE_PAGE_DOMAIN_MODEL = PageDomainModel(
    page = 1,
    totalPages = 10,
    results = MOVIE_DOMAIN_MODEL_LIST
)

private val TV_SHOW_VIEW_MODEL_LIST = listOf(
    WorkViewModel(
        id = 1,
        title = "Batman",
        originalTitle = "Batman",
        type = WorkType.TV_SHOW
    )
)

private val TV_SHOW_DOMAIN_MODEL_LIST = listOf(
    WorkDomainModel(
        id = 1,
        title = "Batman",
        originalTitle = "Batman",
        type = WorkDomainModel.Type.TV_SHOW
    )
)

private val TV_SHOW_PAGE_DOMAIN_MODEL = PageDomainModel(
    page = 1,
    totalPages = 10,
    results = TV_SHOW_DOMAIN_MODEL_LIST
)

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val searchWorksByQueryUseCase: SearchWorksByQueryUseCase = mock()
    private val searchMoviesByQueryUseCase: SearchMoviesByQueryUseCase = mock()
    private val searchTvShowsByQueryUseCase: SearchTvShowsByQueryUseCase = mock()
    private val workDetailsRoute: WorkDetailsRoute = mock()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: SearchViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SearchViewModel(
            searchWorksByQueryUseCase,
            searchMoviesByQueryUseCase,
            searchTvShowsByQueryUseCase,
            workDetailsRoute
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should search works by query and update state`() = runTest(testDispatcher) {
        val result = MOVIE_PAGE_DOMAIN_MODEL to TV_SHOW_PAGE_DOMAIN_MODEL

        whenever(searchWorksByQueryUseCase(QUERY)).thenReturn(result)

        // Trigger search
        viewModel.handleEvent(SearchEvent.SearchQueryChanged(QUERY))

        advanceUntilIdle()

        // State should have results
        val resultState = viewModel.state.value
        assertEquals(QUERY, resultState.query)
        assertFalse(resultState.isSearching)
        assertEquals(MOVIE_VIEW_MODEL_LIST, resultState.movies)
        assertEquals(TV_SHOW_VIEW_MODEL_LIST, resultState.tvShows)
        assertTrue(resultState.hasResults)
        assertEquals(1, resultState.moviePagination.currentPage)
        assertEquals(10, resultState.moviePagination.totalPages)
        assertEquals(1, resultState.tvShowPagination.currentPage)
        assertEquals(10, resultState.tvShowPagination.totalPages)
    }

    @Test
    fun `should update state with error when search fails`() = runTest(testDispatcher) {
        whenever(searchWorksByQueryUseCase(QUERY)).thenThrow(RuntimeException("Network error"))

        viewModel.handleEvent(SearchEvent.SearchQueryChanged(QUERY))

        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isSearching)
        assertNotNull(state.error)
        assertEquals("Failed to search. Please try again.", state.error)
    }

    @Test
    fun `should clear search when query is empty`() = runTest(testDispatcher) {
        // Search with empty query
        viewModel.handleEvent(SearchEvent.SearchQueryChanged(""))

        advanceUntilIdle()

        // State should be reset
        val state = viewModel.state.value
        assertEquals("", state.query)
        assertFalse(state.isSearching)
        assertEquals(emptyList<WorkViewModel>(), state.movies)
        assertEquals(emptyList<WorkViewModel>(), state.tvShows)
        assertFalse(state.hasResults)
    }

    @Test
    fun `should load more movies when pagination is requested`() = runTest(testDispatcher) {
        // First setup initial search
        val initialResult = MOVIE_PAGE_DOMAIN_MODEL to TV_SHOW_PAGE_DOMAIN_MODEL
        whenever(searchWorksByQueryUseCase(QUERY)).thenReturn(initialResult)

        viewModel.handleEvent(SearchEvent.SearchQueryChanged(QUERY))
        advanceUntilIdle()

        // Now setup pagination
        val page2Movies = MOVIE_PAGE_DOMAIN_MODEL.copy(page = 2)
        whenever(searchMoviesByQueryUseCase(QUERY, 2)).thenReturn(page2Movies)

        // Trigger pagination
        viewModel.handleEvent(SearchEvent.LoadMoreMovies)

        advanceUntilIdle()

        // State should have more movies
        val paginatedState = viewModel.state.value
        assertEquals(2, paginatedState.moviePagination.currentPage)
        assertTrue(paginatedState.movies.size >= MOVIE_VIEW_MODEL_LIST.size)
    }

    @Test
    fun `should load more tv shows when pagination is requested`() = runTest(testDispatcher) {
        // First setup initial search
        val initialResult = MOVIE_PAGE_DOMAIN_MODEL to TV_SHOW_PAGE_DOMAIN_MODEL
        whenever(searchWorksByQueryUseCase(QUERY)).thenReturn(initialResult)

        viewModel.handleEvent(SearchEvent.SearchQueryChanged(QUERY))
        advanceUntilIdle()

        // Now setup pagination
        val page2TvShows = TV_SHOW_PAGE_DOMAIN_MODEL.copy(page = 2)
        whenever(searchTvShowsByQueryUseCase(QUERY, 2)).thenReturn(page2TvShows)

        // Trigger pagination
        viewModel.handleEvent(SearchEvent.LoadMoreTvShows)

        advanceUntilIdle()

        // State should have more tv shows
        val paginatedState = viewModel.state.value
        assertEquals(2, paginatedState.tvShowPagination.currentPage)
        assertTrue(paginatedState.tvShows.size >= TV_SHOW_VIEW_MODEL_LIST.size)
    }

    @Test
    fun `should update selected work when work is selected`() = runTest(testDispatcher) {
        viewModel.handleEvent(SearchEvent.WorkItemSelected(WORK_VIEW_MODEL))

        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(WORK_VIEW_MODEL, state.selectedWork)
    }

    @Test
    fun `should call route when work is clicked`() = runTest(testDispatcher) {
        val intent = mock<Intent>()
        whenever(workDetailsRoute.buildWorkDetailIntent(WORK_VIEW_MODEL)).thenReturn(intent)

        viewModel.handleEvent(SearchEvent.WorkClicked(WORK_VIEW_MODEL))

        verify(workDetailsRoute).buildWorkDetailIntent(WORK_VIEW_MODEL)
    }

    @Test
    fun `should clear search when clear event is triggered`() = runTest(testDispatcher) {
        // First do a search
        val result = MOVIE_PAGE_DOMAIN_MODEL to TV_SHOW_PAGE_DOMAIN_MODEL
        whenever(searchWorksByQueryUseCase(QUERY)).thenReturn(result)

        viewModel.handleEvent(SearchEvent.SearchQueryChanged(QUERY))
        advanceUntilIdle()

        // Clear search
        viewModel.handleEvent(SearchEvent.ClearSearch)

        // State should be reset
        val clearedState = viewModel.state.value
        assertEquals("", clearedState.query)
        assertFalse(clearedState.isSearching)
        assertEquals(emptyList<WorkViewModel>(), clearedState.movies)
        assertEquals(emptyList<WorkViewModel>(), clearedState.tvShows)
        assertFalse(clearedState.hasResults)
    }
}
