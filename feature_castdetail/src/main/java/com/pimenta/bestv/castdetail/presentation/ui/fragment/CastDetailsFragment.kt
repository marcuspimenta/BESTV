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

package com.pimenta.bestv.castdetail.presentation.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.os.bundleOf
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ClassPresenterSelector
import androidx.leanback.widget.DetailsOverviewRow
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter
import androidx.leanback.widget.FullWidthDetailsOverviewSharedElementHelper
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.RowPresenter
import com.pimenta.bestv.castdetail.R
import com.pimenta.bestv.castdetail.presentation.presenter.CastDetailsPresenter
import com.pimenta.bestv.castdetail.presentation.ui.activity.CastDetailsActivity
import com.pimenta.bestv.castdetail.presentation.ui.render.CastDetailsDescriptionRender
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.model.presentation.model.loadThumbnail
import com.pimenta.bestv.presentation.extension.addFragment
import com.pimenta.bestv.presentation.extension.isNotNullOrEmpty
import com.pimenta.bestv.presentation.extension.popBackStack
import com.pimenta.bestv.presentation.ui.fragment.ErrorFragment
import com.pimenta.bestv.presentation.ui.render.WorkCardRenderer
import com.pimenta.bestv.presentation.ui.setting.SettingShared
import com.pimenta.bestv.route.Route
import javax.inject.Inject

private const val CAST = "CAST"
private const val ERROR_FRAGMENT_REQUEST_CODE = 1
private const val ACTION_MOVIES = 1
private const val ACTION_TV_SHOWS = 2
private const val MOVIES_HEADER_ID = 1
private const val TV_SHOWS_HEADER_ID = 2

/**
 * Created by marcus on 04-04-2018.
 */
class CastDetailsFragment : DetailsSupportFragment(), CastDetailsPresenter.View {

    private val mainAdapter by lazy { ArrayObjectAdapter(presenterSelector) }
    private val actionAdapter by lazy { ArrayObjectAdapter() }
    private val moviesRowAdapter by lazy { ArrayObjectAdapter(WorkCardRenderer()) }
    private val tvShowsRowAdapter by lazy { ArrayObjectAdapter(WorkCardRenderer()) }
    private val presenterSelector by lazy { ClassPresenterSelector() }
    private val detailsOverviewRow by lazy { DetailsOverviewRow(castViewModel) }
    private val castViewModel by lazy { arguments?.getSerializable(CAST) as CastViewModel }

    @Inject
    lateinit var presenter: CastDetailsPresenter

    override fun onAttach(context: Context) {
        (requireActivity() as CastDetailsActivity).castDetailsActivityComponent
            .castDetailsFragmentComponent()
            .create(this)
            .inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.bindTo(this.lifecycle)

        setupDetailsOverviewRow()
        setupDetailsOverviewRowPresenter()
        adapter = mainAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBarManager.apply {
            enableProgressBar()
            setProgressBarView(
                LayoutInflater.from(context).inflate(R.layout.view_load, null).also {
                    (view.parent as ViewGroup).addView(it)
                }
            )
            initialDelay = 0
        }
        presenter.loadCastDetails(castViewModel)
    }

    override fun onShowProgress() {
        progressBarManager.show()
    }

    override fun onHideProgress() {
        progressBarManager.hide()
    }

    override fun onCastLoaded(castViewModel: CastViewModel?, movies: List<WorkViewModel>?, tvShow: List<WorkViewModel>?) {
        castViewModel?.let {
            detailsOverviewRow.item = it
            mainAdapter.notifyArrayItemRangeChanged(0, mainAdapter.size())
        }

        if (movies.isNotNullOrEmpty()) {
            actionAdapter.add(Action(ACTION_MOVIES.toLong(), resources.getString(R.string.movies)))
            moviesRowAdapter.addAll(0, movies)
            val moviesHeader = HeaderItem(MOVIES_HEADER_ID.toLong(), getString(R.string.movies))
            mainAdapter.add(ListRow(moviesHeader, moviesRowAdapter))
        }

        if (tvShow.isNotNullOrEmpty()) {
            actionAdapter.add(Action(ACTION_TV_SHOWS.toLong(), resources.getString(R.string.tv_shows)))
            tvShowsRowAdapter.addAll(0, tvShow)
            val tvShowsHeader = HeaderItem(TV_SHOWS_HEADER_ID.toLong(), getString(R.string.tv_shows))
            mainAdapter.add(ListRow(tvShowsHeader, tvShowsRowAdapter))
        }
    }

