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
import android.support.v17.leanback.app.BackgroundManager
import android.support.v17.leanback.app.BrowseSupportFragment
import android.support.v17.leanback.widget.*
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pimenta.bestv.BesTV
import com.pimenta.bestv.R
import com.pimenta.bestv.feature.base.BaseBrowseFragment
import com.pimenta.bestv.feature.search.ui.SearchActivity
import com.pimenta.bestv.feature.widget.headeritem.GenreHeaderItem
import com.pimenta.bestv.feature.widget.headeritem.WorkTypeHeaderItem
import com.pimenta.bestv.feature.workbrowse.presenter.WorkBrowsePresenter
import com.pimenta.bestv.feature.workbrowse.presenter.WorkBrowsePresenter.WorkBrowseView
import com.pimenta.bestv.repository.MediaRepository
import com.pimenta.bestv.repository.entity.MovieGenre
import com.pimenta.bestv.repository.entity.TvShowGenre
import javax.inject.Inject

/**
 * Created by marcus on 07-02-2018.
 */
class WorkBrowseFragment : BaseBrowseFragment(), WorkBrowseView {

    private var countFragment = 0
    private var showProgress = false
    private var hasFavorite = false

    private val backgroundManager: BackgroundManager by lazy { BackgroundManager.getInstance(activity) }
    private val rowsAdapter: ArrayObjectAdapter by lazy { ArrayObjectAdapter(ListRowPresenter()) }
    private val favoritePageRow: PageRow by lazy {
        PageRow(
                WorkTypeHeaderItem(
                        TOP_WORK_LIST_ID,
                        getString(MediaRepository.WorkType.FAVORITES_MOVIES.resource),
                        MediaRepository.WorkType.FAVORITES_MOVIES
                )
        )
    }

    @Inject
    lateinit var presenter: WorkBrowsePresenter

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        BesTV.applicationComponent.inject(this)
        presenter.register(this)
        activity?.let {
            backgroundManager.attach(it.window)
            it.windowManager.defaultDisplay.getMetrics(presenter.displayMetrics)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUIElements()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        progressBarManager.setRootView(container)
        progressBarManager.enableProgressBar()
        progressBarManager.initialDelay = 0
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBarManager.show()
        adapter = rowsAdapter
        presenter.loadData()
    }

    override fun onResume() {
        super.onResume()
        if (rowsAdapter.size() > 0) {
            presenter.hasFavorite()
        }
    }

    override fun onDestroy() {
        progressBarManager.hide()
        super.onDestroy()
    }

    override fun onDetach() {
        backgroundManager.release()
        presenter.unRegister()
        super.onDetach()
    }

