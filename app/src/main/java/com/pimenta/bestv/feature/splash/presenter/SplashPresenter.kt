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

import com.pimenta.bestv.feature.base.BasePresenter
import com.pimenta.bestv.feature.splash.presenter.SplashPresenter.View
import com.pimenta.bestv.manager.PermissionManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by marcus on 04-05-2018.
 */
class SplashPresenter @Inject constructor(
        private val mPermissionManager: PermissionManager
) : BasePresenter<View>() {

    /**
     * Loads all permissions
     */
    fun loadPermissions() {
        compositeDisposable.add(mPermissionManager.hasAllPermissions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .delay(SPLASH_TIME_LOAD_SECONDS.toLong(), TimeUnit.SECONDS)
                .subscribe({ result ->
                    if (result) {
                        view.onSplashFinished(true)
                    } else {
                        view.onPermissionsLoaded(mPermissionManager.permissions)
                    }
                }, { throwable ->
                    Timber.e(throwable, "Error while loading permissions")
                    view.onSplashFinished(false)
                }))
    }

    /**
     * Verifies if has all permissions
     */
    fun hasAllPermissions() {
        compositeDisposable.add(mPermissionManager.hasAllPermissions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    view.onSplashFinished(result)
                }, { throwable ->
                    Timber.e(throwable, "Error while checking if has all permissions")
                    view.onSplashFinished(false)
                }))
    }

    interface View : BasePresenter.BaseView {

        fun onSplashFinished(success: Boolean)

        fun onPermissionsLoaded(permissions: Set<String>)

    }

    companion object {

        private const val SPLASH_TIME_LOAD_SECONDS = 3

    }
}