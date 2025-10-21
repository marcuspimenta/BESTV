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

package com.pimenta.bestv.workdetail.presentation.viewmodel

import app.cash.turbine.test
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.pimenta.bestv.model.domain.CastDomainModel
import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.workdetail.domain.GetRecommendationByWorkUseCase
import com.pimenta.bestv.workdetail.domain.GetReviewByWorkUseCase
import com.pimenta.bestv.workdetail.domain.GetSimilarByWorkUseCase
import com.pimenta.bestv.workdetail.domain.GetWorkDetailsUseCase
import com.pimenta.bestv.workdetail.domain.SetFavoriteUseCase
import com.pimenta.bestv.workdetail.domain.model.ReviewDomainModel
import com.pimenta.bestv.workdetail.domain.model.VideoDomainModel
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEffect
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for WorkDetailsViewModel following MVI architecture with Coroutines
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WorkDetailsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var setFavoriteUseCase: SetFavoriteUseCase
    private lateinit var getWorkDetailsUseCase: GetWorkDetailsUseCase
    private lateinit var getReviewByWorkUseCase: GetReviewByWorkUseCase
    private lateinit var getRecommendationByWorkUseCase: GetRecommendationByWorkUseCase
    private lateinit var getSimilarByWorkUseCase: GetSimilarByWorkUseCase
    private lateinit var viewModel: WorkDetailsViewModel

    private val work = WorkViewModel(
        id = 1,
        title = "Test Movie",
        type = WorkType.MOVIE
    )

    private val videoList = listOf(
        VideoDomainModel(
            id = "1",
            name = "Trailer"
        )
    )

    private val castList = listOf(
        CastDomainModel(
            id = 1,
            name = "Actor Name",
            character = "Character Name",
            birthday = "1990-01-01",
            deathDay = null,
            biography = null
        )
    )

    private val workPage = PageDomainModel<WorkDomainModel>(
        page = 1,
        totalPages = 5,
        results = listOf(
            WorkDomainModel(
                id = 2,
                title = "Recommended Movie"
            )
        )
    )

    private val reviewPage = PageDomainModel<ReviewDomainModel>(
        page = 1,
        totalPages = 3,
        results = listOf(
            ReviewDomainModel(
                id = "1",
                author = "Reviewer",
                content = "Great movie!"
            )
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        setFavoriteUseCase = mock()
        getWorkDetailsUseCase = mock()
        getReviewByWorkUseCase = mock()
        getRecommendationByWorkUseCase = mock()
        getSimilarByWorkUseCase = mock()

        viewModel = WorkDetailsViewModel(
            work = work,
            setFavoriteUseCase = setFavoriteUseCase,
            getWorkDetailsUseCase = getWorkDetailsUseCase,
            getReviewByWorkUseCase = getReviewByWorkUseCase,
            getRecommendationByWorkUseCase = getRecommendationByWorkUseCase,
            getSimilarByWorkUseCase = getSimilarByWorkUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have correct work`() {
        val initialState = viewModel.state.value
        assertEquals(work, initialState.work)
        assertFalse(initialState.isLoading)
        assertFalse(initialState.isFavorite)
        assertTrue(initialState.videos.isEmpty())
        assertTrue(initialState.casts.isEmpty())
        assertTrue(initialState.reviews.isEmpty())
        assertTrue(initialState.recommendedWorks.isEmpty())
        assertTrue(initialState.similarWorks.isEmpty())
        assertNull(initialState.error)
    }

    @Test
    fun `loadData should update state with loaded data`() = runTest(testDispatcher) {
        val workDetails = GetWorkDetailsUseCase.WorkDetailsDomainWrapper(
            isFavorite = true,
            videos = videoList,
            casts = castList,
            recommended = workPage,
            similar = workPage,
            reviews = reviewPage
        )

        whenever(getWorkDetailsUseCase(work)).thenReturn(workDetails)

        viewModel.handleEvent(WorkDetailsEvent.LoadData)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertTrue(state.isFavorite)
        assertEquals(1, state.videos.size)
        assertEquals(1, state.casts.size)
        assertEquals(1, state.reviews.size)
        assertEquals(1, state.recommendedWorks.size)
        assertEquals(1, state.similarWorks.size)
        assertNull(state.error)
    }

    @Test
    fun `loadData should show loading state`() = runTest(testDispatcher) {
        val workDetails = GetWorkDetailsUseCase.WorkDetailsDomainWrapper(
            isFavorite = false,
            videos = emptyList(),
            casts = emptyList(),
            recommended = PageDomainModel(1, 1, emptyList()),
            similar = PageDomainModel(1, 1, emptyList()),
            reviews = PageDomainModel(1, 1, emptyList())
        )

        whenever(getWorkDetailsUseCase(work)).thenReturn(workDetails)

        viewModel.state.test {
            val initialState = awaitItem()
            assertFalse(initialState.isLoading)

            viewModel.handleEvent(WorkDetailsEvent.LoadData)

            // Check loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            advanceUntilIdle()

            // Check state after loading completes
            val finalState = awaitItem()
            assertFalse(finalState.isLoading)
        }
    }

    @Test
    fun `loadData should handle error`() = runTest(testDispatcher) {
        val exception = RuntimeException("Network error")
        whenever(getWorkDetailsUseCase(work)).thenThrow(exception)

        viewModel.handleEvent(WorkDetailsEvent.LoadData)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
    }

    @Test
    fun `toggleFavorite should update favorite state`() = runTest(testDispatcher) {
        whenever(setFavoriteUseCase(any())).thenReturn(Unit)

        viewModel.state.test {
            val initialState = awaitItem()
            assertFalse(initialState.isFavorite)

            viewModel.handleEvent(WorkDetailsEvent.ToggleFavorite)
            advanceUntilIdle()

            // Skip intermediate states and get final state
            val finalState = expectMostRecentItem()
            assertTrue(finalState.isFavorite)
        }
    }

    @Test
    fun `toggleFavorite should emit ShowFavoriteSuccess effect`() = runTest(testDispatcher) {
        whenever(setFavoriteUseCase(any())).thenReturn(Unit)

        viewModel.effects.test {
            viewModel.handleEvent(WorkDetailsEvent.ToggleFavorite)
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is WorkDetailsEffect.ShowFavoriteSuccess)
            assertEquals(true, (effect as WorkDetailsEffect.ShowFavoriteSuccess).isFavorite)
        }
    }

    @Test
    fun `workClicked should emit NavigateToWorkDetails effect`() = runTest(testDispatcher) {
        val clickedWork = WorkViewModel(id = 2, title = "Another Movie", type = WorkType.MOVIE)

        viewModel.effects.test {
            viewModel.handleEvent(WorkDetailsEvent.WorkClicked(clickedWork))
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is WorkDetailsEffect.NavigateToWorkDetails)
            assertEquals(clickedWork, (effect as WorkDetailsEffect.NavigateToWorkDetails).work)
        }
    }

    @Test
    fun `videoClicked should emit OpenVideo effect`() = runTest(testDispatcher) {
        val video = com.pimenta.bestv.workdetail.presentation.model.VideoViewModel(
            id = "1",
            type = "Trailer",
            youtubeUrl = "https://youtube.com/watch?v=123"
        )

        viewModel.effects.test {
            viewModel.handleEvent(WorkDetailsEvent.VideoClicked(video))
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is WorkDetailsEffect.OpenVideo)
            assertEquals(video, (effect as WorkDetailsEffect.OpenVideo).video)
        }
    }

    @Test
    fun `reviewItemSelected should load more reviews when needed`() = runTest(testDispatcher) {
        // Setup initial state with one page of reviews
        val initialDetails = GetWorkDetailsUseCase.WorkDetailsDomainWrapper(
            isFavorite = false,
            videos = emptyList(),
            casts = emptyList(),
            recommended = PageDomainModel(1, 1, emptyList()),
            similar = PageDomainModel(1, 1, emptyList()),
            reviews = reviewPage.copy(totalPages = 2)
        )

        val secondReviewPage = PageDomainModel<ReviewDomainModel>(
            page = 2,
            totalPages = 2,
            results = listOf(
                ReviewDomainModel(
                    id = "2",
                    author = "Another Reviewer",
                    content = "Also great!"
                )
            )
        )

        whenever(getWorkDetailsUseCase(work)).thenReturn(initialDetails)
        whenever(getReviewByWorkUseCase(WorkType.MOVIE, work.id, 2))
            .thenReturn(secondReviewPage)

        // Load initial data
        viewModel.handleEvent(WorkDetailsEvent.LoadData)
        advanceUntilIdle()

        val firstReview = viewModel.state.value.reviews.first()

        // Select last review item
        viewModel.handleEvent(WorkDetailsEvent.ReviewItemSelected(firstReview))
        advanceUntilIdle()

        val finalState = viewModel.state.value
        assertEquals(2, finalState.reviews.size)
        assertEquals(2, finalState.reviewPagination.currentPage)
    }
}
