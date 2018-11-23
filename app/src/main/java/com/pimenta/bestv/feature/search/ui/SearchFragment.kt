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
import android.support.v17.leanback.app.BackgroundManager
import android.support.v17.leanback.app.ProgressBarManager
import android.support.v17.leanback.app.SearchSupportFragment
import android.support.v17.leanback.widget.ArrayObjectAdapter
import android.support.v17.leanback.widget.HeaderItem
import android.support.v17.leanback.widget.ImageCardView
import android.support.v17.leanback.widget.ListRow
import android.support.v17.leanback.widget.ListRowPresenter
import android.support.v17.leanback.widget.ObjectAdapter
import android.support.v17.leanback.widget.OnItemViewClickedListener
import android.support.v17.leanback.widget.OnItemViewSelectedListener
import android.support.v17.leanback.widget.Presenter
import android.support.v17.leanback.widget.Row
import android.support.v17.leanback.widget.RowPresenter
import android.support.v4.app.ActivityOptionsCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.pimenta.bestv.BesTV
import com.pimenta.bestv.R
import com.pimenta.bestv.feature.workdetail.ui.WorkDetailsFragment
import com.pimenta.bestv.feature.search.presenter.SearchPresenter
import com.pimenta.bestv.repository.entity.Work
import com.pimenta.bestv.feature.workdetail.ui.WorkDetailsActivity
import com.pimenta.bestv.feature.base.BaseSearchFragment
import com.pimenta.bestv.feature.widget.WorkCardPresenter
import java.util.Timer
import java.util.TimerTask

import javax.inject.Inject

/**
 * Created by marcus on 12-03-2018.
 */
class SearchFragment : BaseSearchFragment(), SearchPresenter.View, SearchSupportFragment.SearchResultProvider {

    private lateinit var mRowsAdapter: ArrayObjectAdapter
    private lateinit var mMovieRowAdapter: ArrayObjectAdapter
    private lateinit var mTvShowRowAdapter: ArrayObjectAdapter
    private lateinit var mBackgroundManager: BackgroundManager

    private lateinit var mWorkSelected: Work
    private lateinit var mBackgroundTimer: Timer

    @Inject
    lateinit var mPresenter: SearchPresenter

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        BesTV.applicationComponent.inject(this)
        mPresenter.register(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        sProgressBarManager.setRootView(container)
        sProgressBarManager.enableProgressBar()
        sProgressBarManager.initialDelay = 0

        val view = super.onCreateView(inflater, container, savedInstanceState)
        view?.setBackgroundColor(resources.getColor(android.support.v17.leanback.R.color.lb_playback_controls_background_light))
        return view
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clearAdapter()
    }

    override fun onResume() {
        super.onResume()
        if (::mWorkSelected.isInitialized) {
            loadBackdropImage(false)
        }
    }

    override fun onDestroyView() {
        sProgressBarManager.hide()
        if (::mBackgroundTimer.isInitialized) {
            mBackgroundTimer.cancel()
        }
        super.onDestroyView()
    }

    override fun onDetach() {
        mPresenter.unRegister()
        super.onDetach()
    }

    override fun onResultLoaded(movies: List<Work>?, tvShows: List<Work>?) {
        val hasMovies = movies != null && movies.isNotEmpty()
        val hasTvShows = tvShows != null && tvShows.isNotEmpty()

        sProgressBarManager.hide()
        if (hasMovies || hasTvShows) {
            mRowsAdapter.clear()

            val workCardPresenter = WorkCardPresenter()
            workCardPresenter.setLoadWorkPosterListener { movie, imageView -> mPresenter.loadWorkPosterImage(movie, imageView) }

            if (hasMovies) {
                val header = HeaderItem(MOVIE_HEADER_ID.toLong(), getString(R.string.movies))
                mMovieRowAdapter = ArrayObjectAdapter(workCardPresenter)
                mMovieRowAdapter.addAll(0, movies!!)
                mRowsAdapter.add(ListRow(header, mMovieRowAdapter))
            }
            if (hasTvShows) {
                val header = HeaderItem(TV_SHOW_HEADER_ID.toLong(), getString(R.string.tv_shows))
                mTvShowRowAdapter = ArrayObjectAdapter(workCardPresenter)
                mTvShowRowAdapter.addAll(0, tvShows!!)
                mRowsAdapter.add(ListRow(header, mTvShowRowAdapter))
            }
        } else {
            clearAdapter()
        }
    }

