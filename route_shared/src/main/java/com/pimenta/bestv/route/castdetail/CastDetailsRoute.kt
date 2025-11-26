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

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import com.pimenta.bestv.model.presentation.model.CastViewModel
import javax.inject.Inject

/**
 * Created by marcus on 25-11-2019.
 */
private const val SCHEMA_URI = "bestv://castdetail/"
private const val CAST = "CAST"

class CastDetailsRoute @Inject constructor(
    private val application: Application,
) {

    fun buildCastDetailIntent(castViewModel: CastViewModel) =
        Intent(Intent.ACTION_VIEW, SCHEMA_URI.toUri()).apply {
            putExtra(CAST, castViewModel)
            setPackage(application.packageName)
        }
}

fun Intent.getCastDeepLink() = getParcelableExtra<CastViewModel>(CAST)