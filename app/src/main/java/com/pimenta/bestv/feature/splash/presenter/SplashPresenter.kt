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

package com.pimenta.bestv.feature.splash.presenter

import com.pimenta.bestv.common.extension.addTo
import com.pimenta.bestv.common.mvp.AutoDisposablePresenter
import com.pimenta.bestv.manager.permission.PermissionManager
import com.pimenta.bestv.scheduler.RxScheduler
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val SPLASH_TIME_LOAD_SECONDS = 4

/**
 * Created by marcus on 04-05-2018.
 */
class SplashPresenter @Inject constructor(
        private val view: View,
        private val permissionManager: PermissionManager,
        private val rxScheduler: RxScheduler
) : AutoDisposablePresenter() {

    fun loadPermissions() {
        permissionManager.loadPermissions()
                .subscribeOn(rxScheduler.ioScheduler)
                .observeOn(rxScheduler.mainScheduler)
                .delay(SPLASH_TIME_LOAD_SECONDS.toLong(), TimeUnit.SECONDS)
                .subscribe({ permissions ->
                    if (permissions.isNotEmpty()) {
                        view.onRequestPermissions(permissions)
                    } else {
                        view.onHasAllPermissions(true)
                    }
                }, { throwable ->
                    Timber.e(throwable, "Error while loading permissions")
                    view.onHasAllPermissions(false)
                }).addTo(compositeDisposable)
    }

    fun hasAllPermissions() {
        permissionManager.loadPermissions()
                .subscribeOn(rxScheduler.ioScheduler)
                .observeOn(rxScheduler.mainScheduler)
                .subscribe({ permissions ->
                    view.onHasAllPermissions(permissions.isEmpty())
                }, { throwable ->
                    Timber.e(throwable, "Error while checking if has all permissions")
                    view.onHasAllPermissions(false)
                }).addTo(compositeDisposable)
    }

    interface View {

        fun onHasAllPermissions(hasAllPermissions: Boolean)

        fun onRequestPermissions(permissions: List<String>)

    }

}