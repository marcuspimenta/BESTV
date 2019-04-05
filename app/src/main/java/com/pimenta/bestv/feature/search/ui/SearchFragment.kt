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

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.ProgressBarManager
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.*
import androidx.core.app.ActivityOptionsCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pimenta.bestv.BesTV
import com.pimenta.bestv.R
import com.pimenta.bestv.feature.base.BaseSearchFragment
import com.pimenta.bestv.feature.search.presenter.SearchPresenter
import com.pimenta.bestv.feature.widget.render.WorkCardRenderer
import com.pimenta.bestv.feature.workdetail.ui.WorkDetailsActivity
import com.pimenta.bestv.feature.workdetail.ui.WorkDetailsFragment
import com.pimenta.bestv.repository.entity.Work
import java.util.*
import javax.inject.Inject

/**
 * Created by marcus on 12-03-2018.
 */
class SearchFragment : BaseSearchFragment(), SearchPresenter.View, SearchSupportFragment.SearchResultProvider {

    private val rowsAdapter: ArrayObjectAdapter by lazy { ArrayObjectAdapter(ListRowPresenter()) }
    private val movieRowAdapter: ArrayObjectAdapter by lazy { ArrayObjectAdapter(WorkCardRenderer()) }
    private val tvShowRowAdapter: ArrayObjectAdapter by lazy { ArrayObjectAdapter(WorkCardRenderer()) }
    private val backgroundManager: BackgroundManager by lazy { BackgroundManager.getInstance(activity) }

    private var workSelected: Work? = null
    private var backgroundTimer: Timer = Timer()

    @Inject
    lateinit var presenter: SearchPresenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        BesTV.applicationComponent.getSearchFragmentComponent()
                .view(this)
                .build()
                .inject(this)
        activity?.let {
            backgroundManager.attach(it.window)
            it.windowManager.defaultDisplay.getMetrics(presenter.displayMetrics)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clearAdapter()
    }

    override fun onResume() {
        super.onResume()
        loadBackdropImage(false)
    }

    override fun onDestroyView() {
        progressBarManager.hide()
        backgroundTimer.cancel()
        super.onDestroyView()
    }

    override fun onDetach() {
        backgroundManager.release()
        presenter.dispose()
        super.onDetach()
    }

    override fun onResultLoaded(movies: List<Work>?, tvShows: List<Work>?) {
        val hasMovies = movies?.isNotEmpty() ?: false
        val hasTvShows = tvShows?.isNotEmpty() ?: false

        progressBarManager.hide()
        if (hasMovies || hasTvShows) {
            rowsAdapter.clear()

            if (hasMovies) {
                val header = HeaderItem(MOVIE_HEADER_ID.toLong(), getString(R.string.movies))
                movieRowAdapter.addAll(0, movies)
                rowsAdapter.add(ListRow(header, movieRowAdapter))
            }
            if (hasTvShows) {
                val header = HeaderItem(TV_SHOW_HEADER_ID.toLong(), getString(R.string.tv_shows))
                tvShowRowAdapter.addAll(0, tvShows)
                rowsAdapter.add(ListRow(header, tvShowRowAdapter))
            }
        } else {
            clearAdapter()
        }
    }

    override fun onMoviesLoaded(movies: List<Work>?) {
        movies?.forEach { work ->
            if (movieRowAdapter.indexOf(work) == -1) {
                movieRowAdapter.add(work)
            }
        }
    }

    override fun onTvShowsLoaded(tvShows: List<Work>?) {
        tvShows?.forEach { work ->
            if (movieRowAdapter.indexOf(work) == -1) {
                movieRowAdapter.add(work)
            }
        }
    }

    override fun onBackdropImageLoaded(bitmap: Bitmap?) {
        backgroundManager.setBitmap(bitmap)
    }

    override fun getResultsAdapter(): ObjectAdapter? {
        return rowsAdapter
    }

    override fun onQueryTextChange(query: String): Boolean {
        searchQuery(query)
        return true
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        searchQuery(query)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            SEARCH_FRAGMENT_REQUEST_CODE -> {
                view?.requestFocus()
            }
        }
    }

    private fun setupUI() {
        setSearchResultProvider(this)
        setOnItemViewSelectedListener { _, item, _, row ->
            workSelected = item as Work?
            loadBackdropImage(true)

            when (row?.headerItem?.id?.toInt()) {
                MOVIE_HEADER_ID -> if (movieRowAdapter.indexOf(workSelected) >= movieRowAdapter.size() - 1) {
                    presenter.loadMovies()
                }
                TV_SHOW_HEADER_ID -> if (tvShowRowAdapter.indexOf(workSelected) >= tvShowRowAdapter.size() - 1) {
                    presenter.loadTvShows()
                }
            }
        }
        setOnItemViewClickedListener { itemViewHolder, item, _, _ ->
            val work = item as Work
            val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity!!,
                    (itemViewHolder?.view as ImageCardView).mainImageView,
                    WorkDetailsFragment.SHARED_ELEMENT_NAME
            ).toBundle()
            startActivityForResult(WorkDetailsActivity.newInstance(context, work), SEARCH_FRAGMENT_REQUEST_CODE, bundle)
        }
    }

    private fun searchQuery(query: String) {
        rowsAdapter.clear()
        progressBarManager.show()
        presenter.searchWorksByQuery(query)
    }

    private fun clearAdapter() {
        backgroundManager.setBitmap(null)
        rowsAdapter.clear()
        val listRowAdapter = ArrayObjectAdapter(WorkCardRenderer())
        val header = HeaderItem(0, getString(R.string.no_results))
        rowsAdapter.add(ListRow(header, listRowAdapter))
    }

    private fun loadBackdropImage(delay: Boolean) {
        workSelected?.let {
            backgroundTimer.cancel()
            backgroundTimer = Timer()
            backgroundTimer.schedule(object : TimerTask() {
                override fun run() {
                    handler.post {
                        presenter.loadBackdropImage(it)
                        backgroundTimer.cancel()
                    }
                }
            }, (BACKGROUND_UPDATE_DELAY.takeIf { delay } ?: 0).toLong())
        }
    }

    companion object {

        private const val SEARCH_FRAGMENT_REQUEST_CODE = 1
        private const val MOVIE_HEADER_ID = 1
        private const val TV_SHOW_HEADER_ID = 2
        private const val BACKGROUND_UPDATE_DELAY = 300

        private val handler = Handler()
        private val progressBarManager = ProgressBarManager()

        fun newInstance(): SearchFragment = SearchFragment()
    }
}