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
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pimenta.bestv.R
import com.pimenta.bestv.presentation.extension.finish
import kotlinx.android.synthetic.main.fragment_splash.*

/**
 * Created by marcus on 04-05-2018.
 */
private const val SPLASH_ANIMATION_FILE = "android.resource://com.pimenta.bestv/raw/splash_animation"

class SplashFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_splash, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        animationVideoView.run {
            setOnCompletionListener {
                requireActivity().finish(Activity.RESULT_OK)
            }
            setVideoURI(
                    Uri.parse(SPLASH_ANIMATION_FILE)
            )
            start()
        }
    }

    companion object {

        fun newInstance() = SplashFragment()
    }
}