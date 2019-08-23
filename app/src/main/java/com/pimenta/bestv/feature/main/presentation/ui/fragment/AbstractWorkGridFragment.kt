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

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.leanback.R
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.VerticalGridPresenter
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.pimenta.bestv.common.extension.addFragment
import com.pimenta.bestv.common.presentation.diffcallback.WorkDiffCallback
import com.pimenta.bestv.common.presentation.model.WorkViewModel
import com.pimenta.bestv.common.presentation.model.loadBackdrop
import com.pimenta.bestv.common.presentation.ui.render.WorkCardRenderer
import com.pimenta.bestv.common.presentation.ui.fragment.ErrorFragment
import com.pimenta.bestv.feature.workdetail.presentation.ui.activity.WorkDetailsActivity
import com.pimenta.bestv.feature.workdetail.presentation.ui.fragment.WorkDetailsFragment
import com.pimenta.bestv.feature.main.presentation.presenter.WorkGridPresenter
import javax.inject.Inject

private const val ERROR_FRAGMENT_REQUEST_CODE = 1
private const val NUMBER_COLUMNS = 6

/**
 * Created by marcus on 09-02-2018.
 */
abstract class AbstractWorkGridFragment : VerticalGridSupportFragment(), WorkGridPresenter.View,
        BrowseSupportFragment.MainFragmentAdapterProvider {

    private val fragmentAdapter: BrowseSupportFragment.MainFragmentAdapter<AbstractWorkGridFragment> by lazy {
        BrowseSupportFragment.MainFragmentAdapter(this)
    }
    private val backgroundManager: BackgroundManager? by lazy { activity?.let { BackgroundManager.getInstance(it) } }
    private val rowsAdapter: ArrayObjectAdapter by lazy { ArrayObjectAdapter(WorkCardRenderer()) }
    private val workDiffCallback: WorkDiffCallback by lazy { WorkDiffCallback() }

    @Inject
    lateinit var presenter: WorkGridPresenter

    private var workSelected: WorkViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.bindTo(this.lifecycle)

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
        loadData()
    }

    override fun onResume() {
        super.onResume()

        loadBackdropImage()
        refreshDada()
    }

    override fun onDestroy() {
        progressBarManager.hide()
        super.onDestroy()
    }

    override fun getMainFragmentAdapter() = fragmentAdapter

    override fun onShowProgress() {
        progressBarManager.show()
    }

    override fun onHideProgress() {
        progressBarManager.hide()
    }

    override fun onWorksLoaded(works: List<WorkViewModel>) {
        rowsAdapter.setItems(works, workDiffCallback)
        mainFragmentAdapter.fragmentHost.notifyDataReady(mainFragmentAdapter)
    }

    override fun loadBackdropImage(workViewModel: WorkViewModel) {
        workViewModel.loadBackdrop(requireNotNull(context), object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                backgroundManager?.setBitmap(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                //DO ANYTHING
            }
        })
    }

    override fun onErrorWorksLoaded() {
        val fragment = ErrorFragment.newInstance().apply {
            setTargetFragment(this@AbstractWorkGridFragment, ERROR_FRAGMENT_REQUEST_CODE)
        }
        fragmentManager?.addFragment(R.id.scale_frame, fragment, ErrorFragment.TAG)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ERROR_FRAGMENT_REQUEST_CODE -> {
                fragmentManager?.popBackStack(ErrorFragment.TAG, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
                if (resultCode == Activity.RESULT_OK) {
                    loadData()
                }
            }
        }
    }

    open fun loadMorePages() {
        loadData()
    }

    open fun refreshDada() {
        workSelected?.let {
            loadData()
        }
    }

    private fun setupUI() {
        val verticalGridPresenter = VerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_MEDIUM)
        verticalGridPresenter.numberOfColumns = NUMBER_COLUMNS
        gridPresenter = verticalGridPresenter

        adapter = rowsAdapter

        setOnItemViewSelectedListener { _, item, _, _ ->
            workSelected = item as WorkViewModel?
            workSelected?.let {
                loadBackdropImage()

                if (rowsAdapter.indexOf(it) >= rowsAdapter.size() - NUMBER_COLUMNS) {
                    loadMorePages()
                }
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

}