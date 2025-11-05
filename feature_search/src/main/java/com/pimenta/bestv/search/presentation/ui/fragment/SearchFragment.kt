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

package com.pimenta.bestv.search.presentation.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.tv.material3.MaterialTheme
import com.pimenta.bestv.search.presentation.ui.activity.SearchActivity
import com.pimenta.bestv.search.presentation.ui.compose.SearchScreen
import com.pimenta.bestv.search.presentation.viewmodel.SearchViewModel
import javax.inject.Inject

/**
 * Created by marcus on 12-03-2018.
 */
class SearchFragment : Fragment() {

    @Inject
    lateinit var viewModel: SearchViewModel

    override fun onAttach(context: Context) {
        (requireActivity() as SearchActivity).searchActivityComponent
            .searchFragmentComponent()
            .create()
            .inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    SearchScreen(
                        viewModel = viewModel,
                        openIntent = { openIntent(it) }
                    )
                }
            }
        }
    }

    private fun openIntent(intent: Intent) {
        // For full Compose shared element transitions, implement:
        // - SharedTransitionLayout wrapping the screen
        // - Modifier.sharedElement() on WorkCard images
        // - Modifier.sharedBounds() for coordinated transitions
        startActivity(intent)
    }

    companion object {

        fun newInstance(): SearchFragment = SearchFragment()
    }
}
