package com.pimenta.bestv.workbrowse.domain

import com.pimenta.bestv.workbrowse.domain.model.BrowseCategoryKey
import com.pimenta.bestv.workbrowse.domain.model.BrowseSectionDetails
import com.pimenta.bestv.workbrowse.domain.model.BrowseSectionDomainModel
import com.pimenta.bestv.workbrowse.domain.model.GenreDomainModel
import com.pimenta.bestv.workbrowse.domain.model.movieBrowseCategories
import com.pimenta.bestv.workbrowse.domain.model.tvShowBrowseCategories
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class GetSectionDetailsUseCase(
    private val getMovieGenresUseCase: GetMovieGenresUseCase,
    private val getTvShowGenresUseCase: GetTvShowGenresUseCase,
    private val getWorkByGenreUseCase: GetWorkByGenreUseCase,
    private val loadWorkByTypeUseCase: LoadWorkByTypeUseCase,
) {
    suspend fun getAllSections(): BrowseSectionDetails =
        coroutineScope {
            val movieTopSections = async { movieBrowseCategories.getSections() }
            val movieGenreSections = async { getWorksByGenres(GenreDomainModel.Source.MOVIE) }
            val tvShowTopSections = async { tvShowBrowseCategories.getSections() }
            val tvShowGenreSections = async { getWorksByGenres(GenreDomainModel.Source.TV_SHOW) }
            val favoriteSections = async { getFavoriteSections() }

            BrowseSectionDetails(
                movieSections = movieTopSections.await() + movieGenreSections.await(),
                tvShowSections = tvShowTopSections.await() + tvShowGenreSections.await(),
                favoriteSections = favoriteSections.await(),
            )
        }

    suspend fun getFavoriteSections(): List<BrowseSectionDomainModel> =
        listOfNotNull(BrowseCategoryKey.FAVORITES_MOVIES.getSection())

    private suspend fun List<BrowseCategoryKey>.getSections(): List<BrowseSectionDomainModel> =
        coroutineScope {
            map { category -> async { category.getSection() } }
                .awaitAll()
                .filterNotNull()
        }

    private suspend fun BrowseCategoryKey.getSection(): BrowseSectionDomainModel.TopContent? {
        val page = loadWorkByTypeUseCase(1, this)
        return BrowseSectionDomainModel
            .TopContent(category = this, page = page)
            .takeIf { !page.results.isNullOrEmpty() }
    }

    private suspend fun getWorksByGenres(source: GenreDomainModel.Source): List<BrowseSectionDomainModel> =
        coroutineScope {
            val genres =
                when (source) {
                    GenreDomainModel.Source.MOVIE -> getMovieGenresUseCase()
                    GenreDomainModel.Source.TV_SHOW -> getTvShowGenresUseCase()
                }.orEmpty()

            val sectionRequests =
                genres.map { genre ->
                    async {
                        val page = getWorkByGenreUseCase(genre.id, genre.source, 1)
                        BrowseSectionDomainModel
                            .GenreContent(genre = genre, page = page)
                            .takeIf { !page.results.isNullOrEmpty() }
                    }
                }

            sectionRequests.awaitAll().filterNotNull()
        }
}
