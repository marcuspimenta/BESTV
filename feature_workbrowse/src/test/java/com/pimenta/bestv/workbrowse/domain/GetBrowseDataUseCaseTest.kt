package com.pimenta.bestv.workbrowse.domain

import com.pimenta.bestv.model.domain.ContentSource
import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.workbrowse.domain.model.BrowseCategoryKey
import com.pimenta.bestv.workbrowse.domain.model.BrowseSectionDomainModel
import com.pimenta.bestv.workbrowse.domain.model.GenreDomainModel
import com.pimenta.bestv.workbrowse.domain.model.movieBrowseCategories
import com.pimenta.bestv.workbrowse.domain.model.tvShowBrowseCategories
import com.pimenta.bestv.workbrowse.domain.repository.GenreRepository
import com.pimenta.bestv.workbrowse.domain.repository.MovieRepository
import com.pimenta.bestv.workbrowse.domain.repository.TvShowRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

private val MOVIE_PAGE =
    PageDomainModel(
        page = 1,
        totalPages = 5,
        results =
        listOf(
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
            ),
        ),
    )

private val TV_SHOW_PAGE =
    PageDomainModel(
        page = 1,
        totalPages = 1,
        results =
        listOf(
            WorkDomainModel(
                id = 2,
                title = "Test show",
                originalTitle = "Test show",
                source = ContentSource.TMDB,
                type = WorkDomainModel.Type.TV_SHOW,
            ),
        ),
    )

private val EMPTY_PAGE = PageDomainModel<WorkDomainModel>(page = 1, totalPages = 0, results = emptyList())
private val MOVIE_GENRE = GenreDomainModel(id = 28, name = "Action", source = GenreDomainModel.Source.MOVIE)
private val TV_SHOW_GENRE =
    GenreDomainModel(
        id = 10759,
        name = "Action & Adventure",
        source = GenreDomainModel.Source.TV_SHOW,
    )

class GetBrowseDataUseCaseTest {
    private val movieRepository: MovieRepository = mock()
    private val tvShowRepository: TvShowRepository = mock()
    private val genreRepository: GenreRepository = mock()
    private val useCase = GetBrowseDataUseCase(movieRepository, tvShowRepository, genreRepository)

    @Test
    fun `loads first page of each category and genre from the appropriate repository`() = runTest {
        stubSuccessfulRequests()

        val sections = useCase()

        verify(movieRepository).getNowPlayingMovies(FIRST_PAGE)
        verify(movieRepository).getPopularMovies(FIRST_PAGE)
        verify(movieRepository).getTopRatedMovies(FIRST_PAGE)
        verify(movieRepository).getUpComingMovies(FIRST_PAGE)
        verify(tvShowRepository).getAiringTodayTvShows(FIRST_PAGE)
        verify(tvShowRepository).getOnTheAirTvShows(FIRST_PAGE)
        verify(tvShowRepository).getTopRatedTvShows(FIRST_PAGE)
        verify(tvShowRepository).getPopularTvShows(FIRST_PAGE)
        verify(genreRepository).getMovieGenres()
        verify(genreRepository).getTvShowGenres()
        verify(movieRepository).getMoviesByGenre(MOVIE_GENRE.id, FIRST_PAGE)
        verify(tvShowRepository).getTvShowByGenre(TV_SHOW_GENRE.id, FIRST_PAGE)
        assertEquals(
            movieBrowseCategories,
            sections.movieSections.filterIsInstance<BrowseSectionDomainModel.TopContent>().map { it.category },
        )
        assertEquals(
            listOf(MOVIE_GENRE),
            sections.movieSections.filterIsInstance<BrowseSectionDomainModel.GenreContent>().map { it.genre },
        )
        assertEquals(
            tvShowBrowseCategories,
            sections.tvShowSections.filterIsInstance<BrowseSectionDomainModel.TopContent>().map { it.category },
        )
        assertEquals(
            listOf(TV_SHOW_GENRE),
            sections.tvShowSections.filterIsInstance<BrowseSectionDomainModel.GenreContent>().map { it.genre },
        )
    }

    @Test
    fun `partial remote group failure does not fail the browse operation`() = runTest {
        stubSuccessfulRequests()
        whenever(movieRepository.getPopularMovies(FIRST_PAGE))
            .thenThrow(RuntimeException("Popular movies unavailable"))

        val sections = useCase()

        assertTrue(
            sections.movieSections.any {
                it is BrowseSectionDomainModel.TopContent && it.category == BrowseCategoryKey.NOW_PLAYING_MOVIES
            },
        )
        assertTrue(
            sections.movieSections.none {
                it is BrowseSectionDomainModel.TopContent && it.category == BrowseCategoryKey.POPULAR_MOVIES
            },
        )
    }

    @Test
    fun `all remote groups failing reports failure`() = runTest {
        stubCategoryFailures()
        whenever(genreRepository.getMovieGenres()).thenThrow(RuntimeException("Movie genres unavailable"))
        whenever(genreRepository.getTvShowGenres()).thenThrow(RuntimeException("TV genres unavailable"))

        val error = runCatching { useCase() }.exceptionOrNull()

        assertTrue(error is RuntimeException)
    }

