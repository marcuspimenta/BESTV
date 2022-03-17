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

package com.pimenta.bestv.route.castdetail

import android.content.Intent
import android.net.Uri
import com.pimenta.bestv.model.presentation.model.CastViewModel
import javax.inject.Inject

/**
 * Created by marcus on 25-11-2019.
 */
private const val SCHEMA_URI_PREFIX = "bestv://castdetail/"
private const val CAST = "cast"
private const val ID = "ID"
private const val NAME = "NAME"
private const val CHARACTER = "CHARACTER"
private const val BIRTHDAY = "BIRTHDAY"
private const val SOURCE = "SOURCE"
private const val DEATH_DAY = "DEATH_DAY"
private const val BIOGRAPHY = "BIOGRAPHY"
private const val THUMBNAIL_URL = "THUMBNAIL_URL"

class CastDetailsRoute @Inject constructor() {

    fun buildCastDetailIntent(castViewModel: CastViewModel) = Intent(Intent.ACTION_VIEW, castViewModel.toUri())

    fun getCastDetailDeepLink(intent: Intent) = intent.getCastDeepLink()

    private fun CastViewModel.toUri(): Uri =
        Uri.parse(SCHEMA_URI_PREFIX.plus(CAST)).buildUpon()
            .appendQueryParameter(ID, id.toString())
            .appendQueryParameter(NAME, name)
            .appendQueryParameter(CHARACTER, character)
            .appendQueryParameter(BIRTHDAY, birthday)
            .appendQueryParameter(SOURCE, source)
            .appendQueryParameter(DEATH_DAY, deathDay)
            .appendQueryParameter(BIOGRAPHY, biography)
            .appendQueryParameter(THUMBNAIL_URL, thumbnailUrl)
            .build()

    private fun Intent.getCastDeepLink() =
        data?.takeIf { it.pathSegments.first() == CAST }
            ?.let {
                CastViewModel(
                    id = it.getQueryParameter(ID)?.toInt() ?: 1,
                    name = it.getQueryParameter(NAME),
                    character = it.getQueryParameter(CHARACTER),
                    birthday = it.getQueryParameter(BIRTHDAY),
                    deathDay = it.getQueryParameter(DEATH_DAY),
                    biography = it.getQueryParameter(BIOGRAPHY),
                    source = it.getQueryParameter(SOURCE),
                    thumbnailUrl = it.getQueryParameter(THUMBNAIL_URL)
                )
            }
}
