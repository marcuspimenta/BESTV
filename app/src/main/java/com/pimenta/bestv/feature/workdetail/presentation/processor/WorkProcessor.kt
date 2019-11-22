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

package com.pimenta.bestv.feature.workdetail.presentation.processor

import android.content.Intent
import com.pimenta.bestv.feature.workdetail.presentation.ui.activity.WorkDetailsActivity
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.workdetail.Route
import javax.inject.Inject

/**
 * Created by marcus on 21-05-2019.
 */
class WorkProcessor @Inject constructor() {

    operator fun invoke(intent: Intent): WorkViewModel? {
        val workIntent = getWorkIntent(intent)
        val workDeepLink = getWorkDeepLink(intent)
        return when {
            workIntent != null -> workIntent
            workDeepLink != null -> workDeepLink
            else -> null
        }
    }

    private fun getWorkIntent(intent: Intent) =
            intent.getSerializableExtra(WorkDetailsActivity.WORK) as? WorkViewModel

    private fun getWorkDeepLink(intent: Intent) =
            intent.data?.let {
                if (it.pathSegments.first() == Route.WORK) {
                    WorkViewModel(
                            id = it.getQueryParameter(Route.ID)?.toInt() ?: 1,
                            title = it.getQueryParameter(Route.TITLE),
                            originalLanguage = it.getQueryParameter(Route.LANGUAGE),
                            overview = it.getQueryParameter(Route.OVERVIEW),
                            backdropUrl = it.getQueryParameter(Route.BACKGROUND_URL),
                            posterUrl = it.getQueryParameter(Route.POSTER_URL),
                            originalTitle = it.getQueryParameter(Route.ORIGINAL_TITLE),
                            releaseDate = it.getQueryParameter(Route.RELEASE_DATE),
                            isFavorite = it.getQueryParameter(Route.FAVORITE)?.toBoolean() ?: false,
                            type = WorkType.valueOf(it.getQueryParameter(Route.TYPE) ?: "MOVIE")
                    )
                } else {
                    null
                }
            }
}