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

package com.pimenta.bestv.model.presentation.mapper

import com.pimenta.bestv.BuildConfig
import com.pimenta.bestv.model.data.local.MovieDbModel
import com.pimenta.bestv.model.data.local.TvShowDbModel
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import java.text.SimpleDateFormat
import java.util.*

fun WorkViewModel.toMovieDbModel() =
        MovieDbModel(id = id)

fun WorkViewModel.toTvShowDbModel() =
        TvShowDbModel(id = id)

fun WorkDomainModel.toViewModel() = WorkViewModel(
        id = id,
        title = title,
        originalLanguage = originalLanguage,
        overview = overview,
        backdropUrl = backdropPath?.let { String.format(BuildConfig.TMDB_LOAD_IMAGE_BASE_URL, it) },
        posterUrl = posterPath?.let { String.format(BuildConfig.TMDB_LOAD_IMAGE_BASE_URL, it) },
        originalTitle = originalTitle,
        releaseDate = releaseDate?.takeUnless { it.isEmpty() || it.isBlank() }
                ?.let { releaseDate ->
                    SimpleDateFormat("MMM dd, yyyy", Locale.US).format(
                            SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(releaseDate) as Date
                    )
                },
        isFavorite = isFavorite,
        type = WorkType.TV_SHOW.takeIf { type == WorkDomainModel.Type.TV_SHOW } ?: WorkType.MOVIE
)