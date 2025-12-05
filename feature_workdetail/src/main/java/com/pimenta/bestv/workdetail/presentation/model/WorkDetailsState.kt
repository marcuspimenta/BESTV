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
import com.pimenta.bestv.presentation.model.PaginationState
import com.pimenta.bestv.workdetail.presentation.model.ErrorType.LoadingError

private const val ID_HEADER = 1
private const val ID_VIDEOS = 2
private const val ID_CASTS = 3
private const val ID_RECOMMENDED_WORKS = 4
private const val ID_SIMILAR_WORKS = 5
private const val ID_REVIEWS = 6

private const val ID_ACTION_SAVE_WORK = 1
private const val ID_ACTION_SCROLL_TO_VIDEOS = 2
private const val ID_ACTION_SCROLL_TO_CASTS = 3
private const val ID_ACTION_SCROLL_TO_RECOMMENDED = 4
private const val ID_ACTION_SCROLL_TO_SIMILAR = 5
private const val ID_ACTION_SCROLL_TO_REVIEWS = 6

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

    sealed class Content(open val id: Int) {
        data class Header(
            val actions: List<ActionButton>,
            val watchProviders: WatchProvidersViewModel?
        ) : Content(ID_HEADER)

        data class Videos(
            val videos: List<VideoViewModel>,
        ) : Content(ID_VIDEOS)

        data class Casts(
            val casts: List<CastViewModel>,
        ) : Content(ID_CASTS)

        data class RecommendedWorks(
            val recommended: List<WorkViewModel>,
            val page: PaginationState
        ) : Content(ID_RECOMMENDED_WORKS)

        data class SimilarWorks(
            val similar: List<WorkViewModel>,
            val page: PaginationState
        ) : Content(ID_SIMILAR_WORKS)

        data class Reviews(
            val reviews: List<ReviewViewModel>,
            val page: PaginationState
        ) : Content(ID_REVIEWS)
    }

    sealed class ActionButton(open val id: Int, open val title: String) {
        data class SaveWork(val isFavorite: Boolean) : ActionButton(ID_ACTION_SAVE_WORK, if (isFavorite) "Unsave" else "Save")
        data object ScrollToVideos : ActionButton(ID_ACTION_SCROLL_TO_VIDEOS, "Videos")
        data object ScrollToCasts : ActionButton(ID_ACTION_SCROLL_TO_CASTS, "Casts")
        data object ScrollToRecommendedWorks : ActionButton(ID_ACTION_SCROLL_TO_RECOMMENDED, "Recommended")
        data object ScrollToSimilarWorks : ActionButton(ID_ACTION_SCROLL_TO_SIMILAR, "Similar")
        data object ScrollToReviews : ActionButton(ID_ACTION_SCROLL_TO_REVIEWS, "Reviews")
    }
}

sealed class ErrorType(open val message: String) {
    data object LoadingError : ErrorType("Could not load the work details. Please check your internet connection")
    data object FavoriteError : ErrorType("Could not save/unsave the work.")
    data object PaginationError : ErrorType("Could not load more content of this row. Please check your internet connection")
    data object FailedToOpenYouTubeVideo : ErrorType("Failed to open the video. Check if you have YouTube installed and try again.")
}