    override fun onErrorCastDetailsLoaded() {
        val fragment = ErrorFragment.newInstance().apply {
            setTargetFragment(this@CastDetailsFragment, ERROR_FRAGMENT_REQUEST_CODE)
        }
        requireActivity().addFragment(fragment, ErrorFragment.TAG)
    }

    override fun openWorkDetails(itemViewHolder: Presenter.ViewHolder, route: Route) {
        val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
            requireActivity(),
            (itemViewHolder.view as ImageCardView).mainImageView,
            SettingShared.SHARED_ELEMENT_NAME
        ).toBundle()
        startActivity(route.intent, bundle)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ERROR_FRAGMENT_REQUEST_CODE -> {
                requireActivity().popBackStack(ErrorFragment.TAG, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
                when (resultCode) {
                    Activity.RESULT_OK -> presenter.loadCastDetails(castViewModel)
                    else -> requireActivity().finish()
                }
            }
        }
    }

    private fun setupDetailsOverviewRow() {
        presenterSelector.addClassPresenter(ListRow::class.java, ListRowPresenter())

        castViewModel.loadThumbnail(requireContext()) {
            detailsOverviewRow.imageDrawable = it
            mainAdapter.notifyArrayItemRangeChanged(0, mainAdapter.size())
        }

        detailsOverviewRow.actionsAdapter = actionAdapter
        mainAdapter.add(detailsOverviewRow)

        setOnItemViewClickedListener { itemViewHolder, item, _, _ ->
            if (item is WorkViewModel) {
                presenter.workClicked(itemViewHolder, item)
            }
        }
    }

    private fun setupDetailsOverviewRowPresenter() {
        // Set detail background.
        val detailsPresenter = object : FullWidthDetailsOverviewRowPresenter(CastDetailsDescriptionRender()) {

            override fun createRowViewHolder(parent: ViewGroup): RowPresenter.ViewHolder {
                val viewHolder = super.createRowViewHolder(parent)
                val detailsImageView = viewHolder.view.findViewById<ImageView>(R.id.details_overview_image)
                val layoutParams = detailsImageView.layoutParams.apply {
                    width = resources.getDimensionPixelSize(R.dimen.movie_card_width)
                    height = resources.getDimensionPixelSize(R.dimen.movie_card_height)
                }
                detailsImageView.layoutParams = layoutParams
                return viewHolder
            }
        }.apply {
            actionsBackgroundColor = resources.getColor(R.color.detail_view_actionbar_background, requireActivity().theme)
            backgroundColor = resources.getColor(R.color.detail_view_background, requireActivity().theme)

            // Hook up transition element.
            val sharedElementHelper = FullWidthDetailsOverviewSharedElementHelper().apply {
                setSharedElementEnterTransition(requireActivity(), SettingShared.SHARED_ELEMENT_NAME)
            }
            setListener(sharedElementHelper)
            isParticipatingEntranceTransition = true
            setOnActionClickedListener {
                val position = actionAdapter.indexOf(it) + 1
                setSelectedPosition(position)
            }
        }
        presenterSelector.addClassPresenter(DetailsOverviewRow::class.java, detailsPresenter)
    }

    companion object {

        fun newInstance(castViewModel: CastViewModel) =
            CastDetailsFragment().apply {
                arguments = bundleOf(
                    CAST to castViewModel
                )
            }
    }
}
