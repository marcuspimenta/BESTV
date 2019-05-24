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

import com.pimenta.bestv.common.mvp.AutoDisposablePresenter
import com.pimenta.bestv.extension.addTo
import com.pimenta.bestv.manager.permission.PermissionManager
import com.pimenta.bestv.scheduler.RxScheduler
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by marcus on 04-05-2018.
 */
class SplashPresenter @Inject constructor(
        private val view: View,
        private val permissionManager: PermissionManager,
        private val rxScheduler: RxScheduler
) : AutoDisposablePresenter() {

    fun loadPermissions() {
        permissionManager.hasAllPermissions()
                .subscribeOn(rxScheduler.ioScheduler)
                .observeOn(rxScheduler.mainScheduler)
                .delay(SPLASH_TIME_LOAD_SECONDS.toLong(), TimeUnit.SECONDS)
                .subscribe({ result ->
                    if (result) {
                        view.onSplashFinished(true)
                    } else {
                        view.onPermissionsLoaded(permissionManager.getPermissions())
                    }
                }, { throwable ->
                    Timber.e(throwable, "Error while loading permissions")
                    view.onSplashFinished(false)
                }).addTo(compositeDisposable)
    }

    fun hasAllPermissions() {
        permissionManager.hasAllPermissions()
                .subscribeOn(rxScheduler.ioScheduler)
                .observeOn(rxScheduler.mainScheduler)
                .subscribe({ result ->
                    view.onSplashFinished(result)
                }, { throwable ->
                    Timber.e(throwable, "Error while checking if has all permissions")
                    view.onSplashFinished(false)
                }).addTo(compositeDisposable)
    }

    interface View {

        fun onSplashFinished(success: Boolean)

        fun onPermissionsLoaded(permissions: Set<String>)

    }

    companion object {

        private const val SPLASH_TIME_LOAD_SECONDS = 3

    }
}