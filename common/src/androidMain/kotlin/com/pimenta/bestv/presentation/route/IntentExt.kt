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

package com.pimenta.bestv.presentation.route

import android.content.Intent
import androidx.navigation3.runtime.NavKey
import com.pimenta.bestv.presentation.model.CastViewModel
import com.pimenta.bestv.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.route.WorkDetailsDestination
import com.pimenta.bestv.presentation.route.bestvDestinationFromUri
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal inline fun <reified T> Intent.jsonExtra(name: String): T? =
    getStringExtra(name)?.let { serialized ->
        runCatching { Json.decodeFromString<T>(serialized) }.getOrNull()
    }

internal inline fun <reified T> Intent.putJsonExtra(name: String, value: T): Intent =
    putExtra(name, Json.encodeToString(value))

fun Intent.toBestvDestination(): NavKey? {
    val uri = data?.toString()
    val uriDestination = uri?.let(::bestvDestinationFromUri)
    if (uriDestination != null) return uriDestination

    return when (data?.host) {
        WORK_DETAILS_HOST -> jsonExtra<WorkViewModel>(WORK_EXTRA)?.let(WorkDetailsDestination::create)
        CAST_DETAILS_HOST -> jsonExtra<CastViewModel>(CAST_EXTRA)?.let { CastDetailsDestination.create(it.id) }
        else -> null
    }
}

private const val WORK_EXTRA = "WORK"
private const val CAST_EXTRA = "CAST"
