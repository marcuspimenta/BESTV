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
import com.pimenta.bestv.workdetail.presentation.model.ErrorType.LoadingError

/**
 * Represents the UI state for the Work Details screen.
 * This is the single source of truth for the screen's state.
 */
data class WorkDetailsState(
    val work: WorkViewModel,
    val state: State
) {
    sealed interface State {
        data object Loading : State
        data class Error(
            val error: ErrorType = LoadingError
        ): State
        data class Loaded(
            val indexOfContentToScroll: Int? = null,
            val contents: List<Content>,
            val error: ErrorType? = null
        ) : State
    }

    sealed interface Content {
        data class Header(
            val actions: List<ActionButton>
        ) : Content
        data class Videos(
            val videos: List<VideoViewModel>,
        ) : Content
        data class Casts(
            val casts: List<CastViewModel>,
        ) : Content
        data class RecommendedWorks(
            val recommended: List<WorkViewModel>,
            val page: PaginationState
        ) : Content
        data class SimilarWorks(
            val similar: List<WorkViewModel>,
            val page: PaginationState
        ) : Content
        data class Reviews(
            val reviews: List<ReviewViewModel>,
            val page: PaginationState
        ) : Content
    }

    sealed class ActionButton(open val title: String) {
        data class SaveWork(val isFavorite: Boolean) : ActionButton(if (isFavorite) "Unsave" else "Save")
        data object ScrollToVideos : ActionButton("Videos")
        data object ScrollToCasts : ActionButton("Casts")
        data object ScrollToRecommendedWorks : ActionButton("Recommended")
        data object ScrollToSimilarWorks : ActionButton("Similar")
        data object ScrollToReviews : ActionButton("Reviews")
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

sealed class ErrorType(open val message: String) {
    data object LoadingError : ErrorType("Could not load the work details. Please check your internet connection")
    data object FavoriteError : ErrorType("Could not save/unsave the work.")
    data object PaginationError : ErrorType("Could not load more content of this row. Please check your internet connection")
}
