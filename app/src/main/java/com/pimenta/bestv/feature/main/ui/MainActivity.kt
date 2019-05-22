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

package com.pimenta.bestv.feature.main.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.pimenta.bestv.BesTV
import com.pimenta.bestv.common.presentation.ui.base.BaseActivity
import com.pimenta.bestv.feature.main.presenter.MainPresenter
import com.pimenta.bestv.feature.splash.ui.SplashActivity
import com.pimenta.bestv.feature.workbrowse.ui.WorkBrowseFragment
import javax.inject.Inject

/**
 * Created by marcus on 11-02-2018.
 */
class MainActivity : BaseActivity() {

    @Inject
    lateinit var presenter: MainPresenter

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BesTV.applicationComponent.inject(this)
        presenter.bindTo(lifecycle)
        presenter.loadRecommendations()

        when (savedInstanceState) {
            null -> startActivityForResult(SplashActivity.newInstance(this), SPLASH_ACTIVITY_REQUEST_CODE)
            else -> replaceFragment(WorkBrowseFragment.newInstance())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SPLASH_ACTIVITY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    replaceFragment(WorkBrowseFragment.newInstance())
                } else {
                    finish()
                }
            }
        }
    }

    companion object {

        private const val SPLASH_ACTIVITY_REQUEST_CODE = 0
    }
}