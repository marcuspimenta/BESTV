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
import androidx.core.net.toUri
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.route.BESTV_SCHEME
import com.pimenta.bestv.route.WORK_DETAILS_HOST
import com.pimenta.bestv.route.parcelableExtra
import com.pimenta.bestv.route.WorkDetailsDestination

/**
 * Created by marcus on 22-11-2019.
 */
private const val SCHEMA_URI = "$BESTV_SCHEME://$WORK_DETAILS_HOST/"
private const val WORK = "WORK"

class WorkDetailsRoute(
    private val application: Application,
) {

    fun buildWorkDetailsDestination(workViewModel: WorkViewModel) = WorkDetailsDestination.create(workViewModel)

    fun buildWorkDetailIntent(workViewModel: WorkViewModel) =
        Intent(Intent.ACTION_VIEW, SCHEMA_URI.toUri()).apply {
            putExtra(WORK, workViewModel)
            setPackage(application.packageName)
        }
}

fun Intent.getWorkDetail() = parcelableExtra<WorkViewModel>(WORK)
