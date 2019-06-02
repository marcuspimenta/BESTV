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

package com.pimenta.bestv.feature.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.ErrorSupportFragment
import com.pimenta.bestv.BesTV
import com.pimenta.bestv.extension.getTopFragment
import com.pimenta.bestv.extension.replaceFragment
import javax.inject.Inject

/**
 * Created by marcus on 12/07/18.
 */
class SearchActivity : FragmentActivity() {

    private val backgroundManager: BackgroundManager by lazy { BackgroundManager.getInstance(this) }

    @Inject
    lateinit var displayMetrics: DisplayMetrics

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BesTV.applicationComponent.inject(this)

        backgroundManager.attach(window)
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        
        replaceFragment(SearchFragment.newInstance())
    }

    override fun onBackPressed() {
        val topFragment = getTopFragment()
        if (topFragment !is ErrorSupportFragment) {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        backgroundManager.release()
        super.onDestroy()
    }

    companion object {

        fun newInstance(context: Context?): Intent = Intent(context, SearchActivity::class.java)

    }
}