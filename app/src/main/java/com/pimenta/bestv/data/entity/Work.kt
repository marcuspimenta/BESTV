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

package com.pimenta.bestv.data.entity

import com.google.gson.annotations.SerializedName
import com.j256.ormlite.field.DatabaseField
import java.io.Serializable

/**
 * Created by marcus on 06/07/18.
 */
abstract class Work(
        @DatabaseField(id = true, columnName = "id") @SerializedName("id") var id: Int = 0,
        @SerializedName("original_language") var originalLanguage: String? = null,
        @SerializedName("overview") var overview: String? = null,
        @SerializedName("backdrop_path") var backdropPath: String? = null,
        @SerializedName("poster_path") var posterPath: String? = null,
        @SerializedName("popularity") var popularity: Float = 0.toFloat(),
        @SerializedName("vote_average") var voteAverage: Float = 0.toFloat(),
        @SerializedName("vote_count") var voteCount: Float = 0.toFloat(),
        @SerializedName("adult") var isAdult: Boolean = false,
        var isFavorite: Boolean = false
) : Serializable {

    abstract var title: String?

    abstract var originalTitle: String?

    abstract var releaseDate: String?

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }

        val that = other as Work?

        return id == that?.id
    }

    override fun hashCode() = id.hashCode()
}