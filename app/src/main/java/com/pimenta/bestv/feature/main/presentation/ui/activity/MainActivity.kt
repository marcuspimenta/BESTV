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

package com.pimenta.bestv.feature.main.presentation.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.BackgroundManager
import com.pimenta.bestv.common.extension.replaceFragment
import com.pimenta.bestv.feature.main.di.MainActivityComponent
import com.pimenta.bestv.feature.main.presentation.presenter.MainPresenter
import com.pimenta.bestv.feature.main.presentation.ui.fragment.WorkBrowseFragment
import com.pimenta.bestv.feature.splash.presentation.ui.activity.SplashActivity
import javax.inject.Inject

private const val SPLASH_ACTIVITY_REQUEST_CODE = 1

/**
 * Created by marcus on 11-02-2018.
 */
class MainActivity : FragmentActivity() {

    private val backgroundManager: BackgroundManager by lazy { BackgroundManager.getInstance(this) }

    @Inject
    lateinit var displayMetrics: DisplayMetrics

    @Inject
    lateinit var presenter: MainPresenter

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivityComponent.create(application).inject(this)

        backgroundManager.attach(window)
        backgroundManager.setBitmap(null)
        windowManager.defaultDisplay.getMetrics(displayMetrics)

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

    override fun onDestroy() {
        backgroundManager.release()
        super.onDestroy()
    }

    companion object {

        fun newInstance(context: Context) = Intent(context, MainActivity::class.java)
    }
}