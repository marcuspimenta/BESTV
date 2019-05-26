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

package com.pimenta.bestv.feature.workdetail.intent

import android.content.Intent
import com.pimenta.bestv.common.presentation.model.WorkType
import com.pimenta.bestv.common.presentation.model.WorkViewModel
import com.pimenta.bestv.common.setting.Const
import com.pimenta.bestv.feature.workdetail.ui.WorkDetailsFragment
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

    private fun getWorkIntent(intent: Intent): WorkViewModel? =
            intent.getSerializableExtra(WorkDetailsFragment.WORK) as? WorkViewModel

    private fun getWorkDeepLink(intent: Intent): WorkViewModel? =
            intent.data?.run {
                if (pathSegments.first() == Const.WORK) {
                    return WorkViewModel(
                            id = getQueryParameter(Const.ID)?.toInt() ?: 1,
                            title = getQueryParameter(Const.TITLE),
                            originalLanguage = getQueryParameter(Const.LANGUAGE),
                            overview = getQueryParameter(Const.OVERVIEW),
                            backdropUrl = getQueryParameter(Const.BACKGROUNG_URL),
                            posterUrl = getQueryParameter(Const.POSRTER_URL),
                            originalTitle = getQueryParameter(Const.ORIGINAL_TITLE),
                            releaseDate = getQueryParameter(Const.RELEASE_DATE),
                            isFavorite = getQueryParameter(Const.FAVORITE)?.toBoolean() ?: false,
                            type = WorkType.valueOf(getQueryParameter(Const.TYPE) ?: "MOVIE")
                    )
                } else {
                    return null
                }
            }

}