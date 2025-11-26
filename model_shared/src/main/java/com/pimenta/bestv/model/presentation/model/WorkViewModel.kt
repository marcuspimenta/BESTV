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

package com.pimenta.bestv.model.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by marcus on 18-04-2019.
 */
@Parcelize
data class WorkViewModel(
    val id: Int,
    val originalLanguage: String,
    val overview: String,
    val source: String,
    val backdropUrl: String,
    val posterUrl: String,
    val title: String,
    val originalTitle: String,
    val releaseDate: String,
    val type: WorkType,
    val voteAverage: Float,
    val isFavorite: Boolean = false,
) : Parcelable

enum class WorkType {
    TV_SHOW,
    MOVIE
}
