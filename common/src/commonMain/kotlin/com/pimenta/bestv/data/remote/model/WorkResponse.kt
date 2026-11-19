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

package com.pimenta.bestv.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by marcus on 06/07/18.
 */
@Serializable
abstract class WorkResponse(
    @SerialName("id") val id: Int = 0,
    @SerialName("original_language") val originalLanguage: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("overview") val overview: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("popularity") val popularity: Float = 0f,
    @SerialName("vote_average") val voteAverage: Float = 0f,
    @SerialName("vote_count") val voteCount: Float = 0f,
    @SerialName("adult") val isAdult: Boolean = false
) {

    abstract val title: String?

    abstract val originalTitle: String?

    abstract val releaseDate: String?
}
