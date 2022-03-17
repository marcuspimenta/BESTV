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

package com.pimenta.bestv.workdetail.presentation.ui.fragment

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.os.bundleOf
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.app.DetailsSupportFragmentBackgroundController
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
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.OnItemViewSelectedListener
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.Row
import androidx.leanback.widget.RowPresenter
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.model.presentation.model.loadBackdrop
import com.pimenta.bestv.model.presentation.model.loadPoster
import com.pimenta.bestv.presentation.extension.addFragment
import com.pimenta.bestv.presentation.extension.isNotNullOrEmpty
import com.pimenta.bestv.presentation.extension.popBackStack
import com.pimenta.bestv.presentation.ui.diffcallback.WorkDiffCallback
import com.pimenta.bestv.presentation.ui.fragment.ErrorFragment
import com.pimenta.bestv.presentation.ui.render.WorkCardRenderer
import com.pimenta.bestv.presentation.ui.setting.SettingShared
import com.pimenta.bestv.route.Route
import com.pimenta.bestv.workdetail.R
import com.pimenta.bestv.workdetail.presentation.model.ReviewViewModel
import com.pimenta.bestv.workdetail.presentation.model.VideoViewModel
import com.pimenta.bestv.workdetail.presentation.presenter.WorkDetailsPresenter
import com.pimenta.bestv.workdetail.presentation.ui.activity.WorkDetailsActivity
import com.pimenta.bestv.workdetail.presentation.ui.diffcallback.ReviewDiffCallback
import com.pimenta.bestv.workdetail.presentation.ui.render.CastCardRender
import com.pimenta.bestv.workdetail.presentation.ui.render.ReviewCardRender
import com.pimenta.bestv.workdetail.presentation.ui.render.VideoCardRender
import com.pimenta.bestv.workdetail.presentation.ui.render.WorkDetailsDescriptionRender
import javax.inject.Inject

private const val WORK = "WORK"
private const val ERROR_FRAGMENT_REQUEST_CODE = 1
private const val ACTION_FAVORITE = 1
private const val ACTION_REVIEWS = 2
private const val ACTION_VIDEOS = 3
private const val ACTION_CAST = 4
private const val ACTION_RECOMMENDED = 5
private const val ACTION_SIMILAR = 6
private const val REVIEW_HEADER_ID = 1
private const val VIDEO_HEADER_ID = 2
private const val RECOMMENDED_HEADER_ID = 4
private const val SIMILAR_HEADER_ID = 5
private const val CAST_HEAD_ID = 6

/**
 * Created by marcus on 07-02-2018.
 */
