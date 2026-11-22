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

import com.pimenta.bestv.domain.model.WorkDomainModel
import com.pimenta.bestv.presentation.model.WorkType
import com.pimenta.bestv.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.platform.ContentSourceLabelProvider
import com.pimenta.bestv.workbrowse.domain.model.BrowseSectionDetails
import com.pimenta.bestv.workbrowse.domain.model.BrowseSectionDomainModel
import com.pimenta.bestv.workbrowse.presentation.mapper.toViewModels
import com.pimenta.bestv.workbrowse.presentation.model.ContentSection
import com.pimenta.bestv.workbrowse.presentation.model.TopWorkTypeViewModel
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section.Favorites
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section.Movies
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section.Search
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section.TvShows
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.State.Loaded

internal fun WorkBrowseState.withBrowseSections(
    details: BrowseSectionDetails,
    favorites: List<WorkViewModel>,
    resource: ContentSourceLabelProvider,
): WorkBrowseState {
    val favoriteIds = favorites.mapTo(mutableSetOf()) { it.type to it.id }
    val movieSections = details.movieSections.withFavoriteFlags(favoriteIds).toViewModels(resource)
    val tvShowSections = details.tvShowSections.withFavoriteFlags(favoriteIds).toViewModels(resource)
    val favoriteSections = favorites.toFavoriteSections()
    val sections = listOfNotNull(
        Search,
        Movies(movieSections).takeIf { it.content.isNotEmpty() },
        TvShows(tvShowSections).takeIf { it.content.isNotEmpty() },
        Favorites(favoriteSections).takeIf { it.content.isNotEmpty() },
    )
    val previous = state as? Loaded
    return copy(
        state = Loaded(
            workSelected = previous?.workSelected,
            selectedSectionIndex = (previous?.selectedSectionIndex ?: 1).coerceAtMost(sections.lastIndex),
            sections = sections,
        ),
    )
}

internal fun WorkBrowseState.withFavorites(favorites: List<WorkViewModel>): WorkBrowseState {
    val favoriteIds = favorites.mapTo(mutableSetOf()) { it.type to it.id }
    val favoriteSections = favorites.toFavoriteSections()
    val loaded = state as? Loaded
    val updatedState = loaded?.let { current ->
        val sections = current.sections.mapNotNull { section ->
            when (section) {
                is Movies -> section.copy(content = section.content.withFavoriteFlagsInPresentation(favoriteIds))
                is TvShows -> section.copy(content = section.content.withFavoriteFlagsInPresentation(favoriteIds))
                is Favorites -> null
                Search -> Search
            }
        } + listOfNotNull(Favorites(favoriteSections).takeIf { favoriteSections.isNotEmpty() })
        current.copy(
            selectedSectionIndex = current.selectedSectionIndex.coerceAtMost(sections.lastIndex),
            sections = sections,
        )
    } ?: state
    return copy(latestFavorites = favorites, state = updatedState)
}

private fun List<WorkViewModel>.toFavoriteSections(): List<ContentSection> {
    if (isEmpty()) return emptyList()
    return listOf(
        ContentSection.TopContent(
            type = TopWorkTypeViewModel.FAVORITES_MOVIES,
            works = map { it.copy(isFavorite = true) },
        ),
    )
}

private fun List<BrowseSectionDomainModel>.withFavoriteFlags(
    favoriteIds: Set<Pair<WorkType, Int>>,
) = map { section ->
    when (section) {
        is BrowseSectionDomainModel.TopContent -> section.copy(works = section.works.withFavoriteStatus(favoriteIds))
        is BrowseSectionDomainModel.GenreContent -> section.copy(works = section.works.withFavoriteStatus(favoriteIds))
    }
}

private fun List<WorkDomainModel>.withFavoriteStatus(
    favoriteIds: Set<Pair<WorkType, Int>>,
) = map { work ->
    work.copy(isFavorite = (work.type.toWorkType() to work.id) in favoriteIds)
}

private fun List<ContentSection>.withFavoriteFlagsInPresentation(
    favoriteIds: Set<Pair<WorkType, Int>>,
) = map { section ->
    when (section) {
        is ContentSection.TopContent -> section.copy(works = section.works.withFavoriteWorkFlags(favoriteIds))
        is ContentSection.Genre -> section.copy(works = section.works.withFavoriteWorkFlags(favoriteIds))
    }
}

private fun List<WorkViewModel>.withFavoriteWorkFlags(
    favoriteIds: Set<Pair<WorkType, Int>>,
) = map { work ->
    work.copy(isFavorite = (work.type to work.id) in favoriteIds)
}

private fun WorkDomainModel.Type.toWorkType() =
    when (this) {
        WorkDomainModel.Type.MOVIE -> WorkType.MOVIE
        WorkDomainModel.Type.TV_SHOW -> WorkType.TV_SHOW
    }
