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

package com.pimenta.bestv.feature.splash.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pimenta.bestv.BesTV
import com.pimenta.bestv.R
import com.pimenta.bestv.feature.base.BaseFragment
import com.pimenta.bestv.feature.splash.presenter.SplashPresenter
import javax.inject.Inject

/**
 * Created by marcus on 04-05-2018.
 */
class SplashFragment : BaseFragment(), SplashPresenter.View {

    @Inject
    lateinit var presenter: SplashPresenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        BesTV.applicationComponent.getSplashFragmentComponent()
                .view(this)
                .build()
                .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.bindTo(this.lifecycle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.loadPermissions()
    }

    override fun onSplashFinished(success: Boolean) {
        val resultCode = Activity.RESULT_OK.takeIf { success } ?: Activity.RESULT_CANCELED
        finishActivity(resultCode)
    }

    override fun onPermissionsLoaded(permissions: Set<String>) {
        requestPermissions(permissions.toTypedArray(), PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> presenter.hasAllPermissions()
        }
    }

    companion object {

        private const val PERMISSION_REQUEST_CODE = 1

        fun newInstance() = SplashFragment()
    }
}