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

package com.pimenta.bestv.workdetail.presentation.processor

import android.content.Intent
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.route.workdetail.WorkDetailRoute
import javax.inject.Inject

/**
 * Created by marcus on 21-05-2019.
 */
class WorkProcessor @Inject constructor() {

    operator fun invoke(intent: Intent): WorkViewModel? {
        val workDeepLink = getWorkDeepLink(intent)
        return when {
            workDeepLink != null -> workDeepLink
            else -> null
        }
    }

    private fun getWorkDeepLink(intent: Intent) =
            intent.data?.let {
                if (it.pathSegments.first() == WorkDetailRoute.WORK) {
                    WorkViewModel(
                            id = it.getQueryParameter(WorkDetailRoute.ID)?.toInt() ?: 1,
                            title = it.getQueryParameter(WorkDetailRoute.TITLE),
                            originalLanguage = it.getQueryParameter(WorkDetailRoute.LANGUAGE),
                            overview = it.getQueryParameter(WorkDetailRoute.OVERVIEW),
                            backdropUrl = it.getQueryParameter(WorkDetailRoute.BACKGROUND_URL),
                            posterUrl = it.getQueryParameter(WorkDetailRoute.POSTER_URL),
                            originalTitle = it.getQueryParameter(WorkDetailRoute.ORIGINAL_TITLE),
                            releaseDate = it.getQueryParameter(WorkDetailRoute.RELEASE_DATE),
                            isFavorite = it.getQueryParameter(WorkDetailRoute.FAVORITE)?.toBoolean() ?: false,
                            type = WorkType.valueOf(it.getQueryParameter(WorkDetailRoute.TYPE) ?: "MOVIE")
                    )
                } else {
                    null
                }
            }
}