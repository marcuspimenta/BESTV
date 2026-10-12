package com.pimenta.bestv.workbrowse.domain

import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.workbrowse.domain.model.BrowseCategoryKey
import com.pimenta.bestv.workbrowse.domain.model.BrowseSectionDomainModel
import com.pimenta.bestv.workbrowse.domain.model.GenreDomainModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.mockito.kotlin.any
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
        source = "tmdb",
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
    fun `getAllSections propagates loading failures`() =
        runTest {
            whenever(loadWorkByTypeUseCase(eq(1), any<BrowseCategoryKey>())).thenThrow(RuntimeException("Network error"))

            try {
                useCase.getAllSections()
                fail("Expected RuntimeException")
            } catch (exception: RuntimeException) {
                assertEquals("Network error", exception.message)
            }
        }
}
