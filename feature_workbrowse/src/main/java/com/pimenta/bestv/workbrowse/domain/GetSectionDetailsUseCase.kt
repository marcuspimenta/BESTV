package com.pimenta.bestv.workbrowse.domain

import com.pimenta.bestv.model.extension.asyncCatching
import com.pimenta.bestv.workbrowse.domain.model.BrowseCategoryKey
import com.pimenta.bestv.workbrowse.domain.model.BrowseSectionDetails
import com.pimenta.bestv.workbrowse.domain.model.BrowseSectionDomainModel
import com.pimenta.bestv.workbrowse.domain.model.GenreDomainModel
import com.pimenta.bestv.workbrowse.domain.model.movieBrowseCategories
import com.pimenta.bestv.workbrowse.domain.model.tvShowBrowseCategories
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class GetSectionDetailsUseCase(
    private val getMovieGenresUseCase: GetMovieGenresUseCase,
    private val getTvShowGenresUseCase: GetTvShowGenresUseCase,
    private val getWorkByGenreUseCase: GetWorkByGenreUseCase,
    private val loadWorkByTypeUseCase: LoadWorkByTypeUseCase,
) {

    suspend fun getAllSections() = coroutineScope {
        val movieTopSections = asyncCatching { movieBrowseCategories.getSections() }
        val movieGenreSections = asyncCatching { getWorksByGenres(GenreDomainModel.Source.MOVIE) }
        val tvShowTopSections = asyncCatching { tvShowBrowseCategories.getSections() }
        val tvShowGenreSections = asyncCatching { getWorksByGenres(GenreDomainModel.Source.TV_SHOW) }
        val favoriteSections = asyncCatching { getFavoriteSections() }

        val results = awaitAll(
            movieTopSections,
            movieGenreSections,
            tvShowTopSections,
            tvShowGenreSections,
            favoriteSections,
        )
        // Local favorites must not mask a complete remote failure.
        val remoteResults = results.dropLast(1)
        if (remoteResults.all { it.isFailure }) {
            remoteResults.first().getOrThrow()
        }

        val (movieTopResult, movieGenreResult, tvShowTopResult, tvShowGenreResult, favoriteResult) = results

        BrowseSectionDetails(
            movieSections = movieTopResult.getOrElse { emptyList() } + movieGenreResult.getOrElse { emptyList() },
            tvShowSections = tvShowTopResult.getOrElse { emptyList() } + tvShowGenreResult.getOrElse { emptyList() },
            favoriteSections = favoriteResult.getOrElse { emptyList() },
        )
    }

    suspend fun getFavoriteSections() = listOfNotNull(BrowseCategoryKey.FAVORITES_MOVIES.getSection())

    private suspend fun List<BrowseCategoryKey>.getSections() = coroutineScope {
        map { category -> asyncCatching { category.getSection() } }
            .awaitAll()
            .sectionsOrThrow()
    }

    private suspend fun BrowseCategoryKey.getSection(): BrowseSectionDomainModel.TopContent? {
        val page = loadWorkByTypeUseCase(1, this)
        return BrowseSectionDomainModel
            .TopContent(category = this, page = page)
            .takeIf { !page.results.isNullOrEmpty() }
    }

    private suspend fun getWorksByGenres(source: GenreDomainModel.Source) = coroutineScope {
        val genres =
            when (source) {
                GenreDomainModel.Source.MOVIE -> getMovieGenresUseCase()
                GenreDomainModel.Source.TV_SHOW -> getTvShowGenresUseCase()
            }.orEmpty()

        val sectionRequests =
            genres.map { genre ->
                asyncCatching {
                    val page = getWorkByGenreUseCase(genre.id, genre.source, 1)
                    BrowseSectionDomainModel
                        .GenreContent(genre = genre, page = page)
                        .takeIf { !page.results.isNullOrEmpty() }
                }
            }

        sectionRequests.awaitAll().sectionsOrThrow()
    }

    private fun List<Result<BrowseSectionDomainModel?>>.sectionsOrThrow(): List<BrowseSectionDomainModel> {
        if (isNotEmpty() && all { it.isFailure }) {
            first().getOrThrow()
        }
        return mapNotNull { it.getOrNull() }
    }
}