    override fun onDataLoaded(hasFavorite: Boolean, movieGenres: List<MovieGenre>?, tvShowGenres: List<TvShowGenre>?) {
        this.hasFavorite = hasFavorite
        if (hasFavorite) {
            rowsAdapter.add(favoritePageRow)
        }

        rowsAdapter.add(DividerRow())
        rowsAdapter.add(SectionRow(getString(R.string.movies)))
        rowsAdapter.add(PageRow(WorkTypeHeaderItem(TOP_WORK_LIST_ID, getString(MediaRepository.WorkType.NOW_PLAYING_MOVIES.resource), MediaRepository.WorkType.NOW_PLAYING_MOVIES)))
        rowsAdapter.add(PageRow(WorkTypeHeaderItem(TOP_WORK_LIST_ID, getString(MediaRepository.WorkType.POPULAR_MOVIES.resource), MediaRepository.WorkType.POPULAR_MOVIES)))
        rowsAdapter.add(PageRow(WorkTypeHeaderItem(TOP_WORK_LIST_ID, getString(MediaRepository.WorkType.TOP_RATED_MOVIES.resource), MediaRepository.WorkType.TOP_RATED_MOVIES)))
        rowsAdapter.add(PageRow(WorkTypeHeaderItem(TOP_WORK_LIST_ID, getString(MediaRepository.WorkType.UP_COMING_MOVIES.resource), MediaRepository.WorkType.UP_COMING_MOVIES)))

        movieGenres?.forEach { genre ->
            rowsAdapter.add(PageRow(GenreHeaderItem(WORK_GENRE_ID, genre)))
        }

        rowsAdapter.add(DividerRow())
        rowsAdapter.add(SectionRow(getString(R.string.tv_shows)))
        rowsAdapter.add(PageRow(WorkTypeHeaderItem(TOP_WORK_LIST_ID, getString(MediaRepository.WorkType.AIRING_TODAY_TV_SHOWS.resource), MediaRepository.WorkType.AIRING_TODAY_TV_SHOWS)))
        rowsAdapter.add(PageRow(WorkTypeHeaderItem(TOP_WORK_LIST_ID, getString(MediaRepository.WorkType.ON_THE_AIR_TV_SHOWS.resource), MediaRepository.WorkType.ON_THE_AIR_TV_SHOWS)))
        rowsAdapter.add(PageRow(WorkTypeHeaderItem(TOP_WORK_LIST_ID, getString(MediaRepository.WorkType.TOP_RATED_TV_SHOWS.resource), MediaRepository.WorkType.TOP_RATED_TV_SHOWS)))
        rowsAdapter.add(PageRow(WorkTypeHeaderItem(TOP_WORK_LIST_ID, getString(MediaRepository.WorkType.POPULAR_TV_SHOWS.resource), MediaRepository.WorkType.POPULAR_TV_SHOWS)))

        tvShowGenres?.forEach { genre ->
            rowsAdapter.add(PageRow(GenreHeaderItem(WORK_GENRE_ID, genre)))
        }

        progressBarManager.hide()
        startEntranceTransition()
    }

    override fun onHasFavorite(hasFavorite: Boolean) {
        this.hasFavorite = hasFavorite
        if (hasFavorite) {
            if (rowsAdapter.indexOf(favoritePageRow) == -1) {
                rowsAdapter.add(FAVORITE_INDEX, favoritePageRow)
            }
        } else {
            if (rowsAdapter.indexOf(favoritePageRow) == FAVORITE_INDEX) {
                if (selectedPosition == FAVORITE_INDEX) {
                    selectedPosition = FAVORITE_INDEX + 3
                }
            }
        }
    }

    private fun setupUIElements() {
        headersState = BrowseSupportFragment.HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        setOnSearchClickedListener {
            startActivity(SearchActivity.newInstance(context!!))
        }

        searchAffordanceColor = resources.getColor(R.color.background_color, activity!!.theme)
        mainFragmentRegistry.registerFragment(PageRow::class.java, PageRowFragmentFactory())

        prepareEntranceTransition()
    }

    private inner class PageRowFragmentFactory : BrowseSupportFragment.FragmentFactory<Fragment>() {

        override fun createFragment(rowObj: Any): Fragment {
            if (countFragment++ >= 1) {
                showProgress = true
            }
            if (!hasFavorite && rowsAdapter.indexOf(favoritePageRow) == FAVORITE_INDEX) {
                rowsAdapter.remove(favoritePageRow)
            }

            val row = rowObj as Row
            when (row.headerItem.id.toInt()) {
                TOP_WORK_LIST_ID -> {
                    val movieListTypeHeaderItem = row.headerItem as WorkTypeHeaderItem
                    title = row.headerItem.name
                    return TopWorkGridFragment.newInstance(movieListTypeHeaderItem.movieListType, showProgress)
                }
                WORK_GENRE_ID -> {
                    val genreHeaderItem = row.headerItem as GenreHeaderItem
                    title = genreHeaderItem.genre.name
                    return GenreWorkGridFragment.newInstance(genreHeaderItem.genre, showProgress)
                }
            }

            throw IllegalArgumentException(String.format("Invalid row %s", rowObj))
        }
    }

    companion object {

        private const val TOP_WORK_LIST_ID = 1
        private const val WORK_GENRE_ID = 2
        private const val FAVORITE_INDEX = 0

        fun newInstance() = WorkBrowseFragment()
    }
}