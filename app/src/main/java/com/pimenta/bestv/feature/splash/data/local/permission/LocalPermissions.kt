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

package com.pimenta.bestv.feature.splash.data.local.permission

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by marcus on 2019-08-28.
 */
private val PERMISSIONS = listOf(
        Manifest.permission.INTERNET,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.RECEIVE_BOOT_COMPLETED
)

class LocalPermissions @Inject constructor(
    private val application: Application
) {

    fun loadPermissionsNotAccepted(): Single<List<String>> =
            Single.fromCallable {
                val permissionsNotAccepted = mutableListOf<String>()
                PERMISSIONS.forEach {
                    val permissionDenied = (ContextCompat.checkSelfPermission(application, it) == PackageManager.PERMISSION_DENIED)
                    if (permissionDenied) {
                        permissionsNotAccepted.add(it)
                    }
                }
                permissionsNotAccepted
            }
}