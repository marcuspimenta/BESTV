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

package com.pimenta.bestv.feature.castdetail.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v17.leanback.widget.Action
import android.support.v17.leanback.widget.ArrayObjectAdapter
import android.support.v17.leanback.widget.ClassPresenterSelector
import android.support.v17.leanback.widget.DetailsOverviewRow
import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter
import android.support.v17.leanback.widget.FullWidthDetailsOverviewSharedElementHelper
import android.support.v17.leanback.widget.HeaderItem
import android.support.v17.leanback.widget.ImageCardView
import android.support.v17.leanback.widget.ListRow
import android.support.v17.leanback.widget.ListRowPresenter
import android.support.v17.leanback.widget.OnItemViewClickedListener
import android.support.v17.leanback.widget.Presenter
import android.support.v17.leanback.widget.Row
import android.support.v17.leanback.widget.RowPresenter
import android.support.v4.app.ActivityOptionsCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.pimenta.bestv.BesTV
import com.pimenta.bestv.R
import com.pimenta.bestv.feature.workdetail.ui.WorkDetailsFragment
import com.pimenta.bestv.feature.castdetail.presenter.CastDetailsPresenter
import com.pimenta.bestv.repository.entity.Cast
import com.pimenta.bestv.repository.entity.Work
import com.pimenta.bestv.feature.workdetail.ui.WorkDetailsActivity
import com.pimenta.bestv.feature.base.BaseDetailsFragment
import com.pimenta.bestv.feature.widget.CastDetailsDescriptionPresenter
import com.pimenta.bestv.feature.widget.WorkCardPresenter

import javax.inject.Inject

/**
 * Created by marcus on 04-04-2018.
 */
class CastDetailsFragment : BaseDetailsFragment(), CastDetailsPresenter.View {

    private lateinit var mAdapter: ArrayObjectAdapter
    private lateinit var mActionAdapter: ArrayObjectAdapter
    private lateinit var mMoviesRowAdapter: ArrayObjectAdapter
    private lateinit var mTvShowsRowAdapter: ArrayObjectAdapter
    private lateinit var mPresenterSelector: ClassPresenterSelector
    private lateinit var mDetailsOverviewRow: DetailsOverviewRow
    private lateinit var mCast: Cast

    @Inject
    lateinit var mPresenter: CastDetailsPresenter

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        BesTV.applicationComponent.inject(this)
        mPresenter.register(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCast = activity?.intent?.getSerializableExtra(CAST) as Cast

        setupDetailsOverviewRow()
        setupDetailsOverviewRowPresenter()
        adapter = mAdapter
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
        mPresenter.loadCastDetails(mCast)
    }

    override fun onDetach() {
        mPresenter.unRegister()
        super.onDetach()
    }

    override fun onCastLoaded(cast: Cast?, movies: List<Work>?, tvShow: List<Work>?) {
        progressBarManager.hide()
        if (cast != null) {
            mCast = cast
            mDetailsOverviewRow.item = cast
            mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size())
        }

        if (movies != null && movies.isNotEmpty()) {
            mActionAdapter.add(Action(ACTION_MOVIES.toLong(), resources.getString(R.string.movies)))
            val workCardPresenter = WorkCardPresenter()
            workCardPresenter.setLoadWorkPosterListener { movie, imageView -> mPresenter.loadWorkPosterImage(movie, imageView) }
            mMoviesRowAdapter = ArrayObjectAdapter(workCardPresenter)
            mMoviesRowAdapter.addAll(0, movies)
            val moviesHeader = HeaderItem(MOVIES_HEADER_ID.toLong(), getString(R.string.movies))
            mAdapter.add(ListRow(moviesHeader, mMoviesRowAdapter))
        }

