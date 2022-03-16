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

package com.pimenta.bestv.workbrowse.presentation.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.BackgroundManager
import com.pimenta.bestv.presentation.extension.replaceFragment
import com.pimenta.bestv.workbrowse.di.MainActivityComponent
import com.pimenta.bestv.workbrowse.di.MainActivityComponentProvider
import com.pimenta.bestv.workbrowse.presentation.presenter.MainPresenter
import com.pimenta.bestv.workbrowse.presentation.ui.fragment.WorkBrowseFragment
import javax.inject.Inject

/**
 * Created by marcus on 11-02-2018.
 */
class MainActivity : FragmentActivity(), MainPresenter.View {

    private val backgroundManager: BackgroundManager by lazy { BackgroundManager.getInstance(this) }

    lateinit var mainActivityComponent: MainActivityComponent

    @Inject
    lateinit var presenter: MainPresenter

    public override fun onCreate(savedInstanceState: Bundle?) {
        mainActivityComponent = (application as MainActivityComponentProvider)
            .mainActivityComponent(this)
            .also {
                it.inject(this)
            }
        super.onCreate(savedInstanceState)

        backgroundManager.apply {
            attach(window)
            setBitmap(null)
        }

        presenter.apply {
            bindTo(lifecycle)
        }.also {
            it.loadRecommendations()
            it.viewCreated(savedInstanceState == null)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.checkActivityResult(requestCode, resultCode)
    }

    override fun onDestroy() {
        backgroundManager.release()
        super.onDestroy()
    }

    override fun openSplashScreen(intent: Intent, requestCode: Int) {
        startActivityForResult(intent, requestCode)
    }

    override fun showWorkBrowseScreen() {
        replaceFragment(WorkBrowseFragment.newInstance())
    }

    override fun close() {
        finish()
    }
}
