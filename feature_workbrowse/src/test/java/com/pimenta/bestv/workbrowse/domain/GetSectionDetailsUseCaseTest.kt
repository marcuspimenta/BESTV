package com.pimenta.bestv.workbrowse.domain

import com.pimenta.bestv.model.domain.ContentSource
import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.workbrowse.domain.model.BrowseCategoryKey
import com.pimenta.bestv.workbrowse.domain.model.BrowseSectionDomainModel
import com.pimenta.bestv.workbrowse.domain.model.GenreDomainModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

private val WORK_DOMAIN_MODEL =
    WorkDomainModel(
        id = 1,
        title = "Batman",
        originalTitle = "Batman",
        originalLanguage = "en",
        overview = "A superhero movie",
        source = ContentSource.TMDB,
        backdropPath = "/backdrop.jpg",
        posterPath = "/poster.jpg",
        releaseDate = "2023-01-01",
        type = WorkDomainModel.Type.MOVIE,
        voteAverage = 8.0f,
    )

private val MOVIE_PAGE =
    PageDomainModel(
        page = 2,
        totalPages = 5,
        results = listOf(WORK_DOMAIN_MODEL),
    )

private val TV_SHOW_PAGE =
    PageDomainModel(
        page = 1,
        totalPages = 3,
        results = listOf(WORK_DOMAIN_MODEL.copy(id = 2, type = WorkDomainModel.Type.TV_SHOW)),
    )

private val FAVORITES_PAGE =
    PageDomainModel(
        page = 1,
        totalPages = 1,
        results = listOf(WORK_DOMAIN_MODEL.copy(id = 3, isFavorite = true)),
    )

private val MOVIE_GENRE = GenreDomainModel(id = 28, name = "Action", source = GenreDomainModel.Source.MOVIE)
private val TV_SHOW_GENRE =
    GenreDomainModel(
        id = 10759,
        name = "Action & Adventure",
        source = GenreDomainModel.Source.TV_SHOW,
    )

class GetSectionDetailsUseCaseTest {
    private val getMovieGenresUseCase: GetMovieGenresUseCase = mock()
    private val getTvShowGenresUseCase: GetTvShowGenresUseCase = mock()
    private val getWorkByGenreUseCase: GetWorkByGenreUseCase = mock()
    private val loadWorkByTypeUseCase: LoadWorkByTypeUseCase = mock()

    private val useCase =
        GetSectionDetailsUseCase(
            getMovieGenresUseCase,
            getTvShowGenresUseCase,
            getWorkByGenreUseCase,
            loadWorkByTypeUseCase,
        )

    @Test
    fun `getAllSections preserves category order and appends genres with their domain source`() =
        runTest {
            whenever(loadWorkByTypeUseCase(eq(1), any<BrowseCategoryKey>())).thenReturn(MOVIE_PAGE)
            whenever(getMovieGenresUseCase()).thenReturn(listOf(MOVIE_GENRE))
            whenever(getTvShowGenresUseCase()).thenReturn(listOf(TV_SHOW_GENRE))
            whenever(getWorkByGenreUseCase(eq(MOVIE_GENRE.id), eq(GenreDomainModel.Source.MOVIE), eq(1)))
                .thenReturn(MOVIE_PAGE)
            whenever(getWorkByGenreUseCase(eq(TV_SHOW_GENRE.id), eq(GenreDomainModel.Source.TV_SHOW), eq(1)))
                .thenReturn(TV_SHOW_PAGE)

            val result = useCase.getAllSections()

            assertEquals(
                listOf(
                    BrowseCategoryKey.NOW_PLAYING_MOVIES,
                    BrowseCategoryKey.POPULAR_MOVIES,
                    BrowseCategoryKey.TOP_RATED_MOVIES,
                    BrowseCategoryKey.UP_COMING_MOVIES,
                ),
                result.movieSections.take(4).map { (it as BrowseSectionDomainModel.TopContent).category },
            )
            assertEquals(MOVIE_GENRE, (result.movieSections.last() as BrowseSectionDomainModel.GenreContent).genre)
            assertEquals(
                listOf(
                    BrowseCategoryKey.AIRING_TODAY_TV_SHOWS,
                    BrowseCategoryKey.ON_THE_AIR_TV_SHOWS,
                    BrowseCategoryKey.TOP_RATED_TV_SHOWS,
                    BrowseCategoryKey.POPULAR_TV_SHOWS,
                ),
                result.tvShowSections.take(4).map { (it as BrowseSectionDomainModel.TopContent).category },
            )
            assertEquals(TV_SHOW_GENRE, (result.tvShowSections.last() as BrowseSectionDomainModel.GenreContent).genre)
            assertEquals(
                5,
                result.movieSections
                    .first()
                    .page
                    .totalPages,
            )
        }

