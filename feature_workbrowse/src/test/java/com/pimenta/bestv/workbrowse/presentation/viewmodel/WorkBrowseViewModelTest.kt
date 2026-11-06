package com.pimenta.bestv.workbrowse.presentation.viewmodel

import com.pimenta.bestv.model.domain.ContentSource
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.platform.Resource
import com.pimenta.bestv.presentation.platform.TvDeviceCapabilities
import com.pimenta.bestv.route.SearchDestination
import com.pimenta.bestv.route.WorkDetailsDestination
import com.pimenta.bestv.route.search.SearchRoute
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
import com.pimenta.bestv.workbrowse.domain.ObserveFavoritesUseCase
import com.pimenta.bestv.workbrowse.domain.GetBrowseDataUseCase
import com.pimenta.bestv.workbrowse.domain.model.BrowseCategoryKey
import com.pimenta.bestv.workbrowse.domain.model.BrowseSectionDetails
import com.pimenta.bestv.workbrowse.domain.model.BrowseSectionDomainModel
import com.pimenta.bestv.workbrowse.presentation.model.ContentSection
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseEffect
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseEvent
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState
import com.pimenta.bestv.workbrowse.presentation.model.TopWorkTypeViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.mock
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
        title = "Test Work $id",
        originalTitle = "Test Work $id",
        originalLanguage = "en",
        overview = "A test work",
        source = ContentSource.TMDB,
        backdropPath = "/backdrop.jpg",
        posterPath = "/poster.jpg",
        releaseDate = "2023-01-01",
        type = type,
        voteAverage = 8.0f,
        isFavorite = isFavorite,
    )

private fun domainSection(category: BrowseCategoryKey, work: WorkDomainModel) =
    BrowseSectionDomainModel.TopContent(category = category, works = listOf(work))

private val BROWSE_SECTIONS =
    BrowseSectionDetails(
        movieSections = listOf(
            domainSection(BrowseCategoryKey.NOW_PLAYING_MOVIES, domainWork(1, WorkDomainModel.Type.MOVIE)),
        ),
        tvShowSections = listOf(
            domainSection(BrowseCategoryKey.AIRING_TODAY_TV_SHOWS, domainWork(2, WorkDomainModel.Type.TV_SHOW)),
        ),
    )

private typealias WorkBrowseViewModelStateLoaded = WorkBrowseState.State.Loaded
private typealias WorkBrowseViewModelStateError = WorkBrowseState.State.Error
private typealias WorkBrowseViewModelStateLoading = WorkBrowseState.State.Loading
private typealias WorkBrowseViewModelSectionMovies = WorkBrowseState.Section.Movies
private typealias WorkBrowseViewModelSectionFavorites = WorkBrowseState.Section.Favorites