    override fun onMoviesLoaded(movies: List<Work>?) {
        if (movies != null) {
            for (work in movies) {
                if (mMovieRowAdapter.indexOf(work) == -1) {
                    mMovieRowAdapter.add(work)
                }
            }
        }
    }

    override fun onTvShowsLoaded(tvShows: List<Work>?) {
        if (tvShows != null) {
            for (work in tvShows) {
                if (mTvShowRowAdapter.indexOf(work) == -1) {
                    mTvShowRowAdapter.add(work)
                }
            }
        }
    }

    override fun onBackdropImageLoaded(bitmap: Bitmap?) {
        mBackgroundManager.setBitmap(bitmap)
    }

    override fun getResultsAdapter(): ObjectAdapter? {
        return mRowsAdapter
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
                val view = view
                view?.requestFocus()
            }
        }
    }

    private fun setupUI() {
        mBackgroundManager = BackgroundManager.getInstance(activity)
        mBackgroundManager.attach(activity!!.window)
        activity!!.windowManager.defaultDisplay.getMetrics(mPresenter.displayMetrics)

        mRowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        setSearchResultProvider(this)
        setOnItemViewSelectedListener(ItemViewSelectedListener())
        setOnItemViewClickedListener(ItemViewClickedListener())
    }

    private fun searchQuery(query: String) {
        mRowsAdapter.clear()
        sProgressBarManager.show()
        mPresenter.searchWorksByQuery(query)
    }

    private fun clearAdapter() {
        mBackgroundManager.setBitmap(null)
        mRowsAdapter.clear()
        val listRowAdapter = ArrayObjectAdapter(WorkCardPresenter())
        val header = HeaderItem(0, getString(R.string.no_results))
        mRowsAdapter.add(ListRow(header, listRowAdapter))
    }

    private fun loadBackdropImage(delay: Boolean) {
        if (::mWorkSelected.isInitialized) {
            return
        }

        if (::mBackgroundTimer.isInitialized) {
            mBackgroundTimer.cancel()
        }
        mBackgroundTimer = Timer()
        mBackgroundTimer.schedule(object : TimerTask() {
            override fun run() {
                sHandler.post {
                    mPresenter.loadBackdropImage(mWorkSelected)
                    mBackgroundTimer.cancel()
                }
            }
        }, (if (delay) BACKGROUND_UPDATE_DELAY else 0).toLong())
    }

    private inner class ItemViewSelectedListener : OnItemViewSelectedListener {

        override fun onItemSelected(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
            if (item != null) {
                mWorkSelected = item as Work
                loadBackdropImage(true)
            }

            if (row!!.headerItem != null) {
                when (row.headerItem.id.toInt()) {
                    MOVIE_HEADER_ID -> if (::mWorkSelected.isInitialized && mMovieRowAdapter.indexOf(mWorkSelected) >= mMovieRowAdapter.size() - 1) {
                        mPresenter.loadMovies()
                    }
                    TV_SHOW_HEADER_ID -> if (::mWorkSelected.isInitialized && mTvShowRowAdapter.indexOf(mWorkSelected) >= mTvShowRowAdapter.size() - 1) {
                        mPresenter.loadTvShows()
                    }
                }
            }
        }
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {

        override fun onItemClicked(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
            val work = item as Work
            val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!,
                    (itemViewHolder!!.view as ImageCardView).mainImageView, WorkDetailsFragment.SHARED_ELEMENT_NAME).toBundle()
            startActivityForResult(WorkDetailsActivity.newInstance(context, work), SEARCH_FRAGMENT_REQUEST_CODE, bundle)
        }
    }

    companion object {

        val TAG = SearchFragment::class.java.simpleName

        private const val SEARCH_FRAGMENT_REQUEST_CODE = 1
        private const val MOVIE_HEADER_ID = 1
        private const val TV_SHOW_HEADER_ID = 2
        private const val BACKGROUND_UPDATE_DELAY = 300

        private val sHandler = Handler()
        private val sProgressBarManager = ProgressBarManager()

        fun newInstance(): SearchFragment = SearchFragment()
    }
}