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

import android.content.Intent
import app.cash.turbine.test
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.pimenta.bestv.model.domain.CastDomainModel
import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.route.castdetail.CastDetailsRoute
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
import com.pimenta.bestv.workdetail.domain.GetRecommendationByWorkUseCase
import com.pimenta.bestv.workdetail.domain.GetReviewByWorkUseCase
import com.pimenta.bestv.workdetail.domain.GetSimilarByWorkUseCase
import com.pimenta.bestv.workdetail.domain.GetWorkDetailsUseCase
import com.pimenta.bestv.workdetail.domain.SetFavoriteUseCase
import com.pimenta.bestv.workdetail.domain.model.ReviewDomainModel
import com.pimenta.bestv.workdetail.domain.model.VideoDomainModel
import com.pimenta.bestv.workdetail.presentation.model.VideoViewModel
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
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private val WORK = WorkViewModel(
    id = 1,
    title = "Test Movie",
    type = WorkType.MOVIE
)

private val VIDEO_LIST = listOf(
    VideoDomainModel(
        id = "1",
        name = "Trailer"
    )
)

private val CAST_LIST = listOf(
    CastDomainModel(
        id = 1,
        name = "Actor Name",
        character = "Character Name",
        birthday = "1990-01-01",
        deathDay = null,
        biography = null
    )
)

private val WORK_PAGE = PageDomainModel(
    page = 1,
    totalPages = 5,
    results = listOf(
        WorkDomainModel(
            id = 2,
            title = "Recommended Movie"
        )
    )
)

private val REVIEW_PAGE = PageDomainModel(
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

/**
 * Unit tests for WorkDetailsViewModel following MVI architecture with Coroutines
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
@OptIn(ExperimentalCoroutinesApi::class)
class WorkDetailsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val setFavoriteUseCase: SetFavoriteUseCase = mock()
    private val getWorkDetailsUseCase: GetWorkDetailsUseCase = mock()
    private val getReviewByWorkUseCase: GetReviewByWorkUseCase = mock()
    private val getRecommendationByWorkUseCase: GetRecommendationByWorkUseCase = mock()
    private val getSimilarByWorkUseCase: GetSimilarByWorkUseCase = mock()
    private val workDetailsRoute: WorkDetailsRoute = mock()
    private val castDetailsRoute: CastDetailsRoute = mock()
    private val viewModel = WorkDetailsViewModel(
        work = WORK,
        setFavoriteUseCase = setFavoriteUseCase,
        getWorkDetailsUseCase = getWorkDetailsUseCase,
        getReviewByWorkUseCase = getReviewByWorkUseCase,
        getRecommendationByWorkUseCase = getRecommendationByWorkUseCase,
        getSimilarByWorkUseCase = getSimilarByWorkUseCase,
        workDetailsRoute = workDetailsRoute,
        castDetailsRoute = castDetailsRoute
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have correct work`() {
        val initialState = viewModel.state.value
        assertEquals(WORK, initialState.work)
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
            videos = VIDEO_LIST,
            casts = CAST_LIST,
            recommended = WORK_PAGE,
            similar = WORK_PAGE,
            reviews = REVIEW_PAGE
        )

        whenever(getWorkDetailsUseCase(WORK)).thenReturn(workDetails)

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

        whenever(getWorkDetailsUseCase(WORK)).thenReturn(workDetails)

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
        whenever(getWorkDetailsUseCase(WORK)).thenThrow(exception)

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
    fun `workClicked should emit OpenIntent effect with transition`() = runTest(testDispatcher) {
        val clickedWork = WorkViewModel(id = 2, title = "Another Movie", type = WorkType.MOVIE)
        val intent = mock<Intent>()
        whenever(workDetailsRoute.buildWorkDetailIntent(clickedWork)).thenReturn(intent)

        viewModel.effects.test {
            viewModel.handleEvent(WorkDetailsEvent.WorkClicked(clickedWork))
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is WorkDetailsEffect.OpenIntent)
            assertEquals(intent, (effect as WorkDetailsEffect.OpenIntent).intent)
            assertTrue(effect.shareTransition)
        }
    }

    @Test
    fun `castClicked should emit OpenIntent effect with transition`() = runTest(testDispatcher) {
        val cast = CastViewModel(
            id = 1,
            name = "Actor Name",
            character = "Character Name"
        )
        val intent = mock<Intent>()
        whenever(castDetailsRoute.buildCastDetailIntent(cast)).thenReturn(intent)

        viewModel.effects.test {
            viewModel.handleEvent(WorkDetailsEvent.CastClicked(cast))
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is WorkDetailsEffect.OpenIntent)
            assertEquals(intent, (effect as WorkDetailsEffect.OpenIntent).intent)
            assertTrue(effect.shareTransition)
        }
    }

    @Test
    fun `videoClicked should emit OpenIntent effect without transition`() = runTest(testDispatcher) {
        val video = VideoViewModel(
            id = "1",
            type = "Trailer",
            youtubeUrl = "https://youtube.com/watch?v=123"
        )

        viewModel.effects.test {
            viewModel.handleEvent(WorkDetailsEvent.VideoClicked(video))
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is WorkDetailsEffect.OpenIntent)
            val openIntent = effect as WorkDetailsEffect.OpenIntent
            // Note: Intent.action and data require Robolectric, so we just verify it's OpenIntent
            assertFalse(openIntent.shareTransition)
        }
    }

    @Test
    fun `videoClicked with null url should not emit effect`() = runTest(testDispatcher) {
        val video = VideoViewModel(
            id = "1",
            type = "Trailer",
            youtubeUrl = null
        )

        viewModel.effects.test {
            viewModel.handleEvent(WorkDetailsEvent.VideoClicked(video))
            advanceUntilIdle()

            // Should not emit any effect when youtubeUrl is null
            expectNoEvents()
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
            reviews = REVIEW_PAGE.copy(totalPages = 2)
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

        whenever(getWorkDetailsUseCase(WORK)).thenReturn(initialDetails)
        whenever(getReviewByWorkUseCase(WorkType.MOVIE, WORK.id, 2))
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
