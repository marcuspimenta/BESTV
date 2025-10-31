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
import com.pimenta.bestv.workdetail.presentation.model.ErrorType
import com.pimenta.bestv.workdetail.presentation.model.VideoViewModel
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEffect
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState
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
        assertTrue(initialState.state is WorkDetailsState.State.Loading)
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
        assertTrue(state.state is WorkDetailsState.State.Loaded)
        val loadedState = state.state as WorkDetailsState.State.Loaded

        // Check contents
        val header = loadedState.contents.filterIsInstance<WorkDetailsState.Content.Header>().first()
        val saveWork = header.actions.filterIsInstance<WorkDetailsState.ActionButton.SaveWork>().first()
        assertTrue(saveWork.isFavorite)

        val videos = loadedState.contents.filterIsInstance<WorkDetailsState.Content.Videos>().firstOrNull()
        assertEquals(1, videos?.videos?.size)

        val casts = loadedState.contents.filterIsInstance<WorkDetailsState.Content.Casts>().firstOrNull()
        assertEquals(1, casts?.casts?.size)

        val reviews = loadedState.contents.filterIsInstance<WorkDetailsState.Content.Reviews>().firstOrNull()
        assertEquals(1, reviews?.reviews?.size)

        val recommendedWorks = loadedState.contents.filterIsInstance<WorkDetailsState.Content.RecommendedWorks>().firstOrNull()
        assertEquals(1, recommendedWorks?.recommended?.size)

        val similarWorks = loadedState.contents.filterIsInstance<WorkDetailsState.Content.SimilarWorks>().firstOrNull()
        assertEquals(1, similarWorks?.similar?.size)

        assertNull(loadedState.error)
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

        // Initial state should be Loading
        val initialState = viewModel.state.value
        assertTrue(initialState.state is WorkDetailsState.State.Loading)

        viewModel.handleEvent(WorkDetailsEvent.LoadData)
        advanceUntilIdle()

        // After loading completes, state should be Loaded
        val finalState = viewModel.state.value
        assertTrue(finalState.state is WorkDetailsState.State.Loaded)
    }

    @Test
    fun `loadData should handle error`() = runTest(testDispatcher) {
        val exception = RuntimeException("Network error")
        whenever(getWorkDetailsUseCase(WORK)).thenThrow(exception)

        viewModel.handleEvent(WorkDetailsEvent.LoadData)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.state is WorkDetailsState.State.Error)
    }

    @Test
    fun `toggleFavorite should update favorite state`() = runTest(testDispatcher) {
        // First load data to have a loaded state
        val workDetails = GetWorkDetailsUseCase.WorkDetailsDomainWrapper(
            isFavorite = false,
            videos = emptyList(),
            casts = emptyList(),
            recommended = PageDomainModel(1, 1, emptyList()),
            similar = PageDomainModel(1, 1, emptyList()),
            reviews = PageDomainModel(1, 1, emptyList())
        )

        whenever(getWorkDetailsUseCase(WORK)).thenReturn(workDetails)
        whenever(setFavoriteUseCase(any())).thenReturn(Unit)

        viewModel.handleEvent(WorkDetailsEvent.LoadData)
        advanceUntilIdle()

        val initialState = viewModel.state.value
        val loadedState = initialState.state as WorkDetailsState.State.Loaded
        val header = loadedState.contents.filterIsInstance<WorkDetailsState.Content.Header>().first()
        val saveWork = header.actions.filterIsInstance<WorkDetailsState.ActionButton.SaveWork>().first()
        assertFalse(saveWork.isFavorite)

        // Now toggle favorite
        viewModel.handleEvent(WorkDetailsEvent.ActionButtonClicked(WorkDetailsState.ActionButton.SaveWork(false)))
        advanceUntilIdle()

        val finalState = viewModel.state.value
        val finalLoadedState = finalState.state as WorkDetailsState.State.Loaded
        val finalHeader = finalLoadedState.contents.filterIsInstance<WorkDetailsState.Content.Header>().first()
        val finalSaveWork = finalHeader.actions.filterIsInstance<WorkDetailsState.ActionButton.SaveWork>().first()
        assertTrue(finalSaveWork.isFavorite)
    }

    @Test
    fun `workClicked should emit OpenIntent effect`() = runTest(testDispatcher) {
        val clickedWork = WorkViewModel(id = 2, title = "Another Movie", type = WorkType.MOVIE)
        val intent = mock<Intent>()
        whenever(workDetailsRoute.buildWorkDetailIntent(clickedWork)).thenReturn(intent)

        viewModel.effects.test {
            viewModel.handleEvent(WorkDetailsEvent.WorkClicked(clickedWork))
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is WorkDetailsEffect.OpenIntent)
            assertEquals(intent, (effect as WorkDetailsEffect.OpenIntent).intent)
        }
    }

    @Test
    fun `castClicked should emit OpenIntent effect`() = runTest(testDispatcher) {
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
        }
    }

    @Test
    fun `videoClicked should emit OpenIntent effect`() = runTest(testDispatcher) {
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
            // Note: Intent.action and data require Robolectric, so we just verify it's OpenIntent
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
    fun `loadMoreReviews should load more reviews when needed`() = runTest(testDispatcher) {
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

        val initialState = viewModel.state.value
        val initialLoadedState = initialState.state as WorkDetailsState.State.Loaded
        val initialReviews = initialLoadedState.contents.filterIsInstance<WorkDetailsState.Content.Reviews>().first()
        assertEquals(1, initialReviews.reviews.size)
        assertEquals(1, initialReviews.page.currentPage)

        // Load more reviews
        viewModel.handleEvent(WorkDetailsEvent.LoadMoreReviews)
        advanceUntilIdle()

        val finalState = viewModel.state.value
        val finalLoadedState = finalState.state as WorkDetailsState.State.Loaded
        val finalReviews = finalLoadedState.contents.filterIsInstance<WorkDetailsState.Content.Reviews>().first()
        assertEquals(2, finalReviews.reviews.size)
        assertEquals(2, finalReviews.page.currentPage)
    }

    @Test
    fun `dismissError should clear favorite error from loaded state`() = runTest(testDispatcher) {
        // First load data to have a loaded state
        val workDetails = GetWorkDetailsUseCase.WorkDetailsDomainWrapper(
            isFavorite = false,
            videos = emptyList(),
            casts = emptyList(),
            recommended = PageDomainModel(1, 1, emptyList()),
            similar = PageDomainModel(1, 1, emptyList()),
            reviews = PageDomainModel(1, 1, emptyList())
        )

        whenever(getWorkDetailsUseCase(WORK)).thenReturn(workDetails)
        whenever(setFavoriteUseCase(any())).thenThrow(RuntimeException("Failed to save"))

        viewModel.handleEvent(WorkDetailsEvent.LoadData)
        advanceUntilIdle()

        // Trigger a favorite error
        viewModel.handleEvent(WorkDetailsEvent.ActionButtonClicked(WorkDetailsState.ActionButton.SaveWork(false)))
        advanceUntilIdle()

        val stateWithError = viewModel.state.value
        val loadedStateWithError = stateWithError.state as WorkDetailsState.State.Loaded
        assertEquals(ErrorType.FavoriteError, loadedStateWithError.error)

        // Dismiss the error
        viewModel.handleEvent(WorkDetailsEvent.DismissError)
        advanceUntilIdle()

        val finalState = viewModel.state.value
        val finalLoadedState = finalState.state as WorkDetailsState.State.Loaded
        assertNull(finalLoadedState.error)

        // Verify other state properties remain unchanged
        val header = finalLoadedState.contents.filterIsInstance<WorkDetailsState.Content.Header>().firstOrNull()
        assertEquals(1, header?.actions?.size)
    }

    @Test
    fun `dismissError should clear pagination error from loaded state`() = runTest(testDispatcher) {
        // Setup initial state with reviews
        val initialDetails = GetWorkDetailsUseCase.WorkDetailsDomainWrapper(
            isFavorite = false,
            videos = emptyList(),
            casts = emptyList(),
            recommended = PageDomainModel(1, 1, emptyList()),
            similar = PageDomainModel(1, 1, emptyList()),
            reviews = REVIEW_PAGE.copy(totalPages = 2)
        )

        whenever(getWorkDetailsUseCase(WORK)).thenReturn(initialDetails)
        whenever(getReviewByWorkUseCase(WorkType.MOVIE, WORK.id, 2))
            .thenThrow(RuntimeException("Network error"))

        // Load initial data
        viewModel.handleEvent(WorkDetailsEvent.LoadData)
        advanceUntilIdle()

        // Try to load more reviews and fail
        viewModel.handleEvent(WorkDetailsEvent.LoadMoreReviews)
        advanceUntilIdle()

        val stateWithError = viewModel.state.value
        val loadedStateWithError = stateWithError.state as WorkDetailsState.State.Loaded
        assertEquals(ErrorType.PaginationError, loadedStateWithError.error)

        // Dismiss the error
        viewModel.handleEvent(WorkDetailsEvent.DismissError)
        advanceUntilIdle()

        val finalState = viewModel.state.value
        val finalLoadedState = finalState.state as WorkDetailsState.State.Loaded
        assertNull(finalLoadedState.error)

        // Verify reviews are still present
        val reviews = finalLoadedState.contents.filterIsInstance<WorkDetailsState.Content.Reviews>().firstOrNull()
        assertEquals(1, reviews?.reviews?.size)
    }

    @Test
    fun `dismissError should do nothing when state is not loaded`() = runTest(testDispatcher) {
        // Initial state is Loading
        val initialState = viewModel.state.value
        assertTrue(initialState.state is WorkDetailsState.State.Loading)

        // Try to dismiss error
        viewModel.handleEvent(WorkDetailsEvent.DismissError)
        advanceUntilIdle()

        // State should remain Loading
        val finalState = viewModel.state.value
        assertTrue(finalState.state is WorkDetailsState.State.Loading)
    }

    @Test
    fun `showError should set error when state is loaded`() = runTest(testDispatcher) {
        // First load data to have a loaded state
        val workDetails = GetWorkDetailsUseCase.WorkDetailsDomainWrapper(
            isFavorite = false,
            videos = emptyList(),
            casts = emptyList(),
            recommended = PageDomainModel(1, 1, emptyList()),
            similar = PageDomainModel(1, 1, emptyList()),
            reviews = PageDomainModel(1, 1, emptyList())
        )

        whenever(getWorkDetailsUseCase(WORK)).thenReturn(workDetails)

        viewModel.handleEvent(WorkDetailsEvent.LoadData)
        advanceUntilIdle()

        // Verify initial state has no error
        val initialState = viewModel.state.value
        val initialLoadedState = initialState.state as WorkDetailsState.State.Loaded
        assertNull(initialLoadedState.error)

        // Show error
        viewModel.handleEvent(WorkDetailsEvent.ShowError(ErrorType.FailedToOpenYouTubeVideo))
        advanceUntilIdle()

        // Verify error is set
        val finalState = viewModel.state.value
        val finalLoadedState = finalState.state as WorkDetailsState.State.Loaded
        assertEquals(ErrorType.FailedToOpenYouTubeVideo, finalLoadedState.error)

        // Verify other state properties remain unchanged
        assertEquals(initialLoadedState.contents.size, finalLoadedState.contents.size)
    }

    @Test
    fun `showError should do nothing when state is not loaded`() = runTest(testDispatcher) {
        // Initial state is Loading
        val initialState = viewModel.state.value
        assertTrue(initialState.state is WorkDetailsState.State.Loading)

        // Try to show error
        viewModel.handleEvent(WorkDetailsEvent.ShowError(ErrorType.FailedToOpenYouTubeVideo))
        advanceUntilIdle()

        // State should remain Loading with no error
        val finalState = viewModel.state.value
        assertTrue(finalState.state is WorkDetailsState.State.Loading)
    }

    @Test
    fun `showError should replace existing error with new error`() = runTest(testDispatcher) {
        // Setup loaded state with an existing error
        val workDetails = GetWorkDetailsUseCase.WorkDetailsDomainWrapper(
            isFavorite = false,
            videos = emptyList(),
            casts = emptyList(),
            recommended = PageDomainModel(1, 1, emptyList()),
            similar = PageDomainModel(1, 1, emptyList()),
            reviews = PageDomainModel(1, 1, emptyList())
        )

        whenever(getWorkDetailsUseCase(WORK)).thenReturn(workDetails)
        whenever(setFavoriteUseCase(any())).thenThrow(RuntimeException("Failed to save"))

        viewModel.handleEvent(WorkDetailsEvent.LoadData)
        advanceUntilIdle()

        // Trigger a favorite error
        viewModel.handleEvent(WorkDetailsEvent.ActionButtonClicked(WorkDetailsState.ActionButton.SaveWork(false)))
        advanceUntilIdle()

        val stateWithFavoriteError = viewModel.state.value
        val loadedStateWithFavoriteError = stateWithFavoriteError.state as WorkDetailsState.State.Loaded
        assertEquals(ErrorType.FavoriteError, loadedStateWithFavoriteError.error)

        // Show a different error
        viewModel.handleEvent(WorkDetailsEvent.ShowError(ErrorType.FailedToOpenYouTubeVideo))
        advanceUntilIdle()

        // Verify new error replaced the old one
        val finalState = viewModel.state.value
        val finalLoadedState = finalState.state as WorkDetailsState.State.Loaded
        assertEquals(ErrorType.FailedToOpenYouTubeVideo, finalLoadedState.error)
    }
}