class WorkDetailsFragment :
    DetailsSupportFragment(),
    WorkDetailsPresenter.View,
    OnItemViewSelectedListener,
    OnItemViewClickedListener {

    private val actionAdapter by lazy { ArrayObjectAdapter() }
    private val reviewRowAdapter by lazy { ArrayObjectAdapter(ReviewCardRender()) }
    private val videoRowAdapter by lazy { ArrayObjectAdapter(VideoCardRender()) }
    private val castRowAdapter by lazy { ArrayObjectAdapter(CastCardRender()) }
    private val recommendedRowAdapter by lazy { ArrayObjectAdapter(WorkCardRenderer()) }
    private val similarRowAdapter by lazy { ArrayObjectAdapter(WorkCardRenderer()) }
    private val mainAdapter by lazy { ArrayObjectAdapter(presenterSelector) }
    private val workDiffCallback by lazy { WorkDiffCallback() }
    private val reviewDiffCallback by lazy { ReviewDiffCallback() }
    private val presenterSelector by lazy {
        ClassPresenterSelector().apply {
            addClassPresenter(ListRow::class.java, ListRowPresenter())
        }
    }
    private val detailsBackground by lazy {
        DetailsSupportFragmentBackgroundController(this).apply {
            enableParallax()
        }
    }
    private val workViewModel by lazy { arguments?.getSerializable(WORK) as WorkViewModel }

    @Inject
    lateinit var presenter: WorkDetailsPresenter

    private lateinit var favoriteAction: Action
    private lateinit var detailsOverviewRow: DetailsOverviewRow

    override fun onAttach(context: Context) {
        (requireActivity() as WorkDetailsActivity).workDetailsActivityComponent
            .workDetailsFragmentComponent()
            .create(this, workViewModel)
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
        presenter.loadData()
    }

    override fun showProgress() {
        progressBarManager.show()
    }

    override fun hideProgress() {
        progressBarManager.hide()
    }

    override fun resultSetFavoriteMovie(isFavorite: Boolean) {
        favoriteAction.label1 = resources.getString(R.string.remove_favorites).takeIf { isFavorite }
            ?: run { resources.getString(R.string.save_favorites) }
        actionAdapter.notifyItemRangeChanged(actionAdapter.indexOf(favoriteAction), 1)
    }

    override fun dataLoaded(
        isFavorite: Boolean,
        reviews: List<ReviewViewModel>?,
        videos: List<VideoViewModel>?,
        casts: List<CastViewModel>?,
        recommendedWorks: List<WorkViewModel>,
        similarWorks: List<WorkViewModel>
    ) {
        favoriteAction = Action(
            ACTION_FAVORITE.toLong(),
            resources.getString(R.string.remove_favorites).takeIf { isFavorite }
                ?: resources.getString(R.string.save_favorites)
        )
        actionAdapter.add(favoriteAction)

        if (reviews.isNotNullOrEmpty()) {
            actionAdapter.add(Action(ACTION_REVIEWS.toLong(), getString(R.string.reviews)))
            reviewRowAdapter.setItems(reviews, reviewDiffCallback)
            mainAdapter.add(ListRow(HeaderItem(REVIEW_HEADER_ID.toLong(), getString(R.string.reviews)), reviewRowAdapter))
        }

        if (videos.isNotNullOrEmpty()) {
            actionAdapter.add(Action(ACTION_VIDEOS.toLong(), getString(R.string.videos)))
            videoRowAdapter.addAll(0, videos)
            mainAdapter.add(ListRow(HeaderItem(VIDEO_HEADER_ID.toLong(), getString(R.string.videos)), videoRowAdapter))
        }

        if (casts.isNotNullOrEmpty()) {
            actionAdapter.add(Action(ACTION_CAST.toLong(), getString(R.string.cast)))
            castRowAdapter.addAll(0, casts)
            mainAdapter.add(ListRow(HeaderItem(CAST_HEAD_ID.toLong(), getString(R.string.cast)), castRowAdapter))
        }

        if (recommendedWorks.isNotEmpty()) {
            actionAdapter.add(Action(ACTION_RECOMMENDED.toLong(), getString(R.string.recommended)))
            recommendedRowAdapter.setItems(recommendedWorks, workDiffCallback)
            mainAdapter.add(ListRow(HeaderItem(RECOMMENDED_HEADER_ID.toLong(), getString(R.string.recommended)), recommendedRowAdapter))
        }

        if (similarWorks.isNotEmpty()) {
            actionAdapter.add(Action(ACTION_SIMILAR.toLong(), getString(R.string.similar)))
            similarRowAdapter.setItems(similarWorks, workDiffCallback)
            mainAdapter.add(ListRow(HeaderItem(SIMILAR_HEADER_ID.toLong(), getString(R.string.similar)), similarRowAdapter))
        }
    }

    override fun reviewLoaded(reviews: List<ReviewViewModel>) {
        reviewRowAdapter.setItems(reviews, reviewDiffCallback)
    }

    override fun recommendationLoaded(works: List<WorkViewModel>) {
        recommendedRowAdapter.setItems(works, workDiffCallback)
    }

    override fun similarLoaded(works: List<WorkViewModel>) {
        similarRowAdapter.setItems(works, workDiffCallback)
    }

    override fun errorWorkDetailsLoaded() {
        val fragment = ErrorFragment.newInstance().apply {
            setTargetFragment(this@WorkDetailsFragment, ERROR_FRAGMENT_REQUEST_CODE)
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

    override fun openCastDetails(itemViewHolder: Presenter.ViewHolder, intent: Intent) {
        val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
            requireActivity(),
            (itemViewHolder.view as ImageCardView).mainImageView,
            SettingShared.SHARED_ELEMENT_NAME
        ).toBundle()
        startActivity(intent, bundle)
    }

    override fun openVideo(videoViewModel: VideoViewModel) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(videoViewModel.youtubeUrl)
        )
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), R.string.failed_open_video, Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ERROR_FRAGMENT_REQUEST_CODE -> {
                requireActivity().popBackStack(ErrorFragment.TAG, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
                when (resultCode) {
                    Activity.RESULT_OK -> presenter.loadData()
                    else -> requireActivity().finish()
                }
            }
        }
    }

    override fun onItemSelected(viewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
        when (row?.run { id.toInt() }) {
            REVIEW_HEADER_ID -> item?.let { presenter.reviewItemSelected(it as ReviewViewModel) }
            RECOMMENDED_HEADER_ID -> item?.let { presenter.recommendationItemSelected(it as WorkViewModel) }
            SIMILAR_HEADER_ID -> item?.let { presenter.similarItemSelected(it as WorkViewModel) }
        }
    }

    override fun onItemClicked(itemViewHolder: Presenter.ViewHolder, item: Any, rowViewHolder: RowPresenter.ViewHolder, row: Row?) {
        when (row?.run { id.toInt() }) {
            CAST_HEAD_ID -> presenter.castClicked(itemViewHolder, item as CastViewModel)
            VIDEO_HEADER_ID -> presenter.videoClicked(item as VideoViewModel)
            RECOMMENDED_HEADER_ID, SIMILAR_HEADER_ID -> presenter.workClicked(itemViewHolder, item as WorkViewModel)
        }
    }

    private fun setupDetailsOverviewRow() {
        detailsOverviewRow = DetailsOverviewRow(workViewModel)

        workViewModel.loadPoster(requireContext()) {
            detailsOverviewRow.imageDrawable = it
            mainAdapter.notifyArrayItemRangeChanged(0, mainAdapter.size())
        }

        workViewModel.loadBackdrop(requireContext()) {
            detailsBackground.coverBitmap = it
            mainAdapter.notifyArrayItemRangeChanged(0, mainAdapter.size())
        }

        detailsOverviewRow.actionsAdapter = actionAdapter
        mainAdapter.add(detailsOverviewRow)

        setOnItemViewSelectedListener(this)
        onItemViewClickedListener = this
    }

    private fun setupDetailsOverviewRowPresenter() {
        // Set detail background.
        val detailsPresenter = object : FullWidthDetailsOverviewRowPresenter(WorkDetailsDescriptionRender()) {

            override fun createRowViewHolder(parent: ViewGroup): RowPresenter.ViewHolder {
                val viewHolder = super.createRowViewHolder(parent)
                val detailsImageView = viewHolder.view.findViewById<ImageView>(R.id.details_overview_image)
                val layoutParams = detailsImageView?.layoutParams?.apply {
                    width = resources.getDimensionPixelSize(R.dimen.movie_width)
                    height = resources.getDimensionPixelSize(R.dimen.movie_height)
                }
                detailsImageView?.layoutParams = layoutParams
                return viewHolder
            }
        }.apply {
            // Hook up transition element.
            val sharedElementHelper = FullWidthDetailsOverviewSharedElementHelper().apply {
                setSharedElementEnterTransition(activity, SettingShared.SHARED_ELEMENT_NAME)
            }

            actionsBackgroundColor = resources.getColor(R.color.detail_view_actionbar_background, requireActivity().theme)
            backgroundColor = resources.getColor(R.color.detail_view_background, requireActivity().theme)

            setListener(sharedElementHelper)
            isParticipatingEntranceTransition = true
            setOnActionClickedListener { action ->
                when (val position = actionAdapter.indexOf(action)) {
                    0 -> presenter.setFavorite()
                    else -> setSelectedPosition(position)
                }
            }
        }
        presenterSelector.addClassPresenter(DetailsOverviewRow::class.java, detailsPresenter)
    }

    companion object {

        fun newInstance(workViewModel: WorkViewModel) =
            WorkDetailsFragment().apply {
                arguments = bundleOf(
                    WORK to workViewModel
                )
            }
    }
}
