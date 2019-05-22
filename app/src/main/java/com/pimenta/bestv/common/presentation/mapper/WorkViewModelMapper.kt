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

package com.pimenta.bestv.common.presentation.mapper

import com.pimenta.bestv.BuildConfig
import com.pimenta.bestv.common.presentation.model.WorkType
import com.pimenta.bestv.common.presentation.model.WorkViewModel
import com.pimenta.bestv.data.entity.Movie
import com.pimenta.bestv.data.entity.TvShow
import com.pimenta.bestv.data.entity.Work
import io.reactivex.Single
import java.text.SimpleDateFormat
import java.util.*

fun Work.toViewModel() = WorkViewModel(
        id = id,
        title = title,
        originalLanguage = originalLanguage,
        overview = overview,
        backdropUrl = String.format(BuildConfig.TMDB_LOAD_IMAGE_BASE_URL, backdropPath),
        posterUrl = String.format(BuildConfig.TMDB_LOAD_IMAGE_BASE_URL, posterPath),
        originalTitle = originalTitle,
        releaseDate = releaseDate
                ?.takeUnless { it.isEmpty() || it.isBlank() }
                ?.let {
                    SimpleDateFormat("MMM dd, yyyy", Locale.US).format(
                            SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(it)
                    )
                },
        isFavorite = isFavorite,
        type = if (this is TvShow) WorkType.TV_SHOW else WorkType.MOVIE
)

fun WorkViewModel.toWork() = when (type) {
    WorkType.MOVIE -> Movie(id = id, isFavorite = isFavorite)
    WorkType.TV_SHOW -> TvShow(id = id, isFavorite = isFavorite)
}

fun Work.toSingle() = Single.fromCallable { this }