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

package com.pimenta.bestv.feature.main.presentation.ui.fragment

import android.content.Context
import androidx.core.os.bundleOf
import com.pimenta.bestv.BesTV
import com.pimenta.bestv.data.MediaRepository

private const val TYPE = "TYPE"

/**
 * Created by marcus on 11-02-2018.
 */
class TopWorkGridFragment : AbstractWorkGridFragment() {

    private val workType: MediaRepository.WorkType by lazy { arguments?.getSerializable(TYPE) as MediaRepository.WorkType }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        BesTV.applicationComponent.getTopWorkGridFragmentComponent()
                .view(this)
                .build()
                .inject(this)
    }

    override fun loadData() {
        presenter.loadWorksByType(workType)
    }

    override fun loadMorePages() {
        if (workType != MediaRepository.WorkType.FAVORITES_MOVIES) {
            super.loadMorePages()
        }
    }

    override fun refreshDada() {
        if (workType == MediaRepository.WorkType.FAVORITES_MOVIES) {
            super.loadMorePages()
        }
    }

    companion object {

        fun newInstance(workType: MediaRepository.WorkType) =
                TopWorkGridFragment().apply {
                    arguments = bundleOf(
                            TYPE to workType
                    )
                }
    }
}