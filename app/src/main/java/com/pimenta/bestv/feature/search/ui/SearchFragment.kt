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

package com.pimenta.bestv.feature.search.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.ProgressBarManager
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.*
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.pimenta.bestv.BesTV
import com.pimenta.bestv.R
import com.pimenta.bestv.common.presentation.diffcallback.WorkDiffCallback
import com.pimenta.bestv.common.presentation.model.WorkViewModel
import com.pimenta.bestv.common.presentation.model.loadBackdrop
import com.pimenta.bestv.common.presentation.ui.render.WorkCardRenderer
import com.pimenta.bestv.common.extension.addFragment
import com.pimenta.bestv.common.extension.popBackStack
import com.pimenta.bestv.feature.error.ErrorFragment
import com.pimenta.bestv.feature.search.presenter.SearchPresenter
import com.pimenta.bestv.feature.workdetail.ui.WorkDetailsActivity
import com.pimenta.bestv.feature.workdetail.ui.WorkDetailsFragment
import javax.inject.Inject

/**
 * Created by marcus on 12-03-2018.
 */
class SearchFragment : SearchSupportFragment(), SearchPresenter.View, SearchSupportFragment.SearchResultProvider {

    private val rowsAdapter: ArrayObjectAdapter by lazy { ArrayObjectAdapter(ListRowPresenter()) }
    private val movieRowAdapter: ArrayObjectAdapter by lazy { ArrayObjectAdapter(WorkCardRenderer()) }
    private val tvShowRowAdapter: ArrayObjectAdapter by lazy { ArrayObjectAdapter(WorkCardRenderer()) }
    private val backgroundManager: BackgroundManager by lazy { BackgroundManager.getInstance(activity) }
    private val progressBarManager: ProgressBarManager by lazy { ProgressBarManager() }
    private val workDiffCallback: WorkDiffCallback by lazy { WorkDiffCallback() }

    @Inject
    lateinit var presenter: SearchPresenter

    private var query: String? = null
    private var workSelected: WorkViewModel? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        BesTV.applicationComponent.getSearchFragmentComponent()
                .view(this)
                .build()
                .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.bindTo(this.lifecycle)

        setupUI()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        progressBarManager.setRootView(container)
        progressBarManager.enableProgressBar()
        progressBarManager.initialDelay = 0

        val view = super.onCreateView(inflater, container, savedInstanceState)
        view?.setBackgroundColor(resources.getColor(androidx.leanback.R.color.lb_playback_controls_background_light, null))
        return view
    }

    override fun onResume() {
        super.onResume()
        loadBackdropImage()
    }

    override fun onShowProgress() {
        progressBarManager.show()
    }

    override fun onHideProgress() {
        progressBarManager.hide()
    }

    override fun onClear() {
        backgroundManager.setBitmap(null)
        rowsAdapter.clear()
        rowsAdapter.add(ListRow(HeaderItem(0, getString(R.string.no_results)),
                ArrayObjectAdapter(WorkCardRenderer())))
    }

    override fun onResultLoaded(movies: List<WorkViewModel>, tvShows: List<WorkViewModel>) {
        rowsAdapter.clear()

        if (movies.isNotEmpty()) {
            val header = HeaderItem(MOVIE_HEADER_ID.toLong(), getString(R.string.movies))
            movieRowAdapter.setItems(movies, workDiffCallback)
            rowsAdapter.add(ListRow(header, movieRowAdapter))
        }
        if (tvShows.isNotEmpty()) {
            val header = HeaderItem(TV_SHOW_HEADER_ID.toLong(), getString(R.string.tv_shows))
            tvShowRowAdapter.setItems(tvShows, workDiffCallback)
            rowsAdapter.add(ListRow(header, tvShowRowAdapter))
        }
    }

    override fun onMoviesLoaded(movies: List<WorkViewModel>) {
        movieRowAdapter.setItems(movies, workDiffCallback)
    }

    override fun onTvShowsLoaded(tvShows: List<WorkViewModel>) {
        tvShowRowAdapter.setItems(tvShows, workDiffCallback)
    }

    override fun loadBackdropImage(workViewModel: WorkViewModel) {
        workViewModel.loadBackdrop(requireNotNull(context), object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                backgroundManager.setBitmap(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                //DO ANYTHING
            }
        })
    }

    override fun onErrorSearch() {
        val fragment = ErrorFragment.newInstance().apply {
            setTargetFragment(this@SearchFragment, ERROR_FRAGMENT_REQUEST_CODE)
        }
        activity?.addFragment(fragment, ErrorFragment.TAG)
    }

    override fun getResultsAdapter() = rowsAdapter

    override fun onQueryTextChange(text: String): Boolean {
        query = text
        presenter.searchWorksByQuery(query)
        return true
    }

    override fun onQueryTextSubmit(text: String): Boolean {
        query = text
        presenter.searchWorksByQuery(query)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            SEARCH_FRAGMENT_REQUEST_CODE -> {
                view?.requestFocus()
            }
            ERROR_FRAGMENT_REQUEST_CODE -> {
                activity?.popBackStack(ErrorFragment.TAG, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
                if (resultCode == Activity.RESULT_OK) {
                    presenter.searchWorksByQuery(query)
                }
            }
        }
    }

    private fun setupUI() {
        setSearchResultProvider(this)
        setOnItemViewSelectedListener { _, item, _, row ->
            workSelected = item as WorkViewModel?
            loadBackdropImage()

            when (row?.run { id.toInt() }) {
                MOVIE_HEADER_ID -> if (movieRowAdapter.indexOf(workSelected) >= movieRowAdapter.size() - 1) {
                    presenter.loadMovies()
                }
                TV_SHOW_HEADER_ID -> if (tvShowRowAdapter.indexOf(workSelected) >= tvShowRowAdapter.size() - 1) {
                    presenter.loadTvShows()
                }
            }
        }
        setOnItemViewClickedListener { itemViewHolder, item, _, _ ->
            val workViewModel = item as WorkViewModel
            val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireNotNull(activity),
                    (itemViewHolder?.view as ImageCardView).mainImageView,
                    WorkDetailsFragment.SHARED_ELEMENT_NAME
            ).toBundle()
            startActivityForResult(WorkDetailsActivity.newInstance(context, workViewModel), SEARCH_FRAGMENT_REQUEST_CODE, bundle)
        }
    }

    private fun loadBackdropImage() {
        workSelected?.let {
            presenter.countTimerLoadBackdropImage(it)
        }
    }

    companion object {

        private const val SEARCH_FRAGMENT_REQUEST_CODE = 1
        private const val ERROR_FRAGMENT_REQUEST_CODE = 2
        private const val MOVIE_HEADER_ID = 1
        private const val TV_SHOW_HEADER_ID = 2

        fun newInstance(): SearchFragment = SearchFragment()
    }
}