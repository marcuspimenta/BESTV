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
import com.pimenta.bestv.feature.widget.render.CastDetailsDescriptionRender
import com.pimenta.bestv.feature.widget.render.WorkCardRenderer

import javax.inject.Inject

/**
 * Created by marcus on 04-04-2018.
 */
class CastDetailsFragment : BaseDetailsFragment(), CastDetailsPresenter.View {

    private lateinit var mainAdapter: ArrayObjectAdapter
    private lateinit var actionAdapter: ArrayObjectAdapter
    private lateinit var moviesRowAdapter: ArrayObjectAdapter
    private lateinit var tvShowsRowAdapter: ArrayObjectAdapter
    private lateinit var presenterSelector: ClassPresenterSelector
    private lateinit var detailsOverviewRow: DetailsOverviewRow
    private lateinit var cast: Cast

    @Inject
    lateinit var presenter: CastDetailsPresenter

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        BesTV.applicationComponent.inject(this)
        presenter.register(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cast = activity?.intent?.getSerializableExtra(CAST) as Cast

        setupDetailsOverviewRow()
        setupDetailsOverviewRowPresenter()
        adapter = mainAdapter
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
        presenter.loadCastDetails(cast)
    }

    override fun onDetach() {
        presenter.unRegister()
        super.onDetach()
    }

    override fun onCastLoaded(cast: Cast?, movies: List<Work>?, tvShow: List<Work>?) {
        progressBarManager.hide()
        if (cast != null) {
            this.cast = cast
            detailsOverviewRow.item = cast
            mainAdapter.notifyArrayItemRangeChanged(0, mainAdapter.size())
        }

        if (movies != null && movies.isNotEmpty()) {
            actionAdapter.add(Action(ACTION_MOVIES.toLong(), resources.getString(R.string.movies)))
            moviesRowAdapter = ArrayObjectAdapter(WorkCardRenderer())
            moviesRowAdapter.addAll(0, movies)
            val moviesHeader = HeaderItem(MOVIES_HEADER_ID.toLong(), getString(R.string.movies))
            mainAdapter.add(ListRow(moviesHeader, moviesRowAdapter))
        }

        if (tvShow != null && tvShow.isNotEmpty()) {
            actionAdapter.add(Action(ACTION_TV_SHOWS.toLong(), resources.getString(R.string.tv_shows)))
            tvShowsRowAdapter = ArrayObjectAdapter(WorkCardRenderer())
            tvShowsRowAdapter.addAll(0, tvShow)
            val tvShowsHeader = HeaderItem(TV_SHOWS_HEADER_ID.toLong(), getString(R.string.tv_shows))
            mainAdapter.add(ListRow(tvShowsHeader, tvShowsRowAdapter))
        }
    }

    override fun onCardImageLoaded(resource: Drawable?) {
        detailsOverviewRow.imageDrawable = resource
        mainAdapter.notifyArrayItemRangeChanged(0, mainAdapter.size())
    }

    private fun setupDetailsOverviewRow() {
        presenterSelector = ClassPresenterSelector()
        presenterSelector.addClassPresenter(ListRow::class.java, ListRowPresenter())
        mainAdapter = ArrayObjectAdapter(presenterSelector)

        detailsOverviewRow = DetailsOverviewRow(cast)
        presenter.loadCastImage(cast)

        actionAdapter = ArrayObjectAdapter()
        detailsOverviewRow.actionsAdapter = actionAdapter
        mainAdapter.add(detailsOverviewRow)

        onItemViewClickedListener = ItemViewClickedListener()
    }

    private fun setupDetailsOverviewRowPresenter() {
        // Set detail background.
        val detailsPresenter = object : FullWidthDetailsOverviewRowPresenter(CastDetailsDescriptionRender()) {

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
                    if (::tvShowsRowAdapter.isInitialized && tvShowsRowAdapter.size() > 0) {
                        position++
                    }
                    if (::moviesRowAdapter.isInitialized && moviesRowAdapter.size() > 0) {
                        position++
                    }
                    setSelectedPosition(position)
                }
                ACTION_MOVIES -> {
                    if (::moviesRowAdapter.isInitialized && moviesRowAdapter.size() > 0) {
                        position++
                    }
                    setSelectedPosition(position)
                }
            }
        }
        presenterSelector.addClassPresenter(DetailsOverviewRow::class.java, detailsPresenter)
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {

        override fun onItemClicked(itemViewHolder: Presenter.ViewHolder, item: Any, rowViewHolder: RowPresenter.ViewHolder, row: Row?) {
            when (row?.headerItem?.id?.toInt()) {
                MOVIES_HEADER_ID, TV_SHOWS_HEADER_ID -> {
                    val work = item as Work
                    val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            activity!!,
                            (itemViewHolder.view as ImageCardView).mainImageView,
                            WorkDetailsFragment.SHARED_ELEMENT_NAME
                    ).toBundle()
                    startActivity(WorkDetailsActivity.newInstance(context, work), bundle)
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

        fun newInstance() = WorkDetailsFragment()
    }
}