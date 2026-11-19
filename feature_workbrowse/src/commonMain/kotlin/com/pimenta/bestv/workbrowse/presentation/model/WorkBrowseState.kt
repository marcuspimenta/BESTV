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

import com.pimenta.bestv.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.ui.compose.LazyContentTypes
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.State.Loading
import com.pimenta.bestv.common.resources.Res as CommonRes
import com.pimenta.bestv.workbrowse.resources.Res as workbrowseR
import com.pimenta.bestv.common.resources.favorite
import com.pimenta.bestv.common.resources.movie
import com.pimenta.bestv.common.resources.movies_title
import com.pimenta.bestv.common.resources.search as commonSearchDrawable
import com.pimenta.bestv.common.resources.tv
import com.pimenta.bestv.common.resources.tv_shows_title
import com.pimenta.bestv.workbrowse.resources.favorites
import com.pimenta.bestv.workbrowse.resources.search as workBrowseSearchString

/**
 * Represents the state of the Work Browse screen.
 */
data class WorkBrowseState(
    val isMobileDevice: Boolean = false,
    val latestFavorites: List<WorkViewModel> = emptyList(),
    val state: State = Loading(false),
) {
    sealed interface State {
        data class Loading(
            val isSplashAnimationFinished: Boolean,
        ) : State

        data object Error : State

        data class Loaded(
            val workSelected: WorkViewModel?,
            val selectedSectionIndex: Int,
            val sections: List<Section>,
        ) : State
    }

    sealed class Section(
        open val iconRes: DrawableResource,
        open val titleRes: StringResource,
    ) {
        data object Search : Section(CommonRes.drawable.commonSearchDrawable, workbrowseR.string.workBrowseSearchString)

        data class Movies(
            val content: List<ContentSection>,
        ) : Section(CommonRes.drawable.movie, CommonRes.string.movies_title)

        data class TvShows(
            val content: List<ContentSection>,
        ) : Section(CommonRes.drawable.tv, CommonRes.string.tv_shows_title)

        data class Favorites(
            val content: List<ContentSection>,
        ) : Section(CommonRes.drawable.favorite, workbrowseR.string.favorites)
    }
}

sealed class ContentSection(
    open val works: List<WorkViewModel>,
) {
    data class TopContent(
        val type: TopWorkTypeViewModel,
        override val works: List<WorkViewModel>,
    ) : ContentSection(works)

    data class Genre(
        val genreViewModel: GenreViewModel,
        override val works: List<WorkViewModel>,
    ) : ContentSection(works)
}

internal fun ContentSection.lazyListKey(): String = when (this) {
    is ContentSection.Genre -> "${LazyContentTypes.GENRE_KEY_PREFIX}${genreViewModel.source.name}:${genreViewModel.id}"
    is ContentSection.TopContent -> "${LazyContentTypes.TOP_KEY_PREFIX}${type.name}"
}

internal fun ContentSection.lazyContentType(): String = when (this) {
    is ContentSection.Genre -> LazyContentTypes.GENRE
    is ContentSection.TopContent -> LazyContentTypes.TOP_CONTENT
}
