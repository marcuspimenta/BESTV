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

import com.pimenta.bestv.domain.model.WorkDomainModel
import com.pimenta.bestv.presentation.model.WorkType
import com.pimenta.bestv.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.platform.ContentSourceLabelProvider

private val releaseDateMonths = listOf(
    "Jan",
    "Feb",
    "Mar",
    "Apr",
    "May",
    "Jun",
    "Jul",
    "Aug",
    "Sep",
    "Oct",
    "Nov",
    "Dec",
)

private fun String.toDisplayReleaseDate(): String? {
    val dateParts = split('-')
    if (dateParts.size != 3) return null

    val year = dateParts[0].toIntOrNull() ?: return null
    val month = dateParts[1].toIntOrNull() ?: return null
    val day = dateParts[2].toIntOrNull() ?: return null
    if (month !in 1..12) return null

    val maxDay = when (month) {
        2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
        4, 6, 9, 11 -> 30
        else -> 31
    }
    if (day !in 1..maxDay) return null

    return "${releaseDateMonths[month - 1]} ${day.toString().padStart(2, '0')}, $year"
}

fun WorkDomainModel.toViewModel(
    contentSourceLabelProvider: ContentSourceLabelProvider,
): WorkViewModel? {
    val displayTitle = title ?: return null
    val displayLanguage = originalLanguage ?: return null
    val displayOverview = overview ?: return null
    val displayBackdropPath = backdropPath ?: return null
    val displayPosterPath = posterPath ?: return null
    val displayOriginalTitle = originalTitle ?: return null
    val displayReleaseDate = releaseDate ?: return null
    val formattedReleaseDate = displayReleaseDate.toDisplayReleaseDate() ?: return null

    return WorkViewModel(
        id = id,
        title = displayTitle,
        originalLanguage = displayLanguage,
        overview = displayOverview,
        source = contentSourceLabelProvider.labelFor(source),
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
