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

package com.pimenta.bestv.workbrowse.presentation.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.tv.material3.MaterialTheme
import com.pimenta.bestv.workbrowse.presentation.ui.activity.MainActivity
import com.pimenta.bestv.workbrowse.presentation.ui.compose.WorkBrowseScreen
import com.pimenta.bestv.workbrowse.presentation.viewmodel.WorkBrowseViewModel
import javax.inject.Inject

/**
 * Fragment for browsing works using Jetpack Compose for TV.
 *
 * Migrated from Leanback to Compose following TV design patterns.
 *
 * Created by marcus on 07-02-2018.
 */
class WorkBrowseFragment : Fragment() {

    @Inject
    lateinit var viewModel: WorkBrowseViewModel

    override fun onAttach(context: Context) {
        (requireActivity() as MainActivity).mainActivityComponent
            .workBrowseFragmentComponent()
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
                    WorkBrowseScreen(
                        viewModel = viewModel,
                        closeScreen = { requireActivity().finish() },
                        openIntent = { openIntent(it) },
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkAndUpdateFavorites()
    }

    private fun openIntent(intent: Intent) {
        startActivity(intent)
    }

    companion object {
        fun newInstance() = WorkBrowseFragment()
    }
}
