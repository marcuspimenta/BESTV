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
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.ActionButton

sealed interface WorkDetailsEvent {

    data object LoadData : WorkDetailsEvent
    data class ActionButtonClicked(val action: ActionButton) : WorkDetailsEvent
    data object LoadMoreReviews : WorkDetailsEvent
    data object LoadMoreRecommendations : WorkDetailsEvent
    data object LoadMoreSimilar : WorkDetailsEvent
    data class WorkClicked(val work: WorkViewModel) : WorkDetailsEvent
    data class CastClicked(val cast: CastViewModel) : WorkDetailsEvent
    data class VideoClicked(val video: VideoViewModel) : WorkDetailsEvent
    data object DismissError : WorkDetailsEvent
}
