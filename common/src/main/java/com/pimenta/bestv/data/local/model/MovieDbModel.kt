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

package com.pimenta.bestv.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie")
data class MovieDbModel(
    @PrimaryKey val id: Int,
    val title: String?,
    val originalTitle: String?,
    val releaseDate: String?,
    val originalLanguage: String?,
    val overview: String?,
    val source: String,
    val backdropPath: String?,
    val posterPath: String?,
    val popularity: Float,
    val voteAverage: Float,
    val voteCount: Float,
    val isAdult: Boolean,
    val isFavorite: Boolean,
) {
    companion object {
        fun favoritePlaceholder(id: Int) =
            MovieDbModel(
                id = id,
                title = null,
                originalTitle = null,
                releaseDate = null,
                originalLanguage = null,
                overview = null,
                source = "TMDB",
                backdropPath = null,
                posterPath = null,
                popularity = 0f,
                voteAverage = 0f,
                voteCount = 0f,
                isAdult = false,
                isFavorite = true,
            )
    }
}