    @Test
    fun `successful empty responses count as a successful browse load`() = runTest {
        stubEmptyCategoryResponses()
        whenever(genreRepository.getMovieGenres()).thenReturn(emptyList())
        whenever(genreRepository.getTvShowGenres()).thenReturn(emptyList())

        val sections = useCase()

        assertEquals(
            movieBrowseCategories,
            sections.movieSections.filterIsInstance<BrowseSectionDomainModel.TopContent>().map { it.category },
        )
        assertEquals(
            tvShowBrowseCategories,
            sections.tvShowSections.filterIsInstance<BrowseSectionDomainModel.TopContent>().map { it.category },
        )
        assertTrue(sections.movieSections.all { it.works.isEmpty() })
        assertTrue(sections.tvShowSections.all { it.works.isEmpty() })
    }

    @Test
    fun `category cancellation propagates`() = runTest {
        stubSuccessfulRequests()
        whenever(movieRepository.getPopularMovies(FIRST_PAGE)).thenThrow(CancellationException())

        val error = runCatching { useCase() }.exceptionOrNull()

        assertTrue(error is CancellationException)
    }

    @Test
    fun `genre discovery cancellation propagates`() = runTest {
        stubSuccessfulRequests()
        whenever(genreRepository.getMovieGenres()).thenThrow(CancellationException())

        val error = runCatching { useCase() }.exceptionOrNull()

        assertTrue(error is CancellationException)
    }

    private suspend fun stubSuccessfulRequests() {
        whenever(movieRepository.getNowPlayingMovies(FIRST_PAGE)).thenReturn(MOVIE_PAGE)
        whenever(movieRepository.getPopularMovies(FIRST_PAGE)).thenReturn(MOVIE_PAGE)
        whenever(movieRepository.getTopRatedMovies(FIRST_PAGE)).thenReturn(MOVIE_PAGE)
        whenever(movieRepository.getUpComingMovies(FIRST_PAGE)).thenReturn(MOVIE_PAGE)
        whenever(tvShowRepository.getAiringTodayTvShows(FIRST_PAGE)).thenReturn(TV_SHOW_PAGE)
        whenever(tvShowRepository.getOnTheAirTvShows(FIRST_PAGE)).thenReturn(TV_SHOW_PAGE)
        whenever(tvShowRepository.getTopRatedTvShows(FIRST_PAGE)).thenReturn(TV_SHOW_PAGE)
        whenever(tvShowRepository.getPopularTvShows(FIRST_PAGE)).thenReturn(TV_SHOW_PAGE)
        whenever(genreRepository.getMovieGenres()).thenReturn(listOf(MOVIE_GENRE))
        whenever(genreRepository.getTvShowGenres()).thenReturn(listOf(TV_SHOW_GENRE))
        whenever(movieRepository.getMoviesByGenre(MOVIE_GENRE.id, FIRST_PAGE)).thenReturn(MOVIE_PAGE)
        whenever(tvShowRepository.getTvShowByGenre(TV_SHOW_GENRE.id, FIRST_PAGE)).thenReturn(TV_SHOW_PAGE)
    }

    private suspend fun stubCategoryFailures() {
        whenever(movieRepository.getNowPlayingMovies(FIRST_PAGE)).thenThrow(RuntimeException("Unavailable"))
        whenever(movieRepository.getPopularMovies(FIRST_PAGE)).thenThrow(RuntimeException("Unavailable"))
        whenever(movieRepository.getTopRatedMovies(FIRST_PAGE)).thenThrow(RuntimeException("Unavailable"))
        whenever(movieRepository.getUpComingMovies(FIRST_PAGE)).thenThrow(RuntimeException("Unavailable"))
        whenever(tvShowRepository.getAiringTodayTvShows(FIRST_PAGE)).thenThrow(RuntimeException("Unavailable"))
        whenever(tvShowRepository.getOnTheAirTvShows(FIRST_PAGE)).thenThrow(RuntimeException("Unavailable"))
        whenever(tvShowRepository.getTopRatedTvShows(FIRST_PAGE)).thenThrow(RuntimeException("Unavailable"))
        whenever(tvShowRepository.getPopularTvShows(FIRST_PAGE)).thenThrow(RuntimeException("Unavailable"))
    }

    private suspend fun stubEmptyCategoryResponses() {
        whenever(movieRepository.getNowPlayingMovies(FIRST_PAGE)).thenReturn(EMPTY_PAGE)
        whenever(movieRepository.getPopularMovies(FIRST_PAGE)).thenReturn(EMPTY_PAGE)
        whenever(movieRepository.getTopRatedMovies(FIRST_PAGE)).thenReturn(EMPTY_PAGE)
        whenever(movieRepository.getUpComingMovies(FIRST_PAGE)).thenReturn(EMPTY_PAGE)
        whenever(tvShowRepository.getAiringTodayTvShows(FIRST_PAGE)).thenReturn(EMPTY_PAGE)
        whenever(tvShowRepository.getOnTheAirTvShows(FIRST_PAGE)).thenReturn(EMPTY_PAGE)
        whenever(tvShowRepository.getTopRatedTvShows(FIRST_PAGE)).thenReturn(EMPTY_PAGE)
        whenever(tvShowRepository.getPopularTvShows(FIRST_PAGE)).thenReturn(EMPTY_PAGE)
    }

    private companion object {
        const val FIRST_PAGE = 1
    }
}
