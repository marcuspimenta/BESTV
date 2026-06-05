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
import com.pimenta.bestv.search.presentation.model.SearchState.State.Empty

fun clearSearch() = SearchState(query = "", isSearching = false, state = Empty)

fun create(
    query: String,
    moviePage: PageViewModel<WorkViewModel>,
    tvShowPage: PageViewModel<WorkViewModel>
): List<Content> = buildList {
    if (moviePage.results.isNotEmpty()) {
        add(
            Movies(
                query = query,
                movies = moviePage.results,
                page = moviePage.toPaginationState()
            )
        )
    }
    if (tvShowPage.results.isNotEmpty()) {
        add(
            TvShows(
                query = query,
                tvShows = tvShowPage.results,
                page = tvShowPage.toPaginationState()
            )
        )
    }
}

private fun PageViewModel<WorkViewModel>.toPaginationState() = PaginationState(
    currentPage = page,
    totalPages = totalPages
)
