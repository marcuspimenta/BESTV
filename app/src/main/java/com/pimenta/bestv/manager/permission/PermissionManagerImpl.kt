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

package com.pimenta.bestv.manager.permission

import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by marcus on 04/07/18.
 */
class PermissionManagerImpl @Inject constructor(
        private val application: Application,
        private val permissions: MutableMap<String, Boolean>
) : PermissionManager {

    override fun hasAllPermissions(): Single<Boolean> =
            Single.fromCallable {
                var hasAllPermissions = true
                for (permission in permissions.keys) {
                    hasAllPermissions = hasAllPermissions and (ContextCompat.checkSelfPermission(application, permission) == PackageManager.PERMISSION_GRANTED)
                    permissions[permission] = hasAllPermissions
                }
                hasAllPermissions
            }

    override fun getPermissions(): Set<String> {
        return permissions.keys
    }
}