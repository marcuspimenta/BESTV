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
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import com.pimenta.bestv.R
import com.pimenta.bestv.common.extension.addFragment
import com.pimenta.bestv.common.extension.popBackStack
import com.pimenta.bestv.common.presentation.model.GenreViewModel
import com.pimenta.bestv.common.presentation.ui.fragment.ErrorFragment
import com.pimenta.bestv.data.MediaRepository
import com.pimenta.bestv.feature.main.di.WorkBrowseFragmentComponent
import com.pimenta.bestv.feature.main.presentation.presenter.WorkBrowsePresenter
import com.pimenta.bestv.feature.main.presentation.ui.headeritem.GenreHeaderItem
import com.pimenta.bestv.feature.main.presentation.ui.headeritem.WorkTypeHeaderItem
import com.pimenta.bestv.feature.search.presentation.ui.activity.SearchActivity
import javax.inject.Inject

private const val ERROR_FRAGMENT_REQUEST_CODE = 1
private const val TOP_WORK_LIST_ID = 1
private const val WORK_GENRE_ID = 2
private const val FAVORITE_INDEX = 0

/**
 * Created by marcus on 07-02-2018.
 */
class WorkBrowseFragment : BrowseSupportFragment(), WorkBrowsePresenter.View {

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

    private var hasFavorite = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        WorkBrowseFragmentComponent.create(this, requireActivity().application)
                .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.bindTo(this.lifecycle)

        setupUIElements()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBarManager.apply {
            enableProgressBar()
            setProgressBarView(
                    LayoutInflater.from(context).inflate(R.layout.view_load, null).also {
                        (view.parent as ViewGroup).addView(it)
                    })
            initialDelay = 0
        }

        adapter = rowsAdapter
        presenter.loadData()
    }

    override fun onResume() {
        super.onResume()
        if (rowsAdapter.size() > 0) {
            presenter.hasFavorite()
        }
    }

    override fun onShowProgress() {
        progressBarManager.show()
    }

    override fun onHideProgress() {
        progressBarManager.hide()
    }

    override fun onDataLoaded(hasFavoriteMovie: Boolean, movieGenres: List<GenreViewModel>?, tvShowGenres: List<GenreViewModel>?) {
        hasFavorite = hasFavoriteMovie
        if (hasFavorite) {
            rowsAdapter.add(favoritePageRow)
        }

        rowsAdapter.add(DividerRow())
        rowsAdapter.add(SectionRow(getString(R.string.movies)))
        rowsAdapter.add(PageRow(WorkTypeHeaderItem(TOP_WORK_LIST_ID, getString(MediaRepository.WorkType.NOW_PLAYING_MOVIES.resource), MediaRepository.WorkType.NOW_PLAYING_MOVIES)))
        rowsAdapter.add(PageRow(WorkTypeHeaderItem(TOP_WORK_LIST_ID, getString(MediaRepository.WorkType.POPULAR_MOVIES.resource), MediaRepository.WorkType.POPULAR_MOVIES)))
        rowsAdapter.add(PageRow(WorkTypeHeaderItem(TOP_WORK_LIST_ID, getString(MediaRepository.WorkType.TOP_RATED_MOVIES.resource), MediaRepository.WorkType.TOP_RATED_MOVIES)))
        rowsAdapter.add(PageRow(WorkTypeHeaderItem(TOP_WORK_LIST_ID, getString(MediaRepository.WorkType.UP_COMING_MOVIES.resource), MediaRepository.WorkType.UP_COMING_MOVIES)))

        movieGenres?.forEach {
            rowsAdapter.add(PageRow(GenreHeaderItem(WORK_GENRE_ID, it)))
        }

        rowsAdapter.add(DividerRow())
        rowsAdapter.add(SectionRow(getString(R.string.tv_shows)))
        rowsAdapter.add(PageRow(WorkTypeHeaderItem(TOP_WORK_LIST_ID, getString(MediaRepository.WorkType.AIRING_TODAY_TV_SHOWS.resource), MediaRepository.WorkType.AIRING_TODAY_TV_SHOWS)))
        rowsAdapter.add(PageRow(WorkTypeHeaderItem(TOP_WORK_LIST_ID, getString(MediaRepository.WorkType.ON_THE_AIR_TV_SHOWS.resource), MediaRepository.WorkType.ON_THE_AIR_TV_SHOWS)))
        rowsAdapter.add(PageRow(WorkTypeHeaderItem(TOP_WORK_LIST_ID, getString(MediaRepository.WorkType.TOP_RATED_TV_SHOWS.resource), MediaRepository.WorkType.TOP_RATED_TV_SHOWS)))
        rowsAdapter.add(PageRow(WorkTypeHeaderItem(TOP_WORK_LIST_ID, getString(MediaRepository.WorkType.POPULAR_TV_SHOWS.resource), MediaRepository.WorkType.POPULAR_TV_SHOWS)))

        tvShowGenres?.forEach {
            rowsAdapter.add(PageRow(GenreHeaderItem(WORK_GENRE_ID, it)))
        }

        startEntranceTransition()
    }

    override fun onHasFavorite(hasFavoriteMovie: Boolean) {
        hasFavorite = hasFavoriteMovie
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

    override fun onErrorDataLoaded() {
        val fragment = ErrorFragment.newInstance().apply {
            setTargetFragment(this@WorkBrowseFragment, ERROR_FRAGMENT_REQUEST_CODE)
        }
        activity?.addFragment(fragment, ErrorFragment.TAG)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ERROR_FRAGMENT_REQUEST_CODE -> {
                activity?.popBackStack(ErrorFragment.TAG, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
                if (resultCode == Activity.RESULT_OK) {
                    presenter.loadData()
                }
            }
        }
    }

    private fun setupUIElements() {
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        setOnSearchClickedListener {
            startActivity(SearchActivity.newInstance(context))
        }

        searchAffordanceColor = resources.getColor(R.color.background_color, activity!!.theme)
        mainFragmentRegistry.registerFragment(PageRow::class.java, PageRowFragmentFactory())

        prepareEntranceTransition()
    }

    private inner class PageRowFragmentFactory : BrowseSupportFragment.FragmentFactory<Fragment>() {

        override fun createFragment(rowObj: Any): Fragment {
            if (!hasFavorite && rowsAdapter.indexOf(favoritePageRow) == FAVORITE_INDEX) {
                rowsAdapter.remove(favoritePageRow)
            }

            val row = rowObj as Row
            when (row.headerItem.id.toInt()) {
                TOP_WORK_LIST_ID -> {
                    val movieListTypeHeaderItem = row.headerItem as WorkTypeHeaderItem
                    title = row.headerItem.name
                    return TopWorkGridFragment.newInstance(movieListTypeHeaderItem.movieListType)
                }
                WORK_GENRE_ID -> {
                    val genreHeaderItem = row.headerItem as GenreHeaderItem
                    title = genreHeaderItem.genreViewModel.name
                    return GenreWorkGridFragment.newInstance(genreHeaderItem.genreViewModel)
                }
            }

            throw IllegalArgumentException(String.format("Invalid row %s", rowObj))
        }
    }

    companion object {

        fun newInstance() = WorkBrowseFragment()
    }
}