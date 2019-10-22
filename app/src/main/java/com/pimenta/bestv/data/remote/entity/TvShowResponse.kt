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

package com.pimenta.bestv.data.remote.entity

import com.google.gson.annotations.SerializedName

/**
 * Created by marcus on 06/07/18.
 */
class TvShowResponse(
        id: Int = 0,
        isFavorite: Boolean = false,
        @SerializedName("name") override var title: String? = null,
        @SerializedName("original_name") override var originalTitle: String? = null,
        @SerializedName("first_air_date") var firstAirDate: String? = null
) : WorkResponse(id = id, isFavorite = isFavorite) {

    override var releaseDate: String?
        get() {
            return firstAirDate
        }
        set(value) {
            firstAirDate = value
        }
}