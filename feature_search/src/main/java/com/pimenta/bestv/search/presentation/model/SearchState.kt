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

package com.pimenta.bestv.search.presentation.model

import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.search.presentation.model.SearchState.State.Empty

/**
 * Represents the UI state for the Search screen.
 * This is the single source of truth for the screen's state.
 */
data class SearchState(
    val query: String = "",
    val isSearching: Boolean = false,
    val state: State = Empty
) {
    sealed interface State {

        data object Empty : State
        data object Error : State
        data class Loaded(
            val selectedWork: WorkViewModel? = null,
            val contents: List<Content>
        ) : State {
            val hasResults: Boolean
                get() = contents.isNotEmpty()
        }
    }

    sealed class Content(open val query: String) {
        data class Movies(
            override val query: String,
            val movies: List<WorkViewModel>,
            val page: PaginationState
        ) : Content(query)

        data class TvShows(
            override val query: String,
            val tvShows: List<WorkViewModel>,
            val page: PaginationState
        ) : Content(query)
    }
}

data class PaginationState(
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val isLoadingMore: Boolean = false
) {
    val canLoadMore: Boolean
        get() = currentPage > 0 && currentPage < totalPages && !isLoadingMore
}
