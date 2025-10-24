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

/**
 * Represents the UI state for the Search screen.
 * This is the single source of truth for the screen's state.
 */
data class SearchState(
    val query: String = "",
    val isSearching: Boolean = false,
    val movies: List<WorkViewModel> = emptyList(),
    val tvShows: List<WorkViewModel> = emptyList(),
    val selectedWork: WorkViewModel? = null,
    val moviePagination: PaginationState = PaginationState(),
    val tvShowPagination: PaginationState = PaginationState(),
    val hasResults: Boolean = false,
    val error: String? = null
)

/**
 * Represents pagination state for movie and TV show lists
 */
data class PaginationState(
    val currentPage: Int = 0,
    val totalPages: Int = 0,
) {
    val hasMore: Boolean
        get() = currentPage > 0 && currentPage < totalPages
}
