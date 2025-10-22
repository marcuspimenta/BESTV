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

package com.pimenta.bestv.castdetail.presentation.model

import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.model.presentation.model.WorkViewModel

/**
 * Represents the state of the Cast Details screen
 *
 * Created by marcus on 21-10-2025.
 */
data class CastDetailsState(
    val cast: CastViewModel,
    val isLoading: Boolean = false,
    val castDetails: CastViewModel? = null,
    val movies: List<WorkViewModel> = emptyList(),
    val tvShows: List<WorkViewModel> = emptyList(),
)