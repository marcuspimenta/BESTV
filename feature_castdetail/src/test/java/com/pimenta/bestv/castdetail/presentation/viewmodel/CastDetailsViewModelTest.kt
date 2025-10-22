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

package com.pimenta.bestv.castdetail.presentation.viewmodel

import android.content.Intent
import app.cash.turbine.test
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.pimenta.bestv.castdetail.domain.GetCastDetailsUseCase
import com.pimenta.bestv.castdetail.presentation.model.CastDetailsEffect
import com.pimenta.bestv.castdetail.presentation.model.CastDetailsEvent
import com.pimenta.bestv.model.domain.CastDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
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

private val CAST = CastViewModel(
    id = 1,
    name = "John Doe",
    character = "Hero"
)

private val CAST_DETAILS = CastDomainModel(
    id = 1,
    name = "John Doe",
    character = "Hero",
    birthday = "1990-01-01",
    deathDay = null,
    biography = "An actor biography"
)

private val MOVIE_LIST = listOf(
    WorkDomainModel(
        id = 1,
        title = "Movie 1"
    ),
    WorkDomainModel(
        id = 2,
        title = "Movie 2"
    )
)

private val TV_SHOW_LIST = listOf(
    WorkDomainModel(
        id = 3,
        title = "TV Show 1"
    )
)

/**
 * Unit tests for CastDetailsViewModel following MVI architecture with Coroutines
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
@OptIn(ExperimentalCoroutinesApi::class)
class CastDetailsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val getCastDetailsUseCase: GetCastDetailsUseCase = mock()
    private val workDetailsRoute: WorkDetailsRoute = mock()
    private val viewModel = CastDetailsViewModel(
        cast = CAST,
        getCastDetailsUseCase = getCastDetailsUseCase,
        workDetailsRoute = workDetailsRoute
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
    fun `initial state should have correct cast`() {
        val initialState = viewModel.state.value
        assertEquals(CAST, initialState.cast)
        assertFalse(initialState.isLoading)
        assertNull(initialState.castDetails)
        assertTrue(initialState.movies.isEmpty())
        assertTrue(initialState.tvShows.isEmpty())
    }

    @Test
    fun `loadData should update state with loaded data`() = runTest(testDispatcher) {
        val result = Triple(CAST_DETAILS, MOVIE_LIST, TV_SHOW_LIST)

        whenever(getCastDetailsUseCase(CAST.id)).thenReturn(result)

        viewModel.handleEvent(CastDetailsEvent.LoadData)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertNotNull(state.castDetails)
        assertEquals("John Doe", state.castDetails?.name)
        assertEquals(2, state.movies.size)
        assertEquals(1, state.tvShows.size)
    }

    @Test
    fun `loadData should show loading state`() = runTest(testDispatcher) {
        val result = Triple(CAST_DETAILS, emptyList<WorkDomainModel>(), emptyList<WorkDomainModel>())

        whenever(getCastDetailsUseCase(CAST.id)).thenReturn(result)

        viewModel.state.test {
            val initialState = awaitItem()
            assertFalse(initialState.isLoading)

            viewModel.handleEvent(CastDetailsEvent.LoadData)

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
        whenever(getCastDetailsUseCase(CAST.id)).thenThrow(exception)

        viewModel.effects.test {
            viewModel.handleEvent(CastDetailsEvent.LoadData)
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is CastDetailsEffect.ShowError)
        }

        val state = viewModel.state.value
        assertFalse(state.isLoading)
    }

    @Test
    fun `workClicked should emit OpenIntent effect with transition`() = runTest(testDispatcher) {
        val clickedWork = WorkViewModel(id = 2, title = "Another Movie", type = WorkType.MOVIE)
        val intent = mock<Intent>()
        whenever(workDetailsRoute.buildWorkDetailIntent(clickedWork)).thenReturn(intent)

        viewModel.effects.test {
            viewModel.handleEvent(CastDetailsEvent.WorkClicked(clickedWork))
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is CastDetailsEffect.OpenIntent)
            assertEquals(intent, (effect as CastDetailsEffect.OpenIntent).intent)
            assertTrue(effect.shareTransition)
        }
    }

    @Test
    fun `loadData with only movies should update movies list`() = runTest(testDispatcher) {
        val result = Triple(CAST_DETAILS, MOVIE_LIST, null)

        whenever(getCastDetailsUseCase(CAST.id)).thenReturn(result)

        viewModel.handleEvent(CastDetailsEvent.LoadData)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(2, state.movies.size)
        assertTrue(state.tvShows.isEmpty())
    }

    @Test
    fun `loadData with only tv shows should update tv shows list`() = runTest(testDispatcher) {
        val result = Triple(CAST_DETAILS, null, TV_SHOW_LIST)

        whenever(getCastDetailsUseCase(CAST.id)).thenReturn(result)

        viewModel.handleEvent(CastDetailsEvent.LoadData)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertTrue(state.movies.isEmpty())
        assertEquals(1, state.tvShows.size)
    }
}