    @Test
    fun `getAllSections filters empty top, genre, and favorite sections`() =
        runTest {
            val emptyPage = PageDomainModel<WorkDomainModel>(page = 1, totalPages = 1, results = emptyList())
            whenever(loadWorkByTypeUseCase(eq(1), any<BrowseCategoryKey>())).thenReturn(emptyPage)
            whenever(getMovieGenresUseCase()).thenReturn(listOf(MOVIE_GENRE))
            whenever(getTvShowGenresUseCase()).thenReturn(listOf(TV_SHOW_GENRE))
            whenever(getWorkByGenreUseCase(eq(MOVIE_GENRE.id), eq(GenreDomainModel.Source.MOVIE), eq(1)))
                .thenReturn(emptyPage)
            whenever(getWorkByGenreUseCase(eq(TV_SHOW_GENRE.id), eq(GenreDomainModel.Source.TV_SHOW), eq(1)))
                .thenReturn(emptyPage)

            val result = useCase.getAllSections()

            assertTrue(result.movieSections.isEmpty())
            assertTrue(result.tvShowSections.isEmpty())
            assertTrue(result.favoriteSections.isEmpty())
        }

    @Test
    fun `getFavoriteSections returns the favorites category and page`() =
        runTest {
            whenever(loadWorkByTypeUseCase(eq(1), eq(BrowseCategoryKey.FAVORITES_MOVIES))).thenReturn(FAVORITES_PAGE)

            val result = useCase.getFavoriteSections()

            assertEquals(1, result.size)
            val favorites = result.single() as BrowseSectionDomainModel.TopContent
            assertEquals(BrowseCategoryKey.FAVORITES_MOVIES, favorites.category)
            assertEquals(FAVORITES_PAGE, favorites.page)
        }

    @Test
    fun `getAllSections preserves successful categories in order when another category fails`() =
        runTest {
            stubSuccessfulRequests()
            whenever(loadWorkByTypeUseCase(1, BrowseCategoryKey.POPULAR_MOVIES))
                .thenThrow(RuntimeException("Popular movies unavailable"))
            whenever(loadWorkByTypeUseCase(1, BrowseCategoryKey.NOW_PLAYING_MOVIES)).doSuspendableAnswer {
                delay(100)
                MOVIE_PAGE
            }

            val result = useCase.getAllSections()

            assertEquals(
                listOf(
                    BrowseCategoryKey.NOW_PLAYING_MOVIES,
                    BrowseCategoryKey.TOP_RATED_MOVIES,
                    BrowseCategoryKey.UP_COMING_MOVIES,
                ),
                result.movieSections.filterIsInstance<BrowseSectionDomainModel.TopContent>().map { it.category },
            )
            assertEquals(MOVIE_GENRE, (result.movieSections.last() as BrowseSectionDomainModel.GenreContent).genre)
            assertTrue(result.tvShowSections.isNotEmpty())
            assertTrue(result.favoriteSections.isNotEmpty())
        }

    @Test
    fun `getAllSections preserves successful genres in order when another genre fails`() =
        runTest {
            stubSuccessfulRequests()
            val failedGenre = MOVIE_GENRE.copy(id = 18, name = "Drama")
            val otherGenre = MOVIE_GENRE.copy(id = 53, name = "Thriller")
            whenever(getMovieGenresUseCase()).thenReturn(listOf(MOVIE_GENRE, failedGenre, otherGenre))
            whenever(getWorkByGenreUseCase(MOVIE_GENRE.id, MOVIE_GENRE.source, 1)).doSuspendableAnswer {
                delay(100)
                MOVIE_PAGE
            }
            whenever(getWorkByGenreUseCase(failedGenre.id, failedGenre.source, 1))
                .thenThrow(RuntimeException("Genre unavailable"))
            whenever(getWorkByGenreUseCase(otherGenre.id, otherGenre.source, 1)).thenReturn(MOVIE_PAGE)

            val result = useCase.getAllSections()

            assertEquals(
                listOf(MOVIE_GENRE, otherGenre),
                result.movieSections.filterIsInstance<BrowseSectionDomainModel.GenreContent>().map { it.genre },
            )
        }

