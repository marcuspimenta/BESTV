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

package com.pimenta.bestv.feature.workbrowse.ui

import android.content.Context
import android.os.Bundle

import com.pimenta.bestv.BesTV
import com.pimenta.bestv.repository.MediaRepository
import com.pimenta.bestv.repository.entity.Work

/**
 * Created by marcus on 11-02-2018.
 */
class TopWorkGridFragment : AbstractWorkGridFragment() {

    private lateinit var workType: MediaRepository.WorkType

    override fun onAttach(context: Context) {
        super.onAttach(context)
        BesTV.applicationComponent.getTopWorkGridFragmentComponent()
                .view(this)
                .build()
                .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            workType = it.getSerializable(TYPE) as MediaRepository.WorkType
            showProgress = it.getBoolean(AbstractWorkGridFragment.SHOW_PROGRESS)
        }
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

    override fun onWorksLoaded(works: List<Work>?) {
        if (workType == MediaRepository.WorkType.FAVORITES_MOVIES) {
            /*rowsAdapter.setItems(works, new DiffCallback<Movie>() {
                @Override
                public boolean areItemsTheSame(@NonNull final Movie oldItem, @NonNull final Movie newItem) {
                    return oldItem.equals(newItem);
                }

                @Override
                public boolean areContentsTheSame(@NonNull final Movie oldItem, @NonNull final Movie newItem) {
                    return oldItem.equals(newItem);
                }
            });*/
            if (works != null) {
                rowsAdapter.setItems(works, null)
            }
            progressBarManager.hide()
            mainFragmentAdapter.fragmentHost.notifyDataReady(mainFragmentAdapter)
            return
        }
        super.onWorksLoaded(works)
    }

    companion object {

        private const val TYPE = "TYPE"

        fun newInstance(workType: MediaRepository.WorkType, showProgress: Boolean) =
                TopWorkGridFragment().apply {
                    this.arguments = Bundle().apply {
                        putSerializable(TYPE, workType)
                        putBoolean(AbstractWorkGridFragment.SHOW_PROGRESS, showProgress)
                    }
                    this.workType = workType
                    this.showProgress = showProgress
                }
    }
}