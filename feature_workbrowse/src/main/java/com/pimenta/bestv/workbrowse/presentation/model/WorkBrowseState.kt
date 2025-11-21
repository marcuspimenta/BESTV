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

package com.pimenta.bestv.workbrowse.presentation.model

import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.workbrowse.R as workbrowseR
import com.pimenta.bestv.presentation.R as presentationR
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.State.Loading

/**
 * Represents the state of the Work Browse screen.
 */
data class WorkBrowseState(
    val state: State = Loading(false)
) {
    sealed interface State {
        data class Loading(val isSplashAnimationFinished: Boolean) : State
        data object Error : State
        data class Loaded(
            val workSelected: WorkViewModel?,
            val selectedSectionIndex: Int,
            val sections: List<Section>
        ) : State
    }

    sealed class Section(
        open val iconRes: Int,
        open val titleRes: Int
    ) {
        data object Search : Section(presentationR.drawable.search, workbrowseR.string.search)
        data class Movies(
            val content: List<ContentSection>
        ) : Section(presentationR.drawable.movie, presentationR.string.movies)
        data class TvShows(
            val content: List<ContentSection>
        ) : Section(presentationR.drawable.tv, presentationR.string.tv_shows)
        data class Favorites(
            val content: List<ContentSection>
        ) : Section(presentationR.drawable.favorite, workbrowseR.string.favorites)
        data object About : Section(presentationR.drawable.info, workbrowseR.string.about)
    }
}

sealed class ContentSection(
    open val works: List<WorkViewModel>,
    open val page: PaginationState
) {

    data class TopContent(
        val type: TopWorkTypeViewModel,
        override val works: List<WorkViewModel>,
        override val page: PaginationState
    ) : ContentSection(works, page)

    data class Genre(
        val genreViewModel: GenreViewModel,
        override val works: List<WorkViewModel>,
        override val page: PaginationState
    ) : ContentSection(works, page)
}

data class PaginationState(
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val isLoadingMore: Boolean = false
) {
    val canLoadMore: Boolean
        get() = currentPage in 1..<totalPages && !isLoadingMore
}