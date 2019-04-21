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
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.VerticalGridPresenter
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.pimenta.bestv.common.presentation.model.WorkViewModel
import com.pimenta.bestv.common.presentation.model.loadBackdrop
import com.pimenta.bestv.feature.base.BaseVerticalGridFragment
import com.pimenta.bestv.feature.error.ErrorFragment
import com.pimenta.bestv.feature.workbrowse.presenter.WorkGridPresenter
import com.pimenta.bestv.feature.workdetail.ui.WorkDetailsActivity
import com.pimenta.bestv.feature.workdetail.ui.WorkDetailsFragment
import com.pimenta.bestv.widget.render.WorkCardRenderer
import javax.inject.Inject

/**
 * Created by marcus on 09-02-2018.
 */
abstract class AbstractWorkGridFragment : BaseVerticalGridFragment(), WorkGridPresenter.View,
        BrowseSupportFragment.MainFragmentAdapterProvider {

    private val fragmentAdapter: BrowseSupportFragment.MainFragmentAdapter<AbstractWorkGridFragment> by lazy {
        BrowseSupportFragment.MainFragmentAdapter<AbstractWorkGridFragment>(this)
    }
    private val backgroundManager: BackgroundManager by lazy { BackgroundManager.getInstance(activity) }
    protected val rowsAdapter: ArrayObjectAdapter by lazy { ArrayObjectAdapter(WorkCardRenderer()) }

    protected var showProgress: Boolean = false
    private var workSelected: WorkViewModel? = null

    @Inject
    lateinit var presenter: WorkGridPresenter

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
        workSelected?.run {
            loadBackdropImage()
            refreshDada()
        }
    }

    override fun onDestroy() {
        progressBarManager.hide()
        super.onDestroy()
    }

    override fun onDetach() {
        presenter.dispose()
        super.onDetach()
    }

    override fun getMainFragmentAdapter() = fragmentAdapter

    override fun onWorksLoaded(works: List<WorkViewModel>?) {
        works?.forEach {
            if (rowsAdapter.indexOf(it) == -1) {
                rowsAdapter.add(it)
            }
        } ?: run {
            if (rowsAdapter.size() == 0) {
                val fragment = ErrorFragment.newInstance()
                fragment.setTarget(this, ERROR_FRAGMENT_REQUEST_CODE)
                addFragment(fragment, ErrorFragment.TAG)
            }
        }

        progressBarManager.hide()
        mainFragmentAdapter.fragmentHost.notifyDataReady(mainFragmentAdapter)
    }

    override fun loadBackdropImage(workViewModel: WorkViewModel) {
        workViewModel.loadBackdrop(requireNotNull(context), object : SimpleTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                backgroundManager.setBitmap(resource)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ERROR_FRAGMENT_REQUEST_CODE -> {
                popBackStack(ErrorFragment.TAG, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
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

        adapter = rowsAdapter

        setOnItemViewSelectedListener { _, item, _, _ ->
            workSelected = item as WorkViewModel?
            loadBackdropImage()

            if (rowsAdapter.indexOf(workSelected) >= rowsAdapter.size() - NUMBER_COLUMNS) {
                loadMorePages()
            }
        }
        setOnItemViewClickedListener { itemViewHolder, item, _, _ ->
            val workViewModel = item as WorkViewModel
            val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireNotNull(activity),
                    (itemViewHolder.view as ImageCardView).mainImageView,
                    WorkDetailsFragment.SHARED_ELEMENT_NAME
            ).toBundle()
            startActivity(WorkDetailsActivity.newInstance(context, workViewModel), bundle)
        }
    }

    private fun loadBackdropImage() {
        workSelected?.let {
            presenter.countTimerLoadBackdropImage(it)
        }
    }

    abstract fun loadData()

    companion object {

        const val SHOW_PROGRESS = "SHOW_PROGRESS"

        private const val ERROR_FRAGMENT_REQUEST_CODE = 1
        private const val NUMBER_COLUMNS = 6

    }
}