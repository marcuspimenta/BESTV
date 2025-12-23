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
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import com.pimenta.bestv.castdetail.domain.GetCastDetailsUseCase
import com.pimenta.bestv.castdetail.presentation.model.CastDetailsEffect
import com.pimenta.bestv.castdetail.presentation.model.CastDetailsEvent
import com.pimenta.bestv.castdetail.presentation.model.CastDetailsState
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private val CAST = CastViewModel(
    id = 1,
    name = "John Doe",
    character = "Hero",
    birthday = "1990-01-01",
    source = "tmdb",
    deathDay = "",
    biography = "An actor biography",
    thumbnailUrl = "https://example.com/photo.jpg"
)

private val CAST_DETAILS = CastDomainModel(
    id = 1,
    name = "John Doe",
    character = "Hero",
    birthday = "1990-01-01",
    deathDay = null,
    biography = "An actor biography",
    profilePath = "/photo.jpg"
)

private val MOVIE_LIST = listOf(
    WorkDomainModel(
        id = 1,
        title = "Movie 1",
        originalLanguage = "en",
        overview = "Movie 1 overview",
        source = "tmdb",
        backdropPath = "/backdrop1.jpg",
        posterPath = "/poster1.jpg",
        originalTitle = "Movie 1",
        releaseDate = "2023-01-01",
        type = WorkDomainModel.Type.MOVIE,
        voteAverage = 7.5f
    ),
    WorkDomainModel(
        id = 2,
        title = "Movie 2",
        originalLanguage = "en",
        overview = "Movie 2 overview",
        source = "tmdb",
        backdropPath = "/backdrop2.jpg",
        posterPath = "/poster2.jpg",
        originalTitle = "Movie 2",
        releaseDate = "2023-02-01",
        type = WorkDomainModel.Type.MOVIE,
        voteAverage = 8.0f
    )
)

private val TV_SHOW_LIST = listOf(
    WorkDomainModel(
        id = 3,
        title = "TV Show 1",
        originalLanguage = "en",
        overview = "TV Show 1 overview",
        source = "tmdb",
        backdropPath = "/backdrop3.jpg",
        posterPath = "/poster3.jpg",
        originalTitle = "TV Show 1",
        releaseDate = "2023-03-01",
        type = WorkDomainModel.Type.TV_SHOW,
        voteAverage = 8.5f
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
    fun `initial state should be Loading`() {
        val initialState = viewModel.state.value
        assertTrue(initialState is CastDetailsState.Loading)
    }

    @Test
    fun `loadData should update state with loaded data`() = runTest(testDispatcher) {
        val result = Triple(CAST_DETAILS, MOVIE_LIST, TV_SHOW_LIST)

        whenever(getCastDetailsUseCase(CAST.id)).thenReturn(result)

        viewModel.handleEvent(CastDetailsEvent.LoadData)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is CastDetailsState.Loaded)
        val loadedState = state as CastDetailsState.Loaded
        assertEquals("John Doe", loadedState.cast.name)
        assertEquals(2, loadedState.movies.size)
        assertEquals(1, loadedState.tvShows.size)
    }

    @Test
    fun `loadData should transition from Loading to Loaded state`() = runTest(testDispatcher) {
        val result = Triple(CAST_DETAILS, emptyList<WorkDomainModel>(), emptyList<WorkDomainModel>())

        whenever(getCastDetailsUseCase(CAST.id)).thenReturn(result)

        viewModel.state.test {
            val initialState = awaitItem()
            assertTrue(initialState is CastDetailsState.Loading)

            viewModel.handleEvent(CastDetailsEvent.LoadData)
            advanceUntilIdle()

            // Check state after loading completes
            val finalState = awaitItem()
            assertTrue(finalState is CastDetailsState.Loaded)
        }
    }

    @Test
    fun `loadData should handle error`() = runTest(testDispatcher) {
        val exception = RuntimeException("Network error")
        whenever(getCastDetailsUseCase(CAST.id)).thenThrow(exception)

        viewModel.handleEvent(CastDetailsEvent.LoadData)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is CastDetailsState.Error)
    }

    @Test
    fun `workClicked should emit OpenIntent effect with transition`() = runTest(testDispatcher) {
        val clickedWork = WorkViewModel(
            id = 2,
            originalLanguage = "en",
            overview = "A movie overview",
            source = "tmdb",
            backdropUrl = "https://example.com/backdrop.jpg",
            posterUrl = "https://example.com/poster.jpg",
            title = "Another Movie",
            originalTitle = "Another Movie",
            releaseDate = "2023-01-01",
            type = WorkType.MOVIE,
            voteAverage = 7.5f
        )
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
        assertTrue(state is CastDetailsState.Loaded)
        val loadedState = state as CastDetailsState.Loaded
        assertEquals(2, loadedState.movies.size)
        assertTrue(loadedState.tvShows.isEmpty())
    }

    @Test
    fun `loadData with only tv shows should update tv shows list`() = runTest(testDispatcher) {
        val result = Triple(CAST_DETAILS, null, TV_SHOW_LIST)

        whenever(getCastDetailsUseCase(CAST.id)).thenReturn(result)

        viewModel.handleEvent(CastDetailsEvent.LoadData)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is CastDetailsState.Loaded)
        val loadedState = state as CastDetailsState.Loaded
        assertTrue(loadedState.movies.isEmpty())
        assertEquals(1, loadedState.tvShows.size)
    }
}