        if (tvShow != null && tvShow.isNotEmpty()) {
            mActionAdapter.add(Action(ACTION_TV_SHOWS.toLong(), resources.getString(R.string.tv_shows)))
            val workCardPresenter = WorkCardPresenter()
            workCardPresenter.setLoadWorkPosterListener { movie, imageView -> mPresenter.loadWorkPosterImage(movie, imageView) }
            mTvShowsRowAdapter = ArrayObjectAdapter(workCardPresenter)
            mTvShowsRowAdapter.addAll(0, tvShow)
            val tvShowsHeader = HeaderItem(TV_SHOWS_HEADER_ID.toLong(), getString(R.string.tv_shows))
            mAdapter.add(ListRow(tvShowsHeader, mTvShowsRowAdapter))
        }
    }

    override fun onCardImageLoaded(resource: Drawable?) {
        mDetailsOverviewRow.imageDrawable = resource
        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size())
    }

    private fun setupDetailsOverviewRow() {
        mPresenterSelector = ClassPresenterSelector()
        mPresenterSelector.addClassPresenter(ListRow::class.java, ListRowPresenter())
        mAdapter = ArrayObjectAdapter(mPresenterSelector)

        mDetailsOverviewRow = DetailsOverviewRow(mCast)
        mPresenter.loadCastImage(mCast)

        mActionAdapter = ArrayObjectAdapter()
        mDetailsOverviewRow.actionsAdapter = mActionAdapter
        mAdapter.add(mDetailsOverviewRow)

        onItemViewClickedListener = ItemViewClickedListener()
    }

    private fun setupDetailsOverviewRowPresenter() {
        // Set detail background.
        val detailsPresenter = object : FullWidthDetailsOverviewRowPresenter(CastDetailsDescriptionPresenter()) {

            private lateinit var mDetailsImageView: ImageView

            override fun createRowViewHolder(parent: ViewGroup): RowPresenter.ViewHolder {
                val viewHolder = super.createRowViewHolder(parent)
                mDetailsImageView = viewHolder.view.findViewById(R.id.details_overview_image)
                val lp = mDetailsImageView.layoutParams
                lp.width = resources.getDimensionPixelSize(R.dimen.movie_card_width)
                lp.height = resources.getDimensionPixelSize(R.dimen.movie_card_height)
                mDetailsImageView.layoutParams = lp
                return viewHolder
            }
        }
        detailsPresenter.actionsBackgroundColor = resources.getColor(R.color.detail_view_actionbar_background, activity!!.theme)
        detailsPresenter.backgroundColor = resources.getColor(R.color.detail_view_background, activity!!.theme)

        // Hook up transition element.
        val sharedElementHelper = FullWidthDetailsOverviewSharedElementHelper()
        sharedElementHelper.setSharedElementEnterTransition(activity, SHARED_ELEMENT_NAME)
        detailsPresenter.setListener(sharedElementHelper)
        detailsPresenter.isParticipatingEntranceTransition = true
        detailsPresenter.setOnActionClickedListener { action ->
            var position = 0
            when (action.id.toInt()) {
                ACTION_TV_SHOWS -> {
                    if (::mTvShowsRowAdapter.isInitialized && mTvShowsRowAdapter.size() > 0) {
                        position++
                    }
                    if (::mMoviesRowAdapter.isInitialized && mMoviesRowAdapter.size() > 0) {
                        position++
                    }
                    setSelectedPosition(position)
                }
                ACTION_MOVIES -> {
                    if (::mMoviesRowAdapter.isInitialized && mMoviesRowAdapter.size() > 0) {
                        position++
                    }
                    setSelectedPosition(position)
                }
            }
        }
        mPresenterSelector.addClassPresenter(DetailsOverviewRow::class.java, detailsPresenter)
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {

        override fun onItemClicked(itemViewHolder: Presenter.ViewHolder, item: Any, rowViewHolder: RowPresenter.ViewHolder, row: Row?) {
            if (row != null && row.headerItem != null) {
                when (row.headerItem.id.toInt()) {
                    MOVIES_HEADER_ID, TV_SHOWS_HEADER_ID -> {
                        val work = item as Work
                        val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!, (itemViewHolder.view as ImageCardView).mainImageView,
                                WorkDetailsFragment.SHARED_ELEMENT_NAME).toBundle()
                        startActivity(WorkDetailsActivity.newInstance(context, work), bundle)
                    }
                }
            }
        }
    }

    companion object {

        const val TAG = "CastDetailsFragment"
        const val CAST = "CAST"
        const val SHARED_ELEMENT_NAME = "SHARED_ELEMENT_NAME"

        private const val ACTION_MOVIES = 1
        private const val ACTION_TV_SHOWS = 2
        private const val MOVIES_HEADER_ID = 1
        private const val TV_SHOWS_HEADER_ID = 2

        fun newInstance(): WorkDetailsFragment = WorkDetailsFragment()
    }
}