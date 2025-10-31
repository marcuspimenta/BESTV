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

package com.pimenta.bestv.workdetail.presentation.ui.fragment

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.tv.material3.MaterialTheme
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.workdetail.presentation.model.ErrorType.FailedToOpenYouTubeVideo
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.ShowError
import com.pimenta.bestv.workdetail.presentation.ui.activity.WorkDetailsActivity
import com.pimenta.bestv.workdetail.presentation.ui.compose.WorkDetailsScreen
import com.pimenta.bestv.workdetail.presentation.viewmodel.WorkDetailsViewModel
import javax.inject.Inject

private const val WORK = "WORK"

/**
 * Fragment for displaying work details using Jetpack Compose for TV.
 *
 * Migrated from Leanback to Compose following TV design patterns.
 *
 * Created by marcus on 07-02-2018.
 */
class WorkDetailsFragment : Fragment() {

    private val workViewModel by lazy { arguments?.getSerializable(WORK) as WorkViewModel }

    @Inject
    lateinit var viewModel: WorkDetailsViewModel

    override fun onAttach(context: Context) {
        (requireActivity() as WorkDetailsActivity).workDetailsActivityComponent
            .workDetailsFragmentComponent()
            .create(workViewModel)
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
                    WorkDetailsScreen(
                        viewModel = viewModel,
                        openIntent = { openIntent(it) }
                    )
                }
            }
        }
    }

    private fun openIntent(intent: Intent) {
        try {
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            viewModel.handleEvent(ShowError(FailedToOpenYouTubeVideo))
        }
    }

    companion object {
        fun newInstance(workViewModel: WorkViewModel) =
            WorkDetailsFragment().apply {
                arguments = bundleOf(
                    WORK to workViewModel
                )
            }
    }
}
