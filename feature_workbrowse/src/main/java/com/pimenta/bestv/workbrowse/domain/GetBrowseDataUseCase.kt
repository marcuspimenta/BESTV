package com.pimenta.bestv.workbrowse.domain

import com.pimenta.bestv.model.extension.asyncCatching
import com.pimenta.bestv.model.domain.PageDomainModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.workbrowse.domain.model.BrowseCategoryKey
import com.pimenta.bestv.workbrowse.domain.model.BrowseCategoryKey.AIRING_TODAY_TV_SHOWS
import com.pimenta.bestv.workbrowse.domain.model.BrowseCategoryKey.FAVORITES_MOVIES
import com.pimenta.bestv.workbrowse.domain.model.BrowseCategoryKey.NOW_PLAYING_MOVIES
import com.pimenta.bestv.workbrowse.domain.model.BrowseCategoryKey.ON_THE_AIR_TV_SHOWS
import com.pimenta.bestv.workbrowse.domain.model.BrowseCategoryKey.POPULAR_MOVIES
import com.pimenta.bestv.workbrowse.domain.model.BrowseCategoryKey.POPULAR_TV_SHOWS
import com.pimenta.bestv.workbrowse.domain.model.BrowseCategoryKey.TOP_RATED_MOVIES
import com.pimenta.bestv.workbrowse.domain.model.BrowseCategoryKey.TOP_RATED_TV_SHOWS
import com.pimenta.bestv.workbrowse.domain.model.BrowseCategoryKey.UP_COMING_MOVIES
import com.pimenta.bestv.workbrowse.domain.model.BrowseSectionDetails
import com.pimenta.bestv.workbrowse.domain.model.BrowseSectionDomainModel
import com.pimenta.bestv.workbrowse.domain.model.GenreDomainModel
import com.pimenta.bestv.workbrowse.domain.model.GenreDomainModel.Source
import com.pimenta.bestv.workbrowse.domain.model.movieBrowseCategories
import com.pimenta.bestv.workbrowse.domain.model.tvShowBrowseCategories
import com.pimenta.bestv.workbrowse.domain.repository.GenreRepository
import com.pimenta.bestv.workbrowse.domain.repository.MovieRepository
import com.pimenta.bestv.workbrowse.domain.repository.TvShowRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class GetBrowseDataUseCase(
    private val movieRepository: MovieRepository,
    private val tvShowRepository: TvShowRepository,
    private val genreRepository: GenreRepository,
) {
    suspend operator fun invoke(): BrowseSectionDetails = coroutineScope {
        val movieTopSections = asyncCatching { refreshCategories(movieBrowseCategories) }
        val movieGenreSections = asyncCatching { refreshGenres(Source.MOVIE) }
        val tvShowTopSections = asyncCatching { refreshCategories(tvShowBrowseCategories) }
        val tvShowGenreSections = asyncCatching { refreshGenres(Source.TV_SHOW) }

        val results = awaitAll(movieTopSections, movieGenreSections, tvShowTopSections, tvShowGenreSections)
        results.throwIfCancelled()
        if (results.all { it.isFailure }) results.first().getOrThrow()

        BrowseSectionDetails(
            movieSections = results[0].getOrNull().orEmpty() + results[1].getOrNull().orEmpty(),
            tvShowSections = results[2].getOrNull().orEmpty() + results[3].getOrNull().orEmpty(),
        )
    }

    private suspend fun refreshCategories(categories: List<BrowseCategoryKey>) = coroutineScope {
        categories
            .map { category ->
                asyncCatching {
                    category to loadWorksByCategory(category)
                }
            }.awaitAll()
            .successfulValuesOrThrow()
            .map { (category, page) ->
                BrowseSectionDomainModel.TopContent(category, page.results.orEmpty())
            }
    }

    private suspend fun refreshGenres(source: Source) = coroutineScope {
        val genres = when (source) {
            Source.MOVIE -> genreRepository.getMovieGenres()
            Source.TV_SHOW -> genreRepository.getTvShowGenres()
        }

        genres
            .orEmpty()
            .map { genre ->
                asyncCatching {
                    genre to loadWorksByGenre(genre)
                }
            }.awaitAll()
            .successfulValuesOrThrow()
            .map { (genre, page) ->
                BrowseSectionDomainModel.GenreContent(genre, page.results.orEmpty())
            }
    }

    private suspend fun loadWorksByCategory(category: BrowseCategoryKey): PageDomainModel<WorkDomainModel> =
        when (category) {
            NOW_PLAYING_MOVIES -> movieRepository.getNowPlayingMovies(FIRST_PAGE)
            POPULAR_MOVIES -> movieRepository.getPopularMovies(FIRST_PAGE)
            TOP_RATED_MOVIES -> movieRepository.getTopRatedMovies(FIRST_PAGE)
            UP_COMING_MOVIES -> movieRepository.getUpComingMovies(FIRST_PAGE)
            AIRING_TODAY_TV_SHOWS -> tvShowRepository.getAiringTodayTvShows(FIRST_PAGE)
            ON_THE_AIR_TV_SHOWS -> tvShowRepository.getOnTheAirTvShows(FIRST_PAGE)
            POPULAR_TV_SHOWS -> tvShowRepository.getPopularTvShows(FIRST_PAGE)
            TOP_RATED_TV_SHOWS -> tvShowRepository.getTopRatedTvShows(FIRST_PAGE)
            FAVORITES_MOVIES -> error("Favorites are observed separately from Room.")
        }

    private suspend fun loadWorksByGenre(genre: GenreDomainModel): PageDomainModel<WorkDomainModel> =
        when (genre.source) {
            Source.MOVIE -> movieRepository.getMoviesByGenre(genre.id, FIRST_PAGE)
            Source.TV_SHOW -> tvShowRepository.getTvShowByGenre(genre.id, FIRST_PAGE)
        }

    private fun <T> List<Result<T>>.successfulValuesOrThrow(): List<T> {
        throwIfCancelled()
        if (isNotEmpty() && all { it.isFailure }) first().getOrThrow()
        return mapNotNull { it.getOrNull() }
    }

    private fun <T> List<Result<T>>.throwIfCancelled() {
        firstNotNullOfOrNull { it.exceptionOrNull() as? CancellationException }?.let { throw it }
    }

    private companion object {
        const val FIRST_PAGE = 1
    }
}
