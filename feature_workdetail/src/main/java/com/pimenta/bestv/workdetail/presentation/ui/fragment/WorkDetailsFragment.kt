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
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
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
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
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
import com.pimenta.bestv.workdetail.presentation.model.VideoViewModel
import com.pimenta.bestv.workdetail.presentation.presenter.WorkDetailsPresenter
import com.pimenta.bestv.workdetail.presentation.ui.activity.WorkDetailsActivity
import com.pimenta.bestv.workdetail.presentation.ui.render.CastCardRender
import com.pimenta.bestv.workdetail.presentation.ui.render.VideoCardRender
import com.pimenta.bestv.workdetail.presentation.ui.render.WorkDetailsDescriptionRender
import javax.inject.Inject

private const val WORK = "WORK"
private const val ERROR_FRAGMENT_REQUEST_CODE = 1
private const val ACTION_FAVORITE = 1
private const val ACTION_VIDEOS = 2
private const val ACTION_CAST = 3
private const val ACTION_RECOMMENDED = 4
private const val ACTION_SIMILAR = 5
private const val VIDEO_HEADER_ID = 1
private const val RECOMMENDED_HEADER_ID = 2
private const val SIMILAR_HEADER_ID = 3
private const val CAST_HEAD_ID = 4

/**
 * Created by marcus on 07-02-2018.
 */
class WorkDetailsFragment : DetailsSupportFragment(), WorkDetailsPresenter.View {

    private val actionAdapter: ArrayObjectAdapter by lazy { ArrayObjectAdapter() }
    private val videoRowAdapter: ArrayObjectAdapter by lazy { ArrayObjectAdapter(VideoCardRender()) }
    private val castRowAdapter: ArrayObjectAdapter by lazy { ArrayObjectAdapter(CastCardRender()) }
    private val recommendedRowAdapter: ArrayObjectAdapter by lazy { ArrayObjectAdapter(WorkCardRenderer()) }
    private val similarRowAdapter: ArrayObjectAdapter by lazy { ArrayObjectAdapter(WorkCardRenderer()) }
    private val mainAdapter: ArrayObjectAdapter by lazy { ArrayObjectAdapter(presenterSelector) }
    private val workDiffCallback: WorkDiffCallback by lazy { WorkDiffCallback() }
    private val presenterSelector: ClassPresenterSelector by lazy {
        ClassPresenterSelector().apply {
            addClassPresenter(ListRow::class.java, ListRowPresenter())
        }
    }
    private val detailsBackground: DetailsSupportFragmentBackgroundController by lazy {
        DetailsSupportFragmentBackgroundController(this).apply {
            enableParallax()
        }
    }
    private val workViewModel: WorkViewModel by lazy { arguments?.getSerializable(WORK) as WorkViewModel }

    @Inject
    lateinit var presenter: WorkDetailsPresenter

    private lateinit var favoriteAction: Action
    private lateinit var detailsOverviewRow: DetailsOverviewRow

