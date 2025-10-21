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

package com.pimenta.bestv.workdetail.presentation.model

import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.model.presentation.model.WorkViewModel

/**
 * Represents the UI state for the Work Details screen.
 * This is the single source of truth for the screen's state.
 */
data class WorkDetailsState(
    val work: WorkViewModel,
    val isLoading: Boolean = false,
    val isFavorite: Boolean = false,
    val reviews: List<ReviewViewModel> = emptyList(),
    val videos: List<VideoViewModel> = emptyList(),
    val casts: List<CastViewModel> = emptyList(),
    val recommendedWorks: List<WorkViewModel> = emptyList(),
    val similarWorks: List<WorkViewModel> = emptyList(),
    val reviewPagination: PaginationState = PaginationState(),
    val recommendedPagination: PaginationState = PaginationState(),
    val similarPagination: PaginationState = PaginationState(),
    val error: ErrorState? = null
)

/**
 * Represents pagination state for lists
 */
data class PaginationState(
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val isLoadingMore: Boolean = false
) {
    val hasMore: Boolean
        get() = currentPage > 0 && currentPage < totalPages

    val canLoadMore: Boolean
        get() = hasMore && !isLoadingMore
}

/**
 * Represents different error states
 */
sealed class ErrorState {
    data class LoadingError(val message: String) : ErrorState()
    data class FavoriteError(val message: String) : ErrorState()
    data class PaginationError(val message: String) : ErrorState()
}
