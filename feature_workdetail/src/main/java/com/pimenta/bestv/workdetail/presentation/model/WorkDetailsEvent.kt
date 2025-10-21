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
 * Represents user intents/actions in the Work Details screen.
 * These are the events that the user can trigger.
 */
sealed interface WorkDetailsEvent {

    /**
     * Load initial data for the work details
     */
    data object LoadData : WorkDetailsEvent

    /**
     * Toggle favorite status of the work
     */
    data object ToggleFavorite : WorkDetailsEvent

    /**
     * User selected a review item (for pagination)
     */
    data class ReviewItemSelected(val review: ReviewViewModel) : WorkDetailsEvent

    /**
     * User selected a recommendation item (for pagination)
     */
    data class RecommendationItemSelected(val work: WorkViewModel) : WorkDetailsEvent

    /**
     * User selected a similar work item (for pagination)
     */
    data class SimilarItemSelected(val work: WorkViewModel) : WorkDetailsEvent

    /**
     * User clicked on a work item to view details
     */
    data class WorkClicked(val work: WorkViewModel) : WorkDetailsEvent

    /**
     * User clicked on a cast member to view details
     */
    data class CastClicked(val cast: CastViewModel) : WorkDetailsEvent

    /**
     * User clicked on a video to play
     */
    data class VideoClicked(val video: VideoViewModel) : WorkDetailsEvent

    /**
     * Dismiss the current error
     */
    data object DismissError : WorkDetailsEvent
}
