package com.pimenta.bestv.workbrowse.domain

import com.pimenta.bestv.model.presentation.mapper.toViewModel
import com.pimenta.bestv.workbrowse.presentation.mapper.toViewModel
import com.pimenta.bestv.workbrowse.presentation.model.ContentSection
import com.pimenta.bestv.presentation.model.PaginationState
import com.pimenta.bestv.workbrowse.presentation.model.Source
import com.pimenta.bestv.workbrowse.presentation.model.TopWorkTypeViewModel
import com.pimenta.bestv.workbrowse.presentation.model.TopWorkTypeViewModel.FAVORITES_MOVIES
import com.pimenta.bestv.workbrowse.presentation.model.topMoviesTypes
import com.pimenta.bestv.workbrowse.presentation.model.topTvShowTypes
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class GetSectionDetailsUseCase(
    private val getMovieGenresUseCase: GetMovieGenresUseCase,
    private val getTvShowGenresUseCase: GetTvShowGenresUseCase,
    private val getWorkByGenreUseCase: GetWorkByGenreUseCase,
    private val loadWorkByTypeUseCase: LoadWorkByTypeUseCase
) {

    suspend fun getAllSections(): SectionDetails = coroutineScope {
        val movieTopWorksDeferred = async { topMoviesTypes.getWorks() }
        val movieGenresDeferred = getWorksByGenres(Source.MOVIE)
        val tvTopWorksDeferred = async { topTvShowTypes.getWorks() }
        val tvGenresDeferred = getWorksByGenres(Source.TV_SHOW)
        val favoritesDeferred = async { getFavoriteSections() }

        SectionDetails(
            movieSectionDetails = movieTopWorksDeferred.await() + movieGenresDeferred.await(),
            tvSectionDetails = tvTopWorksDeferred.await() + tvGenresDeferred.await(),
            favoriteSectionDetails = favoritesDeferred.await()
        )
    }

    suspend fun getFavoriteSections(): List<ContentSection> =
        listOfNotNull(FAVORITES_MOVIES.getWorks())

    private suspend fun List<TopWorkTypeViewModel>.getWorks() = coroutineScope {
        map { async { it.getWorks() } }.awaitAll().filterNotNull()
    }

    private suspend fun TopWorkTypeViewModel.getWorks(): ContentSection? {
        val resultPage = loadWorkByTypeUseCase(1, this).toViewModel()
        return ContentSection.TopContent(
            type = this,
            works = resultPage.results,
            page = PaginationState(
                currentPage = resultPage.page,
                totalPages = resultPage.totalPages
            )
        ).takeIf { resultPage.results.isNotEmpty() }
    }

    private suspend fun getWorksByGenres(source: Source) = coroutineScope {
        async {
            val genres = when (source) {
                Source.MOVIE -> getMovieGenresUseCase()
                Source.TV_SHOW -> getTvShowGenresUseCase()
            }
            genres?.map { genre ->
                async {
                    val genreViewModel = genre.toViewModel()
                    val resultPage = getWorkByGenreUseCase(genreViewModel.id, source, 1).toViewModel()
                    ContentSection.Genre(
                        genreViewModel = genreViewModel,
                        works = resultPage.results,
                        page = PaginationState(
                            currentPage = resultPage.page,
                            totalPages = resultPage.totalPages
                        )
                    ).takeIf { resultPage.results.isNotEmpty() }
                }
            }?.awaitAll()?.filterNotNull().orEmpty()
        }
    }
}

data class SectionDetails(
    val movieSectionDetails: List<ContentSection>,
    val tvSectionDetails: List<ContentSection>,
    val favoriteSectionDetails: List<ContentSection>
)