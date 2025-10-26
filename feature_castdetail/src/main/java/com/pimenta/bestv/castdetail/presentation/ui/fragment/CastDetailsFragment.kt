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

package com.pimenta.bestv.castdetail.presentation.ui.fragment

import android.app.Activity
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
import com.pimenta.bestv.castdetail.presentation.model.CastDetailsEvent
import com.pimenta.bestv.castdetail.presentation.ui.activity.CastDetailsActivity
import com.pimenta.bestv.castdetail.presentation.ui.compose.CastDetailsScreen
import com.pimenta.bestv.castdetail.presentation.viewmodel.CastDetailsViewModel
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.presentation.extension.addFragment
import com.pimenta.bestv.presentation.extension.popBackStack
import com.pimenta.bestv.presentation.ui.fragment.ErrorFragment
import javax.inject.Inject

private const val CAST = "CAST"
private const val ERROR_FRAGMENT_REQUEST_CODE = 1

/**
 * Fragment for displaying cast details using Jetpack Compose for TV.
 *
 * Created by marcus on 04-04-2018.
 */
class CastDetailsFragment : Fragment() {

    private val castViewModel by lazy { arguments?.getSerializable(CAST) as CastViewModel }

    @Inject
    lateinit var viewModel: CastDetailsViewModel

    override fun onAttach(context: Context) {
        (requireActivity() as CastDetailsActivity).castDetailsActivityComponent
            .castDetailsFragmentComponent()
            .create(castViewModel)
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
                    CastDetailsScreen(
                        viewModel = viewModel,
                        openIntent = { openIntent(it) },
                        showError = { showErrorFragment() }
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
        // Reference: https://developer.android.com/develop/ui/compose/animation/shared-elements
        startActivity(intent)
    }

    private fun showErrorFragment() {
        val fragment = ErrorFragment.newInstance()
        // Note: Using deprecated setTargetFragment API
        // ErrorFragment is shared in presentation_shared and still uses Leanback ErrorSupportFragment
        // Full migration to Compose/Fragment Result API should be done when migrating all modules
        fragment.setTargetFragment(this, ERROR_FRAGMENT_REQUEST_CODE)
        requireActivity().addFragment(fragment, ErrorFragment.TAG)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ERROR_FRAGMENT_REQUEST_CODE -> {
                requireActivity().popBackStack(ErrorFragment.TAG, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
                when (resultCode) {
                    Activity.RESULT_OK -> viewModel.handleEvent(CastDetailsEvent.LoadData)
                    else -> requireActivity().finish()
                }
            }
        }
    }

    companion object {

        fun newInstance(castViewModel: CastViewModel) =
            CastDetailsFragment().apply {
                arguments = bundleOf(
                    CAST to castViewModel
                )
            }
    }
}
