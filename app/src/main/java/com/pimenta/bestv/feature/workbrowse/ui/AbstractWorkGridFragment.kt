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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.support.v17.leanback.app.BackgroundManager
import android.support.v17.leanback.app.BrowseSupportFragment
import android.support.v17.leanback.widget.ArrayObjectAdapter
import android.support.v17.leanback.widget.FocusHighlight
import android.support.v17.leanback.widget.ImageCardView
import android.support.v17.leanback.widget.VerticalGridPresenter
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pimenta.bestv.feature.base.BaseVerticalGridFragment
import com.pimenta.bestv.feature.error.ErrorFragment
import com.pimenta.bestv.feature.widget.render.WorkCardRenderer
import com.pimenta.bestv.feature.workbrowse.presenter.WorkGridPresenter
import com.pimenta.bestv.feature.workbrowse.presenter.WorkGridPresenter.WorkGridView
import com.pimenta.bestv.feature.workdetail.ui.WorkDetailsActivity
import com.pimenta.bestv.feature.workdetail.ui.WorkDetailsFragment
import com.pimenta.bestv.repository.entity.Work
import java.util.*
import javax.inject.Inject

/**
 * Created by marcus on 09-02-2018.
 */
abstract class AbstractWorkGridFragment : BaseVerticalGridFragment(), WorkGridView, BrowseSupportFragment.MainFragmentAdapterProvider {

    private val fragmentAdapter: BrowseSupportFragment.MainFragmentAdapter<AbstractWorkGridFragment> by lazy {
        BrowseSupportFragment.MainFragmentAdapter<AbstractWorkGridFragment>(this)
    }

    private val backgroundManager: BackgroundManager by lazy {
        BackgroundManager.getInstance(activity)
    }

    private lateinit var backgroundTimer: Timer
    protected lateinit var rowsAdapter: ArrayObjectAdapter

    private lateinit var workSelected: Work
    protected var showProgress: Boolean = false

    @Inject
    lateinit var presenter: WorkGridPresenter

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        presenter.register(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        progressBarManager.setRootView(container)
        progressBarManager.enableProgressBar()
        progressBarManager.initialDelay = 0
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainFragmentAdapter.fragmentHost.notifyViewCreated(mainFragmentAdapter)

        if (showProgress) {
            progressBarManager.show()
        }
        loadData()
    }

    override fun onResume() {
        super.onResume()
        if (::workSelected.isInitialized) {
            loadBackdropImage(false)
            refreshDada()
        }
    }

    override fun onDestroy() {
        progressBarManager.hide()
        if (::backgroundTimer.isInitialized) {
            backgroundTimer.cancel()
        }
        super.onDestroy()
    }

    override fun onDetach() {
        presenter.unRegister()
        super.onDetach()
    }

    override fun getMainFragmentAdapter() = fragmentAdapter

    override fun onWorksLoaded(works: List<Work>?) {
        if (works != null) {
            works.forEach {
                if (rowsAdapter.indexOf(it) == -1) {
                    rowsAdapter.add(it)
                }
            }
        } else if (rowsAdapter.size() == 0) {
            val fragment = ErrorFragment.newInstance()
            fragment.setTarget(this, ERROR_FRAGMENT_REQUEST_CODE)
            addFragment(fragment, ErrorFragment.TAG)
        }

        progressBarManager.hide()
        mainFragmentAdapter.fragmentHost.notifyDataReady(mainFragmentAdapter)
    }

    override fun onBackdropImageLoaded(bitmap: Bitmap?) {
        backgroundManager.setBitmap(bitmap)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ERROR_FRAGMENT_REQUEST_CODE -> {
                popBackStack(ErrorFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                if (resultCode == Activity.RESULT_OK) {
                    progressBarManager.show()
                    loadData()
                }
            }
        }
    }

    open fun loadMorePages() {
        progressBarManager.show()
        loadData()
    }

    open fun refreshDada() {
        progressBarManager.show()
        loadData()
    }

    private fun setupUI() {
        val verticalGridPresenter = VerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_MEDIUM)
        verticalGridPresenter.numberOfColumns = NUMBER_COLUMNS
        gridPresenter = verticalGridPresenter

        val workCardRenderer = WorkCardRenderer()
        rowsAdapter = ArrayObjectAdapter(workCardRenderer)
        adapter = rowsAdapter

        setOnItemViewSelectedListener { _, item, _, _ ->
            workSelected = item as Work
            loadBackdropImage(true)

            if (rowsAdapter.indexOf(workSelected) >= rowsAdapter.size() - NUMBER_COLUMNS) {
                loadMorePages()
            }
        }
        setOnItemViewClickedListener { itemViewHolder, item, _, _ ->
            val work = item as Work
            val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity!!,
                    (itemViewHolder.view as ImageCardView).mainImageView,
                    WorkDetailsFragment.SHARED_ELEMENT_NAME
            ).toBundle()
            startActivity(WorkDetailsActivity.newInstance(context, work), bundle)
        }
    }

    private fun loadBackdropImage(delay: Boolean) {
        if (!::workSelected.isInitialized) {
            return
        }

        if (::backgroundTimer.isInitialized) {
            backgroundTimer.cancel()
        }
        backgroundTimer = Timer()
        backgroundTimer.schedule(object : TimerTask() {
            override fun run() {
                handler.post {
                    presenter.loadBackdropImage(workSelected)
                    backgroundTimer.cancel()
                }
            }
        }, (BACKGROUND_UPDATE_DELAY.takeIf { delay } ?: 0).toLong())
    }

    abstract fun loadData()

    companion object {

        const val SHOW_PROGRESS = "SHOW_PROGRESS"

        private const val ERROR_FRAGMENT_REQUEST_CODE = 1
        private const val BACKGROUND_UPDATE_DELAY = 300
        private const val NUMBER_COLUMNS = 6

        private val handler = Handler()
    }
}