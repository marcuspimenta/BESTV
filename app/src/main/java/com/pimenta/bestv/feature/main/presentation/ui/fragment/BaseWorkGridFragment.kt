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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.VerticalGridPresenter
import com.pimenta.bestv.common.presentation.ui.diffcallback.WorkDiffCallback
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.common.presentation.ui.render.WorkCardRenderer

/**
 * Created by marcus on 09-02-2018.
 */
private const val NUMBER_COLUMNS = 6

abstract class BaseWorkGridFragment : VerticalGridSupportFragment(),
        BrowseSupportFragment.MainFragmentAdapterProvider {

    private val fragmentAdapter by lazy { BrowseSupportFragment.MainFragmentAdapter(this) }
    protected val backgroundManager by lazy { activity?.let { BackgroundManager.getInstance(it) } }
    protected val rowsAdapter by lazy { ArrayObjectAdapter(WorkCardRenderer()) }
    protected val workDiffCallback by lazy { WorkDiffCallback() }

    private var workSelected: WorkViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBarManager.apply {
            enableProgressBar()
            setProgressBarView(
                    LayoutInflater.from(context).inflate(com.pimenta.bestv.R.layout.view_load, null).also {
                        (view.parent as ViewGroup).addView(it)
                    })
            initialDelay = 0
        }

        mainFragmentAdapter.fragmentHost.notifyViewCreated(mainFragmentAdapter)
    }

    override fun onResume() {
        super.onResume()
        workSelected?.let {
            workSelected(it)
        }
    }

    override fun onDestroy() {
        progressBarManager.hide()
        super.onDestroy()
    }

    override fun getMainFragmentAdapter() = fragmentAdapter

    private fun setupUI() {
        val verticalGridPresenter = VerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_MEDIUM)
        verticalGridPresenter.numberOfColumns = NUMBER_COLUMNS
        gridPresenter = verticalGridPresenter

        adapter = rowsAdapter

        setOnItemViewSelectedListener { _, item, _, _ ->
            workSelected = item as WorkViewModel?
            workSelected?.let {
                workSelected(it)

                if (rowsAdapter.indexOf(it) >= rowsAdapter.size() - NUMBER_COLUMNS) {
                    lastRowLoaded()
                }
            }
        }
        setOnItemViewClickedListener { itemViewHolder, item, _, _ ->
            if (item is WorkViewModel) {
                workClicked(itemViewHolder, item)
            }
        }
    }

    abstract fun lastRowLoaded()

    abstract fun workSelected(workSelected: WorkViewModel)

    abstract fun workClicked(itemViewHolder: Presenter.ViewHolder, workViewModel: WorkViewModel)
}