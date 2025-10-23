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
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.pimenta.bestv.presentation.extension.popBackStack
import com.pimenta.bestv.presentation.ui.diffcallback.WorkDiffCallback
import com.pimenta.bestv.presentation.ui.fragment.ErrorFragment
import com.pimenta.bestv.presentation.ui.render.WorkCardRenderer
import com.pimenta.bestv.presentation.ui.setting.SettingShared
import com.pimenta.bestv.presentation.R as presentationR
import com.pimenta.bestv.workdetail.R as workdetailR
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.pimenta.bestv.workdetail.presentation.model.ReviewViewModel
import com.pimenta.bestv.workdetail.presentation.model.VideoViewModel
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEffect
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState
import com.pimenta.bestv.workdetail.presentation.ui.activity.WorkDetailsActivity
import com.pimenta.bestv.workdetail.presentation.ui.diffcallback.ReviewDiffCallback
import com.pimenta.bestv.workdetail.presentation.ui.render.CastCardRender
import com.pimenta.bestv.workdetail.presentation.ui.render.ReviewCardRender
import com.pimenta.bestv.workdetail.presentation.ui.render.VideoCardRender
import com.pimenta.bestv.workdetail.presentation.ui.render.WorkDetailsDescriptionRender
import com.pimenta.bestv.workdetail.presentation.viewmodel.WorkDetailsViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val WORK = "WORK"
private const val ERROR_FRAGMENT_REQUEST_CODE = 1
private const val ACTION_FAVORITE = 1L
private const val ACTION_REVIEWS = 2L
private const val ACTION_VIDEOS = 3L
private const val ACTION_CAST = 4L
private const val ACTION_RECOMMENDED = 5L
private const val ACTION_SIMILAR = 6L
private const val REVIEW_HEADER_ID = 1
private const val VIDEO_HEADER_ID = 2
private const val RECOMMENDED_HEADER_ID = 4
private const val SIMILAR_HEADER_ID = 5
private const val CAST_HEAD_ID = 6

/**
 * Created by marcus on 07-02-2018.
 */
