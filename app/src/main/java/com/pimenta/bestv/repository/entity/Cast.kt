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

package com.pimenta.bestv.repository.entity

import com.google.gson.annotations.SerializedName

import java.io.Serializable

/**
 * Created by marcus on 15-02-2018.
 */
class Cast : Serializable {

    @SerializedName("cast_id") var castId: Int = 0
    @SerializedName("credit_id") var creditId: String? = null
    @SerializedName("gender") var gender: Int = 0
    @SerializedName("id") var id: Int = 0
    @SerializedName("order") var order: Int = 0
    @SerializedName("name") var name: String? = null
    @SerializedName("character") var character: String? = null
    @SerializedName("profile_path") var profilePath: String? = null
    @SerializedName("birthday") var birthday: String? = null
    @SerializedName("deathday") var deathDay: String? = null
    @SerializedName("biography") var biography: String? = null
    @SerializedName("popularity") var popularity: Double? = null
    @SerializedName("place_of_birth") var placeOfBirth: String? = null
}