    @Test
    fun `getAllSections retains top sections when genre discovery and favorites fail`() =
        runTest {
            stubSuccessfulRequests()
            whenever(getMovieGenresUseCase()).thenThrow(RuntimeException("Movie genres unavailable"))
            whenever(getTvShowGenresUseCase()).thenThrow(RuntimeException("TV genres unavailable"))
            whenever(loadWorkByTypeUseCase(1, BrowseCategoryKey.FAVORITES_MOVIES))
                .thenThrow(RuntimeException("Favorites unavailable"))

            val result = useCase.getAllSections()

            assertEquals(4, result.movieSections.size)
            assertEquals(4, result.tvShowSections.size)
            assertTrue(result.favoriteSections.isEmpty())
        }

    @Test
    fun `getAllSections retains genre sections when all top categories fail`() =
        runTest {
            stubSuccessfulRequests()
            whenever(loadWorkByTypeUseCase(eq(1), any<BrowseCategoryKey>()))
                .thenThrow(RuntimeException("Category unavailable"))

            val result = useCase.getAllSections()

            assertEquals(MOVIE_GENRE, (result.movieSections.single() as BrowseSectionDomainModel.GenreContent).genre)
            assertEquals(TV_SHOW_GENRE, (result.tvShowSections.single() as BrowseSectionDomainModel.GenreContent).genre)
            assertTrue(result.favoriteSections.isEmpty())
        }

    @Test
    fun `getAllSections accepts a sole successful empty category`() =
        runTest {
            stubFailedRequests()
            val emptyPage = PageDomainModel<WorkDomainModel>(1, 0, emptyList())
            doReturn(emptyPage).whenever(loadWorkByTypeUseCase).invoke(1, BrowseCategoryKey.NOW_PLAYING_MOVIES)

            val result = useCase.getAllSections()

            assertTrue(result.movieSections.isEmpty())
            assertTrue(result.tvShowSections.isEmpty())
            assertTrue(result.favoriteSections.isEmpty())
        }

    @Test
    fun `getAllSections accepts a sole successful empty genre list`() =
        runTest {
            stubFailedRequests()
            doReturn(emptyList<GenreDomainModel>()).whenever(getMovieGenresUseCase).invoke()

            val result = useCase.getAllSections()

            assertTrue(result.movieSections.isEmpty())
            assertTrue(result.tvShowSections.isEmpty())
            assertTrue(result.favoriteSections.isEmpty())
        }

    @Test
    fun `getAllSections throws the first category error when all remote groups fail despite loaded favorites`() =
        runTest {
            stubFailedRequests()
            val exception = IllegalStateException("Now playing unavailable")
            doSuspendableAnswer {
                delay(100)
                throw exception
            }.whenever(loadWorkByTypeUseCase).invoke(1, BrowseCategoryKey.NOW_PLAYING_MOVIES)
            doReturn(FAVORITES_PAGE).whenever(loadWorkByTypeUseCase).invoke(1, BrowseCategoryKey.FAVORITES_MOVIES)

            val thrown = runCatching { useCase.getAllSections() }.exceptionOrNull()

            assertTrue(thrown is IllegalStateException)
            assertEquals(exception.message, thrown?.message)
        }

    @Test
    fun `getAllSections throws when all category and genre page requests fail`() =
        runTest {
            stubFailedRequests()
            doReturn(listOf(MOVIE_GENRE)).whenever(getMovieGenresUseCase).invoke()
            doReturn(listOf(TV_SHOW_GENRE)).whenever(getTvShowGenresUseCase).invoke()

            val thrown = runCatching { useCase.getAllSections() }.exceptionOrNull()

            assertTrue(thrown is RuntimeException)
            assertEquals("Category unavailable", thrown?.message)
        }

