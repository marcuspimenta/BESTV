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
data class TvShowResponse(
    @SerialName("name") override var title: String? = null,
    @SerialName("original_name") override var originalTitle: String? = null,
    @SerialName("first_air_date") var firstAirDate: String? = null,
    @SerialName("keywords") val keywords: TvShowKeywordsResponse? = null,
) : WorkResponse() {

    override var releaseDate: String?
        get() {
            return firstAirDate
        }
        set(value) {
            firstAirDate = value
        }
}