    override fun onAttach(context: Context) {
        (requireActivity() as WorkDetailsActivity).workDetailsActivityComponent
                .workDetailsFragmentComponent()
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
                    })
            initialDelay = 0
        }
        presenter.loadDataByWork(workViewModel)
    }

    override fun onShowProgress() {
        progressBarManager.show()
    }

    override fun onHideProgress() {
        progressBarManager.hide()
    }

    override fun onResultSetFavoriteMovie(isFavorite: Boolean) {
        workViewModel.isFavorite = isFavorite
        favoriteAction.label1 = resources.getString(R.string.remove_favorites).takeIf { workViewModel.isFavorite }
                ?: run { resources.getString(R.string.save_favorites) }
        actionAdapter.notifyItemRangeChanged(actionAdapter.indexOf(favoriteAction), 1)
    }

    override fun onDataLoaded(isFavorite: Boolean, videos: List<VideoViewModel>?, casts: List<CastViewModel>?, recommendedWorks: List<WorkViewModel>, similarWorks: List<WorkViewModel>) {
        workViewModel.isFavorite = isFavorite
        favoriteAction = Action(
                ACTION_FAVORITE.toLong(),
                resources.getString(R.string.remove_favorites).takeIf { isFavorite }
                        ?: resources.getString(R.string.save_favorites)
        )
        actionAdapter.add(favoriteAction)

        if (videos.isNotNullOrEmpty()) {
            actionAdapter.add(Action(ACTION_VIDEOS.toLong(), resources.getString(R.string.videos)))
            videoRowAdapter.addAll(0, videos)
            mainAdapter.add(ListRow(HeaderItem(VIDEO_HEADER_ID.toLong(), getString(R.string.videos)), videoRowAdapter))
        }

        if (casts.isNotNullOrEmpty()) {
            actionAdapter.add(Action(ACTION_CAST.toLong(), resources.getString(R.string.cast)))
            castRowAdapter.addAll(0, casts)
            mainAdapter.add(ListRow(HeaderItem(CAST_HEAD_ID.toLong(), getString(R.string.cast)), castRowAdapter))
        }

        if (recommendedWorks.isNotEmpty()) {
            actionAdapter.add(Action(ACTION_RECOMMENDED.toLong(), resources.getString(R.string.recommended)))
            recommendedRowAdapter.setItems(recommendedWorks, workDiffCallback)
            mainAdapter.add(ListRow(HeaderItem(RECOMMENDED_HEADER_ID.toLong(), getString(R.string.recommended)), recommendedRowAdapter))
        }

        if (similarWorks.isNotEmpty()) {
            actionAdapter.add(Action(ACTION_SIMILAR.toLong(), resources.getString(R.string.similar)))
            similarRowAdapter.setItems(similarWorks, workDiffCallback)
            mainAdapter.add(ListRow(HeaderItem(SIMILAR_HEADER_ID.toLong(), getString(R.string.similar)), similarRowAdapter))
        }
    }

    override fun onRecommendationLoaded(works: List<WorkViewModel>) {
        recommendedRowAdapter.setItems(works, workDiffCallback)
    }

    override fun onSimilarLoaded(works: List<WorkViewModel>) {
        similarRowAdapter.setItems(works, workDiffCallback)
    }

    override fun onErrorWorkDetailsLoaded() {
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

    override fun openCastDetails(itemViewHolder: Presenter.ViewHolder, route: Route) {
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
                    Activity.RESULT_OK -> presenter.loadDataByWork(workViewModel)
                    else -> requireActivity().finish()
                }
            }
        }
    }

    private fun setupDetailsOverviewRow() {
        detailsOverviewRow = DetailsOverviewRow(workViewModel)

        workViewModel.loadPoster(requireContext(), object : CustomTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                detailsOverviewRow.imageDrawable = resource
                mainAdapter.notifyArrayItemRangeChanged(0, mainAdapter.size())
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                // DO ANYTHING
            }
        })

        workViewModel.loadBackdrop(requireContext(), object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                detailsBackground.coverBitmap = resource
                mainAdapter.notifyArrayItemRangeChanged(0, mainAdapter.size())
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                // DO ANYTHING
            }
        })

        detailsOverviewRow.actionsAdapter = actionAdapter
        mainAdapter.add(detailsOverviewRow)

        setOnItemViewSelectedListener(ItemViewSelectedListener())
        onItemViewClickedListener = ItemViewClickedListener()
    }

    private fun setupDetailsOverviewRowPresenter() {
        // Set detail background.
        val detailsPresenter = object : FullWidthDetailsOverviewRowPresenter(WorkDetailsDescriptionRender()) {

            private var mDetailsImageView: ImageView? = null

            override fun createRowViewHolder(parent: ViewGroup): RowPresenter.ViewHolder {
                val viewHolder = super.createRowViewHolder(parent)
                mDetailsImageView = viewHolder.view.findViewById(R.id.details_overview_image)
                val lp = mDetailsImageView?.layoutParams
                lp?.width = resources.getDimensionPixelSize(R.dimen.movie_width)
                lp?.height = resources.getDimensionPixelSize(R.dimen.movie_height)
                mDetailsImageView?.layoutParams = lp
                return viewHolder
            }
        }
        detailsPresenter.actionsBackgroundColor = resources.getColor(R.color.detail_view_actionbar_background, requireActivity().theme)
        detailsPresenter.backgroundColor = resources.getColor(R.color.detail_view_background, requireActivity().theme)

        // Hook up transition element.
        val sharedElementHelper = FullWidthDetailsOverviewSharedElementHelper()
        sharedElementHelper.setSharedElementEnterTransition(activity, SettingShared.SHARED_ELEMENT_NAME)
        detailsPresenter.setListener(sharedElementHelper)
        detailsPresenter.isParticipatingEntranceTransition = true
        detailsPresenter.setOnActionClickedListener { action ->
            var position = 0
            when (action.id.toInt()) {
                ACTION_FAVORITE -> presenter.setFavorite(workViewModel)
                ACTION_SIMILAR -> {
                    if (similarRowAdapter.size() > 0) {
                        position++
                    }
                    if (recommendedRowAdapter.size() > 0) {
                        position++
                    }
                    if (castRowAdapter.size() > 0) {
                        position++
                    }
                    if (videoRowAdapter.size() > 0) {
                        position++
                    }
                    setSelectedPosition(position)
                }
                ACTION_RECOMMENDED -> {
                    if (recommendedRowAdapter.size() > 0) {
                        position++
                    }
                    if (castRowAdapter.size() > 0) {
                        position++
                    }
                    if (videoRowAdapter.size() > 0) {
                        position++
                    }
                    setSelectedPosition(position)
                }
                ACTION_CAST -> {
                    if (castRowAdapter.size() > 0) {
                        position++
                    }
                    if (videoRowAdapter.size() > 0) {
                        position++
                    }
                    setSelectedPosition(position)
                }
                ACTION_VIDEOS -> {
                    if (videoRowAdapter.size() > 0) {
                        position++
                    }
                    setSelectedPosition(position)
                }
            }
        }
        presenterSelector.addClassPresenter(DetailsOverviewRow::class.java, detailsPresenter)
    }

    private inner class ItemViewSelectedListener : OnItemViewSelectedListener {

        override fun onItemSelected(viewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
            when (row?.run { id.toInt() }) {
                RECOMMENDED_HEADER_ID -> {
                    item?.let {
                        if (recommendedRowAdapter.indexOf(it) >= recommendedRowAdapter.size() - 1) {
                            presenter.loadRecommendationByWork(workViewModel)
                        }
                    }
                }
                SIMILAR_HEADER_ID -> {
                    item?.let {
                        if (similarRowAdapter.indexOf(it) >= similarRowAdapter.size() - 1) {
                            presenter.loadSimilarByWork(workViewModel)
                        }
                    }
                }
            }
        }
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {

        override fun onItemClicked(itemViewHolder: Presenter.ViewHolder, item: Any, rowViewHolder: RowPresenter.ViewHolder, row: Row?) {
            when (row?.run { id.toInt() }) {
                CAST_HEAD_ID -> {
                    if (item is CastViewModel) {
                        presenter.castClicked(itemViewHolder, item)
                    }
                }
                VIDEO_HEADER_ID -> {
                    val videoViewModel = item as VideoViewModel
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
                RECOMMENDED_HEADER_ID, SIMILAR_HEADER_ID -> {
                    if (item is WorkViewModel) {
                        presenter.workClicked(itemViewHolder, item)
                    }
                }
            }
        }
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
