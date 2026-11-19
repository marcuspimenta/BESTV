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
 * Created by marcus on 15-02-2018.
 */
@Serializable
data class CastResponse(
    @SerialName("cast_id") var castId: Int = 0,
    @SerialName("credit_id") var creditId: String? = null,
    @SerialName("gender") var gender: Int = 0,
    @SerialName("id") var id: Int = 0,
    @SerialName("order") var order: Int = 0,
    @SerialName("name") var name: String? = null,
    @SerialName("character") var character: String? = null,
    @SerialName("profile_path") var profilePath: String? = null,
    @SerialName("birthday") var birthday: String? = null,
    @SerialName("deathday") var deathDay: String? = null,
    @SerialName("biography") var biography: String? = null,
    @SerialName("popularity") var popularity: Double? = null,
    @SerialName("place_of_birth") var placeOfBirth: String? = null
)