class WorkDetailsFragment : DetailsSupportFragment(), OnItemViewSelectedListener,
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

    @Inject lateinit var viewModel: WorkDetailsViewModel

    private lateinit var detailsOverviewRow: DetailsOverviewRow

    override fun onAttach(context: Context) {
        (requireActivity() as WorkDetailsActivity).workDetailsActivityComponent
            .workDetailsFragmentComponent()
            .create(workViewModel)
            .inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupDetailsOverviewRow()
        setupDetailsOverviewRowPresenter()
        adapter = mainAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBarManager.apply {
            enableProgressBar()
            setProgressBarView(
                LayoutInflater.from(context).inflate(presentationR.layout.view_load, null).also {
                    (view.parent as ViewGroup).addView(it)
                }
            )
            initialDelay = 0
        }

        observeState()
        observeEffects()
        viewModel.handleEvent(WorkDetailsEvent.LoadData)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ERROR_FRAGMENT_REQUEST_CODE -> {
                requireActivity().popBackStack(ErrorFragment.TAG, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
                when (resultCode) {
                    Activity.RESULT_OK -> viewModel.handleEvent(WorkDetailsEvent.LoadData)
                    else -> requireActivity().finish()
                }
            }
        }
    }

    override fun onItemSelected(viewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
        when (row?.id?.toInt()) {
            REVIEW_HEADER_ID -> item?.let { viewModel.handleEvent(WorkDetailsEvent.ReviewItemSelected(it as ReviewViewModel)) }
            RECOMMENDED_HEADER_ID -> item?.let { viewModel.handleEvent(WorkDetailsEvent.RecommendationItemSelected(it as WorkViewModel)) }
            SIMILAR_HEADER_ID -> item?.let { viewModel.handleEvent(WorkDetailsEvent.SimilarItemSelected(it as WorkViewModel)) }
        }
    }

    override fun onItemClicked(itemViewHolder: Presenter.ViewHolder, item: Any, rowViewHolder: RowPresenter.ViewHolder, row: Row?) {
        when (row?.id?.toInt()) {
            CAST_HEAD_ID -> viewModel.handleEvent(WorkDetailsEvent.CastClicked(item as CastViewModel))
            VIDEO_HEADER_ID -> viewModel.handleEvent(WorkDetailsEvent.VideoClicked(item as VideoViewModel))
            RECOMMENDED_HEADER_ID, SIMILAR_HEADER_ID -> viewModel.handleEvent(WorkDetailsEvent.WorkClicked(item as WorkViewModel))
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    renderState(state)
                }
            }
        }
    }

    private fun observeEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effects.collect { effect ->
                    handleEffect(effect)
                }
            }
        }
    }

    private fun renderState(state: WorkDetailsState) {
        with(state) {
            // Handle loading state
            if (isLoading) {
                progressBarManager.show()
            } else {
                progressBarManager.hide()
            }

            if (!isActionPresent(ACTION_FAVORITE)) {
                actionAdapter.add(Action(
                    ACTION_FAVORITE,
                    resources.getString(workdetailR.string.remove_favorites).takeIf { isFavorite }
                        ?: resources.getString(workdetailR.string.save_favorites))
                )
            }
            updateFavoriteAction(state.isFavorite)

            if (reviews.isNotEmpty()) {
                reviewRowAdapter.setItems(reviews, reviewDiffCallback)
                if (!isActionPresent(ACTION_REVIEWS)) {
                    actionAdapter.add(Action(ACTION_REVIEWS, getString(workdetailR.string.reviews)))
                    mainAdapter.add(ListRow(HeaderItem(REVIEW_HEADER_ID.toLong(), getString(workdetailR.string.reviews)), reviewRowAdapter))
                }
            }

            if (videos.isNotEmpty()) {
                videoRowAdapter.addAll(0, videos)
                if (!isActionPresent(ACTION_VIDEOS)) {
                    actionAdapter.add(Action(ACTION_VIDEOS, getString(workdetailR.string.videos)))
                    mainAdapter.add(ListRow(HeaderItem(VIDEO_HEADER_ID.toLong(), getString(workdetailR.string.videos)), videoRowAdapter))
                }
            }

            if (casts.isNotEmpty() && !isActionPresent(ACTION_CAST)) {
                castRowAdapter.addAll(0, casts)
                if (!isActionPresent(ACTION_CAST)) {
                    actionAdapter.add(Action(ACTION_CAST, getString(workdetailR.string.cast)))
                    mainAdapter.add(ListRow(HeaderItem(CAST_HEAD_ID.toLong(), getString(workdetailR.string.cast)), castRowAdapter))
                }
            }

            if (recommendedWorks.isNotEmpty() && !isActionPresent(ACTION_RECOMMENDED)) {
                recommendedRowAdapter.setItems(recommendedWorks, workDiffCallback)
                if (!isActionPresent(ACTION_RECOMMENDED)) {
                    actionAdapter.add(Action(ACTION_RECOMMENDED, getString(workdetailR.string.recommended)))
                    mainAdapter.add(ListRow(HeaderItem(RECOMMENDED_HEADER_ID.toLong(), getString(workdetailR.string.recommended)), recommendedRowAdapter))
                }
            }

            if (similarWorks.isNotEmpty()) {
                similarRowAdapter.setItems(similarWorks, workDiffCallback)
                if (!isActionPresent(ACTION_SIMILAR)) {
                    actionAdapter.add(Action(ACTION_SIMILAR, getString(workdetailR.string.similar)))
                    mainAdapter.add(ListRow(HeaderItem(SIMILAR_HEADER_ID.toLong(), getString(workdetailR.string.similar)), similarRowAdapter))
                }
            }
        }
    }

    private fun handleEffect(effect: WorkDetailsEffect) {
        when (effect) {
            is WorkDetailsEffect.OpenIntent -> openIntent(effect.intent, effect.shareTransition)
            is WorkDetailsEffect.ShowError -> showErrorFragment()
        }
    }

    private fun updateFavoriteAction(isFavorite: Boolean) {
        (actionAdapter.get(0) as Action).label1 = resources.getString(workdetailR.string.remove_favorites).takeIf { isFavorite }
            ?: resources.getString(workdetailR.string.save_favorites)
        actionAdapter.notifyItemRangeChanged(0, 1)
    }

    private fun showErrorFragment() {
        val fragment = ErrorFragment.newInstance().apply {
            setTargetFragment(this@WorkDetailsFragment, ERROR_FRAGMENT_REQUEST_CODE)
        }
        requireActivity().addFragment(fragment, ErrorFragment.TAG)
    }

    private fun openIntent(intent: Intent, shareTransition: Boolean) {
        try {
            if (shareTransition) {
                view?.let { fragmentView ->
                    val selectedView = fragmentView.findFocus() as? ImageCardView
                    selectedView?.mainImageView?.let { imageView ->
                        val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            requireActivity(),
                            imageView,
                            SettingShared.SHARED_ELEMENT_NAME
                        ).toBundle()
                        startActivity(intent, bundle)
                    }
                }
            } else {
                startActivity(intent)
            }
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), workdetailR.string.failed_open_video, Toast.LENGTH_LONG).show()
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
                // TODO bring it back
                /*val detailsImageView = viewHolder.view.findViewById<ImageView>(workdetailR.id.details_overview_image)
                val layoutParams = detailsImageView?.layoutParams?.apply {
                    width = resources.getDimensionPixelSize(presentationR.dimen.movie_width)
                    height = resources.getDimensionPixelSize(presentationR.dimen.movie_height)
                }
                detailsImageView?.layoutParams = layoutParams*/
                return viewHolder
            }
        }.apply {
            // Hook up transition element.
            val sharedElementHelper = FullWidthDetailsOverviewSharedElementHelper().apply {
                setSharedElementEnterTransition(activity, SettingShared.SHARED_ELEMENT_NAME)
            }

            actionsBackgroundColor = resources.getColor(presentationR.color.detail_view_actionbar_background, requireActivity().theme)
            backgroundColor = resources.getColor(presentationR.color.detail_view_background, requireActivity().theme)

            setListener(sharedElementHelper)
            isParticipatingEntranceTransition = true
            setOnActionClickedListener { action ->
                when (val position = actionAdapter.indexOf(action)) {
                    0 -> viewModel.handleEvent(WorkDetailsEvent.ToggleFavorite)
                    else -> setSelectedPosition(position)
                }
            }
        }
        presenterSelector.addClassPresenter(DetailsOverviewRow::class.java, detailsPresenter)
    }

    private fun isActionPresent(actionId: Long): Boolean {
        for (i in 0 until actionAdapter.size()) {
            val action = actionAdapter.get(i) as? Action
            if (action?.id == actionId) {
                return true
            }
        }
        return false
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