@OptIn(ExperimentalCoroutinesApi::class)
class WorkBrowseViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val getBrowseDataUseCase: GetBrowseDataUseCase = mock()
    private val observeFavoritesUseCase: ObserveFavoritesUseCase = mock()
    private val observedFavorites = MutableStateFlow<List<WorkDomainModel>>(emptyList())
    private val workDetailsRoute: WorkDetailsRoute = mock()
    private val searchRoute: SearchRoute = mock()
    private val resource: Resource = mock()
    private lateinit var viewModel: WorkBrowseViewModel

    @Before
    fun setUp() = runTest(testDispatcher) {
        Dispatchers.setMain(testDispatcher)
        whenever(getBrowseDataUseCase()).thenReturn(BROWSE_SECTIONS)
        whenever(observeFavoritesUseCase()).thenReturn(observedFavorites)
        whenever(resource.getStringResource(any())).thenReturn("tmdb")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() =
        WorkBrowseViewModel(
            getBrowseDataUseCase = getBrowseDataUseCase,
            observeFavoritesUseCase = observeFavoritesUseCase,
            workDetailsRoute = workDetailsRoute,
            searchRoute = searchRoute,
            resource = resource,
            deviceCapabilities = TvDeviceCapabilities,
        )

    private suspend fun TestScope.transitionToLoadedState() {
        viewModel.handleEvent(WorkBrowseEvent.SplashAnimationFinished)
        advanceUntilIdle()
    }

    @Test
    fun `initial state is Loading`() = runTest(testDispatcher) {
        viewModel = createViewModel()

        assertTrue(viewModel.state.value.state is WorkBrowseState.State.Loading)
    }

    @Test
    fun `network sections display when loaded and favorites come from Room`() = runTest(testDispatcher) {
        observedFavorites.value = listOf(domainWork(1, WorkDomainModel.Type.MOVIE, true))
        viewModel = createViewModel()
        advanceUntilIdle()

        val loaded = viewModel.state.value.state as WorkBrowseState.State.Loaded
        assertEquals(1, loaded.selectedSectionIndex)
        assertTrue(loaded.sections.any { it is WorkBrowseState.Section.Search })
        val movieSection = loaded.sections
            .filterIsInstance<WorkBrowseState.Section.Movies>()
            .single()
            .content
            .single()
            as ContentSection.TopContent
        assertEquals(TopWorkTypeViewModel.NOW_PLAYING_MOVIES, movieSection.type)
        assertTrue(movieSection.works.single().isFavorite)
        val favorites = loaded.sections.filterIsInstance<WorkBrowseState.Section.Favorites>().single()
        assertEquals(
            listOf(1),
            favorites.content
                .single()
                .works
                .map { it.id }
        )
    }

    @Test
    fun `empty successful response displays Search without waiting for splash completion`() = runTest(testDispatcher) {
        whenever(getBrowseDataUseCase()).thenReturn(BrowseSectionDetails(emptyList(), emptyList()))
        viewModel = createViewModel()
        advanceUntilIdle()

        val loaded = viewModel.state.value.state as WorkBrowseViewModelStateLoaded
        assertEquals(listOf(WorkBrowseState.Section.Search), loaded.sections)
    }

    @Test
    fun `browse content loads even when favorites flow never emits`() = runTest(testDispatcher) {
        whenever(observeFavoritesUseCase()).thenReturn(emptyFlow())
        viewModel = createViewModel()

        advanceUntilIdle()

        assertTrue(viewModel.state.value.state is WorkBrowseViewModelStateLoaded)
    }

    @Test
    fun `browse content loads when favorites observation fails`() = runTest(testDispatcher) {
        whenever(observeFavoritesUseCase()).thenReturn(
            flow { throw IllegalStateException("Room favorites unavailable") },
        )
        viewModel = createViewModel()

        advanceUntilIdle()

        assertTrue(viewModel.state.value.state is WorkBrowseViewModelStateLoaded)
    }

    @Test
    fun `empty failed response displays Error`() = runTest(testDispatcher) {
        whenever(getBrowseDataUseCase()).thenThrow(RuntimeException("Network unavailable"))
        viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.state is WorkBrowseState.State.Error)
    }

    @Test
    fun `partial network response displays its successful sections`() = runTest(testDispatcher) {
        whenever(getBrowseDataUseCase()).thenReturn(BROWSE_SECTIONS.copy(tvShowSections = emptyList()))
        viewModel = createViewModel()
        advanceUntilIdle()
        transitionToLoadedState()

        val loaded = viewModel.state.value.state as WorkBrowseState.State.Loaded
        assertEquals(1, loaded.sections.filterIsInstance<WorkBrowseState.Section.Movies>().size)
        assertTrue(loaded.sections.none { it is WorkBrowseState.Section.TvShows })
    }

    @Test
    fun `current network results remain visible when an explicit refresh fails`() = runTest(testDispatcher) {
        whenever(getBrowseDataUseCase())
            .thenReturn(BROWSE_SECTIONS)
            .thenThrow(RuntimeException("Network unavailable"))
        viewModel = createViewModel()
        advanceUntilIdle()
        transitionToLoadedState()
        viewModel.handleEvent(WorkBrowseEvent.LoadData)
        advanceUntilIdle()

        val loaded = viewModel.state.value.state as WorkBrowseViewModelStateLoaded
        val movie = loaded.sections.filterIsInstance<WorkBrowseViewModelSectionMovies>().single()
        assertEquals(
            1,
            movie.content
                .single()
                .works
                .single()
                .id
        )
    }

    @Test
    fun `Room favorite emissions add and remove the favorites section`() = runTest(testDispatcher) {
        viewModel = createViewModel()
        advanceUntilIdle()
        transitionToLoadedState()

        observedFavorites.value = listOf(
            domainWork(1, WorkDomainModel.Type.MOVIE, true),
            domainWork(3, WorkDomainModel.Type.TV_SHOW, true),
        )
        advanceUntilIdle()
        val loaded = viewModel.state.value.state as WorkBrowseViewModelStateLoaded
        val favorites = loaded.sections.filterIsInstance<WorkBrowseViewModelSectionFavorites>().single()
        assertEquals(
            listOf(1, 3),
            favorites.content
                .single()
                .works
                .map { it.id }
        )
        val movie = loaded.sections.filterIsInstance<WorkBrowseViewModelSectionMovies>().single()
        assertTrue(
            movie.content
                .single()
                .works
                .single()
                .isFavorite
        )

        observedFavorites.value = emptyList()
        advanceUntilIdle()
        val updated = viewModel.state.value.state as WorkBrowseViewModelStateLoaded
        assertTrue(updated.sections.none { it is WorkBrowseViewModelSectionFavorites })
        val updatedMovie = updated.sections.filterIsInstance<WorkBrowseViewModelSectionMovies>().single()
        assertEquals(
            false,
            updatedMovie.content
                .single()
                .works
                .single()
                .isFavorite
        )
    }

    @Test
    fun `favorite observer cancellation is propagated`() = runTest(testDispatcher) {
        val refreshFinished = CompletableDeferred<BrowseSectionDetails>()
        var observerJob: Job? = null
        whenever(observeFavoritesUseCase()).thenReturn(
            flow {
                observerJob = currentCoroutineContext()[Job]
                throw CancellationException()
            },
        )
        whenever(getBrowseDataUseCase()).doSuspendableAnswer { refreshFinished.await() }

        viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(observerJob?.isCancelled == true)
        refreshFinished.complete(BROWSE_SECTIONS)
        advanceUntilIdle()
    }

    @Test
    fun `refresh cancellation leaves initial loading state`() = runTest(testDispatcher) {
        whenever(getBrowseDataUseCase()).thenThrow(CancellationException())
        viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.state is WorkBrowseViewModelStateLoading)
    }

    @Test
    fun `retry after initial failure loads sections`() = runTest(testDispatcher) {
        whenever(getBrowseDataUseCase())
            .thenThrow(RuntimeException("First request fails"))
            .thenReturn(BROWSE_SECTIONS)
        viewModel = createViewModel()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.state is WorkBrowseViewModelStateError)

        viewModel.handleEvent(WorkBrowseEvent.RetryLoad)
        advanceUntilIdle()
        transitionToLoadedState()

        assertTrue(viewModel.state.value.state is WorkBrowseViewModelStateLoaded)
    }

    @Test
    fun `backClicked emits CloseScreen effect`() = runTest(testDispatcher) {
        viewModel = createViewModel()
        val effects = mutableListOf<WorkBrowseEffect>()
        val job = launch { viewModel.effects.collect { effects.add(it) } }

        viewModel.handleEvent(WorkBrowseEvent.BackClicked)
        advanceUntilIdle()

        assertTrue(effects.any { it is WorkBrowseEffect.CloseScreen })
        job.cancel()
    }

    @Test
    fun `selecting Search emits navigation effect`() = runTest(testDispatcher) {
        val destination = SearchDestination.create()
        whenever(searchRoute.buildSearchDestination()).thenReturn(destination)
        viewModel = createViewModel()
        advanceUntilIdle()
        transitionToLoadedState()
        val effects = mutableListOf<WorkBrowseEffect>()
        val job = launch { viewModel.effects.collect { effects.add(it) } }

        viewModel.handleEvent(WorkBrowseEvent.SectionClicked(0))
        advanceUntilIdle()

        assertEquals(destination, effects.filterIsInstance<WorkBrowseEffect.Navigate>().single().destination)
        job.cancel()
    }

    @Test
    fun `selecting a non Search section updates selection`() = runTest(testDispatcher) {
        viewModel = createViewModel()
        advanceUntilIdle()
        transitionToLoadedState()

        viewModel.handleEvent(WorkBrowseEvent.SectionClicked(2))
        advanceUntilIdle()

        assertEquals(2, (viewModel.state.value.state as WorkBrowseViewModelStateLoaded).selectedSectionIndex)
    }

    @Test
    fun `workSelected updates selected work`() = runTest(testDispatcher) {
        viewModel = createViewModel()
        advanceUntilIdle()
        transitionToLoadedState()

        viewModel.handleEvent(WorkBrowseEvent.WorkSelected(WORK))
        advanceUntilIdle()

        assertEquals(WORK, (viewModel.state.value.state as WorkBrowseViewModelStateLoaded).workSelected)
    }

    @Test
    fun `workClicked emits work details navigation`() = runTest(testDispatcher) {
        val destination = WorkDetailsDestination.create(WORK)
        whenever(workDetailsRoute.buildWorkDetailsDestination(WORK)).thenReturn(destination)
        viewModel = createViewModel()
        val effects = mutableListOf<WorkBrowseEffect>()
        val job = launch { viewModel.effects.collect { effects.add(it) } }

        viewModel.handleEvent(WorkBrowseEvent.WorkClicked(WORK))
        advanceUntilIdle()

        assertNotNull(effects.filterIsInstance<WorkBrowseEffect.Navigate>().single())
        assertEquals(destination, effects.filterIsInstance<WorkBrowseEffect.Navigate>().single().destination)
        job.cancel()
    }
}
