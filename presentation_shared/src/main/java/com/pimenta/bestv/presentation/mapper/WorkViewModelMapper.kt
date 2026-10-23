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

package com.pimenta.bestv.presentation.mapper

import com.pimenta.bestv.model.domain.ContentSource
import com.pimenta.bestv.model.domain.WorkDomainModel
import com.pimenta.bestv.model.presentation.mapper.TmdbImageUrlBuilder
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.R
import com.pimenta.bestv.presentation.platform.Resource
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun WorkDomainModel.toViewModel(resource: Resource): WorkViewModel? {
    val displayTitle = title ?: return null
    val displayLanguage = originalLanguage ?: return null
    val displayOverview = overview ?: return null
    val displayBackdropPath = backdropPath ?: return null
    val displayPosterPath = posterPath ?: return null
    val displayOriginalTitle = originalTitle ?: return null
    val displayReleaseDate = releaseDate ?: return null
    val formattedReleaseDate = displayReleaseDate
        .takeUnless { it.isEmpty() || it.isBlank() }
        ?.let { releaseDate ->
            SimpleDateFormat("MMM dd, yyyy", Locale.US).format(
                SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(releaseDate) as Date
            )
        } ?: return null

    return WorkViewModel(
        id = id,
        title = displayTitle,
        originalLanguage = displayLanguage,
        overview = displayOverview,
        source = when (source) {
            ContentSource.TMDB -> resource.getStringResource(R.string.source_tmdb)
        },
        backdropUrl = TmdbImageUrlBuilder.backdrop(displayBackdropPath),
        backdropCardUrl = TmdbImageUrlBuilder.backdropCard(displayBackdropPath),
        posterUrl = TmdbImageUrlBuilder.poster(displayPosterPath),
        originalTitle = displayOriginalTitle,
        releaseDate = formattedReleaseDate,
        voteAverage = voteAverage,
        type = WorkType.TV_SHOW.takeIf { type == WorkDomainModel.Type.TV_SHOW } ?: WorkType.MOVIE,
        isFavorite = isFavorite,
    )
}
