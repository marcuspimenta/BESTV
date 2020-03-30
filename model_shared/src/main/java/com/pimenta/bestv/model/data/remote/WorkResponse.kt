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

package com.pimenta.bestv.model.data.remote

import com.google.gson.annotations.SerializedName

/**
 * Created by marcus on 06/07/18.
 */
abstract class WorkResponse(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("original_language") var originalLanguage: String? = null,
    @SerializedName("overview") var overview: String? = null,
    @SerializedName("backdrop_path") var backdropPath: String? = null,
    @SerializedName("poster_path") var posterPath: String? = null,
    @SerializedName("popularity") var popularity: Float = 0f,
    @SerializedName("vote_average") var voteAverage: Float = 0f,
    @SerializedName("vote_count") var voteCount: Float = 0f,
    @SerializedName("adult") var isAdult: Boolean = false
) {

    abstract var title: String?

    abstract var originalTitle: String?

    abstract var releaseDate: String?
}