    @Test
    fun `getAllSections retains other sections when a category throws cancellation`() =
        runTest {
            stubSuccessfulRequests()
            whenever(loadWorkByTypeUseCase(1, BrowseCategoryKey.POPULAR_MOVIES)).thenThrow(CancellationException())

            val result = useCase.getAllSections()

            assertEquals(
                listOf(
                    BrowseCategoryKey.NOW_PLAYING_MOVIES,
                    BrowseCategoryKey.TOP_RATED_MOVIES,
                    BrowseCategoryKey.UP_COMING_MOVIES,
                ),
                result.movieSections.filterIsInstance<BrowseSectionDomainModel.TopContent>().map { it.category },
            )
            assertEquals(MOVIE_GENRE, (result.movieSections.last() as BrowseSectionDomainModel.GenreContent).genre)
            assertTrue(result.tvShowSections.isNotEmpty())
        }

    @Test
    fun `getAllSections retains other sections when genre discovery throws cancellation`() =
        runTest {
            stubSuccessfulRequests()
            whenever(getMovieGenresUseCase()).thenThrow(CancellationException())

            val result = useCase.getAllSections()

            assertEquals(4, result.movieSections.size)
            assertTrue(result.movieSections.all { it is BrowseSectionDomainModel.TopContent })
            assertEquals(TV_SHOW_GENRE, (result.tvShowSections.last() as BrowseSectionDomainModel.GenreContent).genre)
        }

    @Test
    fun `getAllSections finishes pending movie genres when a tv genre request throws cancellation`() =
        runTest {
            stubSuccessfulRequests()
            val moviesStarted = CompletableDeferred<Unit>()
            val exception = CancellationException("TV genre cancelled")
            whenever(getWorkByGenreUseCase(MOVIE_GENRE.id, MOVIE_GENRE.source, 1)).doSuspendableAnswer {
                moviesStarted.complete(Unit)
                delay(100)
                MOVIE_PAGE
            }
            whenever(getWorkByGenreUseCase(TV_SHOW_GENRE.id, TV_SHOW_GENRE.source, 1)).doSuspendableAnswer {
                moviesStarted.await()
                throw exception
            }

            val result = useCase.getAllSections()

            assertEquals(MOVIE_GENRE, (result.movieSections.last() as BrowseSectionDomainModel.GenreContent).genre)
            assertEquals(4, result.tvShowSections.size)
            assertTrue(result.tvShowSections.all { it is BrowseSectionDomainModel.TopContent })
        }

    @Test
    fun `getAllSections retains remote sections when favorites throw cancellation`() =
        runTest {
            stubSuccessfulRequests()
            whenever(loadWorkByTypeUseCase(1, BrowseCategoryKey.FAVORITES_MOVIES)).thenThrow(CancellationException())

            val result = useCase.getAllSections()

            assertTrue(result.favoriteSections.isEmpty())
            assertTrue(result.movieSections.isNotEmpty())
            assertTrue(result.tvShowSections.isNotEmpty())
        }

    private suspend fun stubSuccessfulRequests() {
        whenever(loadWorkByTypeUseCase(eq(1), any<BrowseCategoryKey>())).thenReturn(MOVIE_PAGE)
        whenever(getMovieGenresUseCase()).thenReturn(listOf(MOVIE_GENRE))
        whenever(getTvShowGenresUseCase()).thenReturn(listOf(TV_SHOW_GENRE))
        whenever(getWorkByGenreUseCase(MOVIE_GENRE.id, MOVIE_GENRE.source, 1)).thenReturn(MOVIE_PAGE)
        whenever(getWorkByGenreUseCase(TV_SHOW_GENRE.id, TV_SHOW_GENRE.source, 1)).thenReturn(TV_SHOW_PAGE)
    }

    private suspend fun stubFailedRequests() {
        whenever(loadWorkByTypeUseCase(eq(1), any<BrowseCategoryKey>())).thenThrow(RuntimeException("Category unavailable"))
        whenever(getMovieGenresUseCase()).thenThrow(RuntimeException("Movie genres unavailable"))
        whenever(getTvShowGenresUseCase()).thenThrow(RuntimeException("TV genres unavailable"))
        whenever(getWorkByGenreUseCase(any(), any(), eq(1))).thenThrow(RuntimeException("Genre works unavailable"))
    }
}
