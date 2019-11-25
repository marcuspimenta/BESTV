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

package com.pimenta.bestv.feature.castdetail.presentation.processor

import android.content.Intent
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.route.castdetail.CastDetailsRoute
import javax.inject.Inject

/**
 * Created by marcus on 25-11-2019.
 */
class CastProcessor @Inject constructor() {

    operator fun invoke(intent: Intent): CastViewModel? {
        val castDeepLink = getCastDeepLink(intent)
        return when {
            castDeepLink != null -> castDeepLink
            else -> null
        }
    }

    private fun getCastDeepLink(intent: Intent) =
            intent.data?.let {
                if (it.pathSegments.first() == CastDetailsRoute.CAST) {
                    CastViewModel(
                            id = it.getQueryParameter(CastDetailsRoute.ID)?.toInt() ?: 1,
                            name = it.getQueryParameter(CastDetailsRoute.NAME),
                            character = it.getQueryParameter(CastDetailsRoute.CHARACTER),
                            birthday = it.getQueryParameter(CastDetailsRoute.BIRTHDAY),
                            deathDay = it.getQueryParameter(CastDetailsRoute.DEATH_DAY),
                            biography = it.getQueryParameter(CastDetailsRoute.BIOGRAPH),
                            thumbnailUrl = it.getQueryParameter(CastDetailsRoute.THUMBNAIL_URL)
                    )
                } else {
                    null
                }
            }
}