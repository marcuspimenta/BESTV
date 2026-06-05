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

package com.pimenta.bestv.search.presentation.viewmodel

import com.pimenta.bestv.model.presentation.model.PageViewModel
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.model.PaginationState
import com.pimenta.bestv.search.presentation.model.SearchState
import com.pimenta.bestv.search.presentation.model.SearchState.Content
import com.pimenta.bestv.search.presentation.model.SearchState.Content.Movies
import com.pimenta.bestv.search.presentation.model.SearchState.Content.TvShows
import com.pimenta.bestv.search.presentation.model.SearchState.State.Error
import com.pimenta.bestv.search.presentation.model.SearchState.State.Loaded

fun SearchState.searchStarted(query: String) = copy(
    query = query,
    isSearching = true
)

fun SearchState.searchLoaded(contents: List<Content>) = copy(
    isSearching = false,
    state = Loaded(contents = contents)
)

fun SearchState.searchFailed() = copy(
    isSearching = false,
    state = Error
)

fun SearchState.moviesPaginationStarted() = updateLoadedContent { content ->
    if (content is Movies) {
        content.copy(page = content.page.copy(isLoadingMore = true))
    } else {
        content
    }
}

fun SearchState.moviesPaginationSucceeded(
    moviePage: PageViewModel<WorkViewModel>
) = updateLoadedContent { content ->
    if (content is Movies) {
        content.copy(
            movies = content.movies + moviePage.results,
            page = PaginationState(
                currentPage = moviePage.page,
                totalPages = moviePage.totalPages,
                isLoadingMore = false
            )
        )
    } else {
        content
    }
}

fun SearchState.moviesPaginationFailed() = updateLoadedContent { content ->
    if (content is Movies) {
        content.copy(page = content.page.copy(isLoadingMore = false))
    } else {
        content
    }
}

fun SearchState.tvShowsPaginationStarted() = updateLoadedContent { content ->
    if (content is TvShows) {
        content.copy(page = content.page.copy(isLoadingMore = true))
    } else {
        content
    }
}

fun SearchState.tvShowsPaginationSucceeded(
    tvShowPage: PageViewModel<WorkViewModel>
) = updateLoadedContent { content ->
    if (content is TvShows) {
        content.copy(
            tvShows = content.tvShows + tvShowPage.results,
            page = PaginationState(
                currentPage = tvShowPage.page,
                totalPages = tvShowPage.totalPages,
                isLoadingMore = false
            )
        )
    } else {
        content
    }
}

fun SearchState.tvShowsPaginationFailed() = updateLoadedContent { content ->
    if (content is TvShows) {
        content.copy(page = content.page.copy(isLoadingMore = false))
    } else {
        content
    }
}

fun SearchState.workSelected(work: WorkViewModel?) = updateLoadedState {
    copy(selectedWork = work)
}

private fun SearchState.updateLoadedState(transform: Loaded.() -> Loaded): SearchState {
    val loadedState = state as? Loaded ?: return this
    return copy(state = loadedState.transform())
}

private fun SearchState.updateLoadedContent(transform: (Content) -> Content): SearchState =
    updateLoadedState {
        copy(contents = contents.map(transform))
    }
