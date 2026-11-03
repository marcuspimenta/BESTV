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

package com.pimenta.bestv.workbrowse.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.model.domain.ContentSource
import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.presentation.model.PaginationState
import com.pimenta.bestv.presentation.platform.Resource
import com.pimenta.bestv.presentation.platform.TvDeviceCapabilities
import com.pimenta.bestv.route.search.SearchRoute
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
import com.pimenta.bestv.route.SearchDestination
import com.pimenta.bestv.route.WorkDetailsDestination
import com.pimenta.bestv.workbrowse.domain.GetSectionDetailsUseCase
import com.pimenta.bestv.workbrowse.domain.GetWorkByGenreUseCase
import com.pimenta.bestv.workbrowse.domain.LoadWorkByTypeUseCase
import com.pimenta.bestv.workbrowse.domain.model.BrowseCategoryKey
import com.pimenta.bestv.workbrowse.domain.model.BrowseSectionDetails
import com.pimenta.bestv.workbrowse.domain.model.BrowseSectionDomainModel
import com.pimenta.bestv.workbrowse.domain.model.GenreDomainModel
import com.pimenta.bestv.workbrowse.presentation.model.ContentSection
import com.pimenta.bestv.workbrowse.presentation.model.Source
import com.pimenta.bestv.workbrowse.presentation.model.TopWorkTypeViewModel
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseEffect
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseEvent
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
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
import org.mockito.kotlin.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

private val WORK =
    WorkViewModel(
        id = 1,
        title = "Test Movie",
        originalTitle = "Test Movie",
        originalLanguage = "en",
        overview = "A test movie",
        source = "tmdb",
        backdropUrl = "https://image.tmdb.org/t/p/original/backdrop.jpg",
        posterUrl = "https://image.tmdb.org/t/p/original/poster.jpg",
        releaseDate = "Jan 01, 2023",
        type = WorkType.MOVIE,
        voteAverage = 8.0f,
    )

private fun domainWork(
    id: Int,
    type: WorkDomainModel.Type,
    isFavorite: Boolean = false,
) =
    WorkDomainModel(
        id = id,
        title = "Test Movie",
        originalTitle = "Test Movie",
        originalLanguage = "en",
        overview = "A test movie",
        source = ContentSource.TMDB,
        backdropPath = "/backdrop.jpg",
        posterPath = "/poster.jpg",
        releaseDate = "2023-01-01",
        type = type,
        voteAverage = 8.0f,
        isFavorite = isFavorite,
    )

private fun domainSection(category: BrowseCategoryKey, work: WorkDomainModel, totalPages: Int) =
    BrowseSectionDomainModel.TopContent(
        category = category,
        page = PageDomainModel(page = 1, totalPages = totalPages, results = listOf(work)),
    )

private val SECTION_DETAILS =
    BrowseSectionDetails(
        movieSections = listOf(
            domainSection(BrowseCategoryKey.NOW_PLAYING_MOVIES, domainWork(1, WorkDomainModel.Type.MOVIE), 5),
        ),
        tvShowSections = listOf(
            domainSection(BrowseCategoryKey.AIRING_TODAY_TV_SHOWS, domainWork(2, WorkDomainModel.Type.TV_SHOW), 3),
        ),
        favoriteSections = listOf(
            domainSection(BrowseCategoryKey.FAVORITES_MOVIES, domainWork(3, WorkDomainModel.Type.MOVIE, true), 1),
        ),
    )

