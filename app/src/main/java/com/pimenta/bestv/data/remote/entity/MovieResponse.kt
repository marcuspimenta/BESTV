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

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

/**
 * Created by marcus on 09-02-2018.
 */
@Entity(tableName = MovieResponse.TABLE)
class MovieResponse(
        id: Int = 0,
        isFavorite: Boolean = false,
        @SerializedName("title") override var title: String? = null,
        @SerializedName("original_title") override var originalTitle: String? = null,
        @SerializedName("release_date") var releaseDateString: String? = null
) : WorkResponse(id = id, isFavorite = isFavorite) {

    override var releaseDate: String?
        get() {
            return releaseDateString
        }
        set(value) {
            releaseDateString = value
        }

    companion object {

        const val TABLE = "movie"
    }
}