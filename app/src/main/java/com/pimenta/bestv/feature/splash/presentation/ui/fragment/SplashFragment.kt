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

package com.pimenta.bestv.feature.splash.presentation.ui.fragment

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pimenta.bestv.R
import com.pimenta.bestv.common.extension.finish
import com.pimenta.bestv.feature.splash.di.SplashFragmentComponent
import com.pimenta.bestv.feature.splash.presentation.presenter.SplashPresenter
import kotlinx.android.synthetic.main.fragment_splash.*
import javax.inject.Inject

private const val PERMISSION_REQUEST_CODE = 1

/**
 * Created by marcus on 04-05-2018.
 */
class SplashFragment : Fragment(), SplashPresenter.View {

    @Inject
    lateinit var presenter: SplashPresenter

    override fun onAttach(context: Context) {
        SplashFragmentComponent.create(this, requireActivity().application)
                .inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.bindTo(lifecycle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_splash, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        animationVideoView.setVideoURI(
                Uri.parse("android.resource://com.pimenta.bestv/raw/splash_animation")
        )
        animationVideoView.start()

        presenter.loadPermissions()
    }

    override fun onHasAllPermissions(hasAllPermissions: Boolean) {
        val resultCode = Activity.RESULT_OK.takeIf { hasAllPermissions } ?: Activity.RESULT_CANCELED
        requireActivity().finish(resultCode)
    }

    override fun onRequestPermissions(permissions: List<String>) {
        requestPermissions(permissions.toTypedArray(), PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> presenter.hasAllPermissions()
        }
    }

    companion object {

        fun newInstance() = SplashFragment()
    }
}