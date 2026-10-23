package com.pimenta.bestv.workbrowse.presentation.mapper

import com.pimenta.bestv.presentation.mapper.toViewModel
import com.pimenta.bestv.presentation.model.PaginationState
import com.pimenta.bestv.presentation.platform.Resource
import com.pimenta.bestv.workbrowse.domain.model.BrowseCategoryKey
import com.pimenta.bestv.workbrowse.domain.model.BrowseSectionDomainModel
import com.pimenta.bestv.workbrowse.presentation.model.ContentSection
import com.pimenta.bestv.workbrowse.presentation.model.TopWorkTypeViewModel

fun BrowseSectionDomainModel.toViewModel(resource: Resource): ContentSection? {
    val pageViewModel = page.toViewModel(resource)
    if (pageViewModel.results.isEmpty()) return null

    val pagination =
        PaginationState(
            currentPage = pageViewModel.page,
            totalPages = pageViewModel.totalPages,
        )

    return when (this) {
        is BrowseSectionDomainModel.TopContent ->
            ContentSection.TopContent(
                type = category.toViewModel(),
                works = pageViewModel.results,
                page = pagination,
            )
        is BrowseSectionDomainModel.GenreContent ->
            ContentSection.Genre(
                genreViewModel = genre.toViewModel(),
                works = pageViewModel.results,
                page = pagination,
            )
    }
}

fun List<BrowseSectionDomainModel>.toViewModels(resource: Resource): List<ContentSection> =
    mapNotNull { it.toViewModel(resource) }

private fun BrowseCategoryKey.toViewModel(): TopWorkTypeViewModel =
    when (this) {
        BrowseCategoryKey.FAVORITES_MOVIES -> TopWorkTypeViewModel.FAVORITES_MOVIES
        BrowseCategoryKey.NOW_PLAYING_MOVIES -> TopWorkTypeViewModel.NOW_PLAYING_MOVIES
        BrowseCategoryKey.POPULAR_MOVIES -> TopWorkTypeViewModel.POPULAR_MOVIES
        BrowseCategoryKey.TOP_RATED_MOVIES -> TopWorkTypeViewModel.TOP_RATED_MOVIES
        BrowseCategoryKey.UP_COMING_MOVIES -> TopWorkTypeViewModel.UP_COMING_MOVIES
        BrowseCategoryKey.AIRING_TODAY_TV_SHOWS -> TopWorkTypeViewModel.AIRING_TODAY_TV_SHOWS
        BrowseCategoryKey.ON_THE_AIR_TV_SHOWS -> TopWorkTypeViewModel.ON_THE_AIR_TV_SHOWS
        BrowseCategoryKey.POPULAR_TV_SHOWS -> TopWorkTypeViewModel.POPULAR_TV_SHOWS
        BrowseCategoryKey.TOP_RATED_TV_SHOWS -> TopWorkTypeViewModel.TOP_RATED_TV_SHOWS
    }