/**
 * Unit tests for WorkBrowseViewModel following MVI architecture with Coroutines
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WorkBrowseViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private val getSectionDetailsUseCase: GetSectionDetailsUseCase = mock()
    private val getWorkByGenreUseCase: GetWorkByGenreUseCase = mock()
    private val loadWorkByTypeUseCase: LoadWorkByTypeUseCase = mock()
    private val workDetailsRoute: WorkDetailsRoute = mock()
    private val searchRoute: SearchRoute = mock()
    private val resource: Resource = mock()

    private lateinit var viewModel: WorkBrowseViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        whenever(resource.getStringResource(any())).thenReturn("tmdb")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): WorkBrowseViewModel =
        WorkBrowseViewModel(
            getSectionDetailsUseCase = getSectionDetailsUseCase,
            getWorkByGenreUseCase = getWorkByGenreUseCase,
            loadWorkByTypeUseCase = loadWorkByTypeUseCase,
            workDetailsRoute = workDetailsRoute,
            searchRoute = searchRoute,
            resource = resource,
            deviceCapabilities = TvDeviceCapabilities,
        )

    /**
     * Helper to transition ViewModel to Loaded state
     */
    private suspend fun transitionToLoadedState() {
        viewModel.handleEvent(WorkBrowseEvent.SplashAnimationFinished)
        // Let the coroutines complete
    }

    @Test
    fun `initial state should be Loading`() =
        runTest(testDispatcher) {
            whenever(getSectionDetailsUseCase.getAllSections()).thenReturn(SECTION_DETAILS)

            viewModel = createViewModel()

            val initialState = viewModel.state.value
            assertTrue(initialState.state is WorkBrowseState.State.Loading)
        }

    @Test
    fun `loadData should update state to Loaded after splash animation finishes`() =
        runTest(testDispatcher) {
            whenever(getSectionDetailsUseCase.getAllSections()).thenReturn(SECTION_DETAILS)

            viewModel = createViewModel()

            // First, let the init coroutine start and reach the wait point
            advanceUntilIdle()

            // Signal splash animation finished
            transitionToLoadedState()
            advanceUntilIdle()

            val state = viewModel.state.value
            assertTrue("State should be Loaded but was ${state.state}", state.state is WorkBrowseState.State.Loaded)

            val loadedState = state.state as WorkBrowseState.State.Loaded
            assertEquals(1, loadedState.selectedSectionIndex)
            assertTrue(loadedState.sections.any { it is WorkBrowseState.Section.Search })
            assertTrue(loadedState.sections.any { it is WorkBrowseState.Section.Movies })
            assertTrue(loadedState.sections.any { it is WorkBrowseState.Section.TvShows })
            assertTrue(loadedState.sections.any { it is WorkBrowseState.Section.Favorites })

            val movieSection =
                loadedState.sections
                    .filterIsInstance<WorkBrowseState.Section.Movies>()
                    .single()
                    .content
                    .single() as ContentSection.TopContent
            assertEquals(TopWorkTypeViewModel.NOW_PLAYING_MOVIES, movieSection.type)
            assertEquals(1, movieSection.works.single().id)
            assertEquals(PaginationState(currentPage = 1, totalPages = 5), movieSection.page)
        }

    @Test
    fun `loadMore should request the next page once and append its works`() =
        runTest(testDispatcher) {
            whenever(getSectionDetailsUseCase.getAllSections()).thenReturn(SECTION_DETAILS)
            whenever(loadWorkByTypeUseCase(2, BrowseCategoryKey.NOW_PLAYING_MOVIES)).thenReturn(
                PageDomainModel(
                    page = 2,
                    totalPages = 5,
                    results = listOf(
                        domainWork(1, WorkDomainModel.Type.MOVIE),
                        domainWork(4, WorkDomainModel.Type.MOVIE),
                    ),
                ),
            )

            viewModel = createViewModel()
            advanceUntilIdle()
            transitionToLoadedState()
            advanceUntilIdle()

            val section =
                (viewModel.state.value.state as WorkBrowseState.State.Loaded)
                    .sections
                    .filterIsInstance<WorkBrowseState.Section.Movies>()
                    .single()
                    .content
                    .single()
            viewModel.handleEvent(WorkBrowseEvent.LoadMore(section))
            viewModel.handleEvent(WorkBrowseEvent.LoadMore(section))
            advanceUntilIdle()

            val loadedState = viewModel.state.value.state as WorkBrowseState.State.Loaded
            val updatedSection =
                loadedState.sections
                    .filterIsInstance<WorkBrowseState.Section.Movies>()
                    .single()
                    .content
                    .single()
            assertEquals(listOf(1, 4), updatedSection.works.map { it.id })
            assertEquals(PaginationState(currentPage = 2, totalPages = 5), updatedSection.page)
            verify(loadWorkByTypeUseCase, times(1))(2, BrowseCategoryKey.NOW_PLAYING_MOVIES)
        }

    @Test
    fun `loadMore should request the next genre page`() =
        runTest(testDispatcher) {
            val movieGenre = GenreDomainModel(id = 28, name = "Action", source = GenreDomainModel.Source.MOVIE)
            val sectionDetails = SECTION_DETAILS.copy(
                movieSections = SECTION_DETAILS.movieSections +
                    BrowseSectionDomainModel.GenreContent(
                        genre = movieGenre,
                        page = PageDomainModel(
                            page = 1,
                            totalPages = 2,
                            results = listOf(domainWork(5, WorkDomainModel.Type.MOVIE)),
                        ),
                    ),
            )
            whenever(getSectionDetailsUseCase.getAllSections()).thenReturn(sectionDetails)
            whenever(getWorkByGenreUseCase(28, GenreDomainModel.Source.MOVIE, 2)).thenReturn(
                PageDomainModel(
                    page = 2,
                    totalPages = 2,
                    results = listOf(domainWork(6, WorkDomainModel.Type.MOVIE)),
                ),
            )

            viewModel = createViewModel()
            advanceUntilIdle()
            transitionToLoadedState()
            advanceUntilIdle()

            val genreSection =
                (viewModel.state.value.state as WorkBrowseState.State.Loaded)
                    .sections
                    .filterIsInstance<WorkBrowseState.Section.Movies>()
                    .single()
                    .content
                    .filterIsInstance<ContentSection.Genre>()
                    .single()
            assertEquals(Source.MOVIE, genreSection.genreViewModel.source)
            viewModel.handleEvent(WorkBrowseEvent.LoadMore(genreSection))
            advanceUntilIdle()

            val loadedState = viewModel.state.value.state as WorkBrowseState.State.Loaded
            val updatedGenreSection =
                loadedState.sections
                    .filterIsInstance<WorkBrowseState.Section.Movies>()
                    .single()
                    .content
                    .filterIsInstance<ContentSection.Genre>()
                    .single()
            assertEquals(listOf(5, 6), updatedGenreSection.works.map { it.id })
            assertEquals(PaginationState(currentPage = 2, totalPages = 2), updatedGenreSection.page)
            verify(getWorkByGenreUseCase)(28, GenreDomainModel.Source.MOVIE, 2)
        }

    @Test
    fun `loadData should update state to Error when loading is cancelled`() =
        runTest(testDispatcher) {
            whenever(getSectionDetailsUseCase.getAllSections()).thenThrow(CancellationException())

            viewModel = createViewModel()
            advanceUntilIdle()

            assertTrue(viewModel.state.value.state is WorkBrowseState.State.Error)
        }

    @Test
    fun `loadData should update state to Error when exception occurs`() =
        runTest(testDispatcher) {
            whenever(getSectionDetailsUseCase.getAllSections()).thenThrow(RuntimeException("Network error"))

            viewModel = createViewModel()
            advanceUntilIdle()

            // The error should happen before waiting for splash animation
            val state = viewModel.state.value
            assertTrue("State should be Error but was ${state.state}", state.state is WorkBrowseState.State.Error)
        }

    @Test
    fun `backClicked should emit CloseScreen effect`() =
        runTest(testDispatcher) {
            whenever(getSectionDetailsUseCase.getAllSections()).thenReturn(SECTION_DETAILS)

            viewModel = createViewModel()

            val collectedEffects = mutableListOf<WorkBrowseEffect>()
            val job = launch {
                viewModel.effects.collect { collectedEffects.add(it) }
            }

            viewModel.handleEvent(WorkBrowseEvent.BackClicked)
            advanceUntilIdle()

            assertTrue(collectedEffects.any { it is WorkBrowseEffect.CloseScreen })
            job.cancel()
        }

    @Test
    fun `sectionClicked with index 0 should emit Navigate effect to search`() =
        runTest(testDispatcher) {
            whenever(getSectionDetailsUseCase.getAllSections()).thenReturn(SECTION_DETAILS)
            val searchDestination = SearchDestination.create()
            whenever(searchRoute.buildSearchDestination()).thenReturn(searchDestination)

            viewModel = createViewModel()
            advanceUntilIdle()
            transitionToLoadedState()
            advanceUntilIdle()

            // Ensure state is Loaded before testing
            assertTrue("State should be Loaded", viewModel.state.value.state is WorkBrowseState.State.Loaded)

            val collectedEffects = mutableListOf<WorkBrowseEffect>()
            val job = launch {
                viewModel.effects.collect { collectedEffects.add(it) }
            }

            viewModel.handleEvent(WorkBrowseEvent.SectionClicked(0))
            advanceUntilIdle()

            val navigateEffect = collectedEffects.filterIsInstance<WorkBrowseEffect.Navigate>().firstOrNull()
            assertNotNull("Navigate effect should be emitted", navigateEffect)
            assertEquals(searchDestination, navigateEffect?.destination)
            job.cancel()
        }

    @Test
    fun `sectionClicked with non-zero index should update selectedSectionIndex`() =
        runTest(testDispatcher) {
            whenever(getSectionDetailsUseCase.getAllSections()).thenReturn(SECTION_DETAILS)

            viewModel = createViewModel()
            advanceUntilIdle()
            transitionToLoadedState()
            advanceUntilIdle()

            // Ensure state is Loaded
            assertTrue("State should be Loaded", viewModel.state.value.state is WorkBrowseState.State.Loaded)

            viewModel.handleEvent(WorkBrowseEvent.SectionClicked(2))
            advanceUntilIdle()

            val state = viewModel.state.value
            val loadedState = state.state as WorkBrowseState.State.Loaded
            assertEquals(2, loadedState.selectedSectionIndex)
        }

    @Test
    fun `workSelected should update workSelected in state`() =
        runTest(testDispatcher) {
            whenever(getSectionDetailsUseCase.getAllSections()).thenReturn(SECTION_DETAILS)

            viewModel = createViewModel()
            advanceUntilIdle()
            transitionToLoadedState()
            advanceUntilIdle()

            // Ensure state is Loaded
            assertTrue("State should be Loaded", viewModel.state.value.state is WorkBrowseState.State.Loaded)

            viewModel.handleEvent(WorkBrowseEvent.WorkSelected(WORK))
            advanceUntilIdle()

            val state = viewModel.state.value
            val loadedState = state.state as WorkBrowseState.State.Loaded
            assertEquals(WORK, loadedState.workSelected)
        }

    @Test
    fun `workClicked should emit Navigate effect to work details`() =
        runTest(testDispatcher) {
            whenever(getSectionDetailsUseCase.getAllSections()).thenReturn(SECTION_DETAILS)
            val workDetailsDestination = WorkDetailsDestination.create(WORK)
            whenever(workDetailsRoute.buildWorkDetailsDestination(WORK)).thenReturn(workDetailsDestination)

            viewModel = createViewModel()

            val collectedEffects = mutableListOf<WorkBrowseEffect>()
            val job = launch {
                viewModel.effects.collect { collectedEffects.add(it) }
            }

            viewModel.handleEvent(WorkBrowseEvent.WorkClicked(WORK))
            advanceUntilIdle()

            val navigateEffect = collectedEffects.filterIsInstance<WorkBrowseEffect.Navigate>().firstOrNull()
            assertNotNull("Navigate effect should be emitted", navigateEffect)
            assertEquals(workDetailsDestination, navigateEffect?.destination)
            job.cancel()

            verify(workDetailsRoute).buildWorkDetailsDestination(WORK)
        }

    @Test
    fun `screenResumed should update favorites section`() =
        runTest(testDispatcher) {
            whenever(getSectionDetailsUseCase.getAllSections()).thenReturn(SECTION_DETAILS)

            viewModel = createViewModel()
            advanceUntilIdle()
            transitionToLoadedState()
            advanceUntilIdle()

            // Ensure state is Loaded
            assertTrue("State should be Loaded", viewModel.state.value.state is WorkBrowseState.State.Loaded)

            val newFavorites = listOf(
                domainSection(
                    BrowseCategoryKey.FAVORITES_MOVIES,
                    domainWork(10, WorkDomainModel.Type.MOVIE, true),
                    1,
                ),
            )
            whenever(getSectionDetailsUseCase.getFavoriteSections()).thenReturn(newFavorites)

            viewModel.handleEvent(WorkBrowseEvent.ScreenResumed)
            advanceUntilIdle()

            val state = viewModel.state.value
            val loadedState = state.state as WorkBrowseState.State.Loaded
            val favoritesSection = loadedState.sections.filterIsInstance<WorkBrowseState.Section.Favorites>().firstOrNull()
            val favoriteContent = favoritesSection?.content?.single() as ContentSection.TopContent
            assertEquals(TopWorkTypeViewModel.FAVORITES_MOVIES, favoriteContent.type)
            assertEquals(10, favoriteContent.works.single().id)
            assertTrue(favoriteContent.works.single().isFavorite)
            assertEquals(PaginationState(currentPage = 1, totalPages = 1), favoriteContent.page)
        }

    @Test
    fun `screenResumed should handle cancellation and preserve loaded sections`() =
        runTest(testDispatcher) {
            whenever(getSectionDetailsUseCase.getAllSections()).thenReturn(SECTION_DETAILS)
            viewModel = createViewModel()
            advanceUntilIdle()
            transitionToLoadedState()
            advanceUntilIdle()
            val loadedState = viewModel.state.value
            whenever(getSectionDetailsUseCase.getFavoriteSections()).thenThrow(CancellationException())

            viewModel.handleEvent(WorkBrowseEvent.ScreenResumed)
            val refreshJob = viewModel.viewModelScope.coroutineContext.job.children
                .single()
            advanceUntilIdle()

            assertTrue(refreshJob.isCompleted)
            assertFalse(refreshJob.isCancelled)
            assertEquals(loadedState, viewModel.state.value)
        }

    @Test
    fun `screenResumed should remove favorites section when no favorites exist`() =
        runTest(testDispatcher) {
            whenever(getSectionDetailsUseCase.getAllSections()).thenReturn(SECTION_DETAILS)

            viewModel = createViewModel()
            advanceUntilIdle()
            transitionToLoadedState()
            advanceUntilIdle()

            // Ensure state is Loaded
            assertTrue("State should be Loaded", viewModel.state.value.state is WorkBrowseState.State.Loaded)

            // Return empty favorites
            whenever(getSectionDetailsUseCase.getFavoriteSections()).thenReturn(emptyList())

            viewModel.handleEvent(WorkBrowseEvent.ScreenResumed)
            advanceUntilIdle()

            val state = viewModel.state.value
            val loadedState = state.state as WorkBrowseState.State.Loaded
            val favoritesSection = loadedState.sections.filterIsInstance<WorkBrowseState.Section.Favorites>().firstOrNull()
            assertNull("Favorites section should be removed", favoritesSection)
        }

    @Test
    fun `retryLoad should reload data`() =
        runTest(testDispatcher) {
            whenever(getSectionDetailsUseCase.getAllSections())
                .thenThrow(RuntimeException("First call fails"))
                .thenReturn(SECTION_DETAILS)

            viewModel = createViewModel()
            advanceUntilIdle()

            // First load should fail
            assertTrue("State should be Error", viewModel.state.value.state is WorkBrowseState.State.Error)

            // Retry
            viewModel.handleEvent(WorkBrowseEvent.RetryLoad)
            advanceUntilIdle()

            // Now signal splash animation finished
            transitionToLoadedState()
            advanceUntilIdle()

            assertTrue("State should be Loaded after retry", viewModel.state.value.state is WorkBrowseState.State.Loaded)
        }

    @Test
    fun `splashAnimationFinished should transition to Loaded state when data is ready`() =
        runTest(testDispatcher) {
            whenever(getSectionDetailsUseCase.getAllSections()).thenReturn(SECTION_DETAILS)

            viewModel = createViewModel()

            // Before splash animation finishes - state should still be Loading
            val initialState = viewModel.state.value.state
            assertTrue("Initial state should be Loading", initialState is WorkBrowseState.State.Loading)
            assertEquals(false, (initialState as WorkBrowseState.State.Loading).isSplashAnimationFinished)

            // Let the data loading start
            advanceUntilIdle()

            // Now signal splash animation finished
            viewModel.handleEvent(WorkBrowseEvent.SplashAnimationFinished)

            // After splash animation signal and data loads, state should transition to Loaded
            advanceUntilIdle()

            // After splash animation finishes and data loads, state should transition to Loaded
            assertTrue("State should be Loaded", viewModel.state.value.state is WorkBrowseState.State.Loaded)
        }
}
