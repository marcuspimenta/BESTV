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

package com.pimenta.bestv.route.workdetail

import android.app.Application
import android.content.Intent
import android.net.Uri
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import javax.inject.Inject

/**
 * Created by marcus on 22-11-2019.
 */
private const val SCHEMA_URI_PREFIX = "bestv://workdetail/"
private const val WORK = "work"
private const val ID = "ID"
private const val LANGUAGE = "LANGUAGE"
private const val OVERVIEW = "OVERVIEW"
private const val SOURCE = "SOURCE"
private const val BACKGROUND_URL = "BACKGROUND_URL"
private const val POSTER_URL = "POSTER_URL"
private const val TITLE = "TITLE"
private const val ORIGINAL_TITLE = "ORIGINAL_TITLE"
private const val RELEASE_DATE = "RELEASE_DATE"
private const val FAVORITE = "FAVORITE"
private const val TYPE = "TYPE"

class WorkDetailsRoute @Inject constructor(
    private val application: Application
) {

    fun buildWorkDetailIntent(workViewModel: WorkViewModel) =
        Intent(Intent.ACTION_VIEW, workViewModel.toUri()).apply {
            setPackage(application.packageName)
        }

    private fun WorkViewModel.toUri(): Uri =
        Uri.parse(SCHEMA_URI_PREFIX.plus(WORK)).buildUpon()
            .appendQueryParameter(ID, id.toString())
            .appendQueryParameter(LANGUAGE, originalLanguage)
            .appendQueryParameter(OVERVIEW, overview)
            .appendQueryParameter(SOURCE, source)
            .appendQueryParameter(BACKGROUND_URL, backdropUrl)
            .appendQueryParameter(POSTER_URL, posterUrl)
            .appendQueryParameter(TITLE, title)
            .appendQueryParameter(ORIGINAL_TITLE, originalTitle)
            .appendQueryParameter(RELEASE_DATE, releaseDate)
            .appendQueryParameter(FAVORITE, isFavorite.toString())
            .appendQueryParameter(TYPE, type.toString())
            .build()
}

fun Intent.getWorkDetail() = data
    ?.takeIf { it.pathSegments.first() == WORK }
    ?.let {
        WorkViewModel(
            id = it.getQueryParameter(ID)?.toInt() ?: 1,
            title = it.getQueryParameter(TITLE).orEmpty(),
            originalLanguage = it.getQueryParameter(LANGUAGE).orEmpty(),
            overview = it.getQueryParameter(OVERVIEW).orEmpty(),
            source = it.getQueryParameter(SOURCE).orEmpty(),
            backdropUrl = it.getQueryParameter(BACKGROUND_URL).orEmpty(),
            posterUrl = it.getQueryParameter(POSTER_URL).orEmpty(),
            originalTitle = it.getQueryParameter(ORIGINAL_TITLE).orEmpty(),
            releaseDate = it.getQueryParameter(RELEASE_DATE).orEmpty(),
            isFavorite = it.getQueryParameter(FAVORITE)?.toBoolean() ?: false,
            type = WorkType.valueOf(it.getQueryParameter(TYPE) ?: "MOVIE")
        )
    }
