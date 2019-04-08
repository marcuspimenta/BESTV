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

package com.pimenta.bestv.feature.workdetail.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import androidx.leanback.app.DetailsSupportFragmentBackgroundController
import androidx.leanback.widget.*
import androidx.core.app.ActivityOptionsCompat
import android.view.ViewGroup
import android.widget.ImageView
import com.pimenta.bestv.BesTV
import com.pimenta.bestv.BuildConfig
import com.pimenta.bestv.R
import com.pimenta.bestv.feature.base.BaseDetailsFragment
import com.pimenta.bestv.feature.castdetail.ui.CastDetailsActivity
import com.pimenta.bestv.feature.castdetail.ui.CastDetailsFragment
import com.pimenta.bestv.widget.render.CastCardRender
import com.pimenta.bestv.widget.render.VideoCardRender
import com.pimenta.bestv.widget.render.WorkCardRenderer
import com.pimenta.bestv.widget.render.WorkDetailsDescriptionRender
import com.pimenta.bestv.feature.workdetail.presenter.WorkDetailsPresenter
import com.pimenta.bestv.repository.entity.Cast
import com.pimenta.bestv.repository.entity.Video
import com.pimenta.bestv.repository.entity.Work
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by marcus on 07-02-2018.
 */
class WorkDetailsFragment : BaseDetailsFragment(), WorkDetailsPresenter.View {

    private val actionAdapter: ArrayObjectAdapter by lazy { ArrayObjectAdapter() }
    private val videoRowAdapter: ArrayObjectAdapter by lazy { ArrayObjectAdapter(VideoCardRender()) }
    private val castRowAdapter: ArrayObjectAdapter by lazy { ArrayObjectAdapter(CastCardRender()) }
    private val recommendedRowAdapter: ArrayObjectAdapter by lazy { ArrayObjectAdapter(WorkCardRenderer()) }
    private val similarRowAdapter: ArrayObjectAdapter  by lazy { ArrayObjectAdapter(WorkCardRenderer()) }
    private val mainAdapter: ArrayObjectAdapter by lazy { ArrayObjectAdapter(presenterSelector) }
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

    private lateinit var favoriteAction: Action
    private lateinit var detailsOverviewRow: DetailsOverviewRow
    private lateinit var work: Work

    @Inject
    lateinit var presenter: WorkDetailsPresenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        BesTV.applicationComponent.getWorkDetailsFragmentComponent()
                .view(this)
                .build()
                .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let {
            work = it.intent.getSerializableExtra(WORK) as Work
        }

        setupDetailsOverviewRow()
        setupDetailsOverviewRowPresenter()
        adapter = mainAdapter
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favoriteAction = Action(
                ACTION_FAVORITE.toLong(),
                if (presenter.isFavorite(work))
                    resources.getString(R.string.remove_favorites)
                else
                    resources.getString(R.string.save_favorites)
        )
        actionAdapter.add(favoriteAction)
        presenter.loadDataByWork(work)
    }

    override fun onDetach() {
        presenter.dispose()
        super.onDetach()
    }

    override fun onResultSetFavoriteMovie(success: Boolean) {
        if (success) {
            favoriteAction.label1 = if (work.isFavorite) resources.getString(R.string.remove_favorites) else resources.getString(R.string.save_favorites)
            actionAdapter.notifyItemRangeChanged(actionAdapter.indexOf(favoriteAction), 1)
        }
    }

    override fun onDataLoaded(casts: List<Cast>?, recommendedWorks: List<Work>?, similarWorks: List<Work>?, videos: List<Video>?) {
        videos?.let {
            if (it.isNotEmpty()) {
                actionAdapter.add(Action(ACTION_VIDEOS.toLong(), resources.getString(R.string.videos)))
                videoRowAdapter.addAll(0, it)
                mainAdapter.add(ListRow(HeaderItem(VIDEO_HEADER_ID.toLong(), getString(R.string.videos)), videoRowAdapter))
            }
        }

        casts?.let {
            if (it.isNotEmpty()) {
                actionAdapter.add(Action(ACTION_CAST.toLong(), resources.getString(R.string.cast)))
                castRowAdapter.addAll(0, it)
                mainAdapter.add(ListRow(HeaderItem(CAST_HEAD_ID.toLong(), getString(R.string.cast)), castRowAdapter))
            }
        }

        recommendedWorks?.let {
            if (it.isNotEmpty()) {
                actionAdapter.add(Action(ACTION_RECOMMENDED.toLong(), resources.getString(R.string.recommended)))
                recommendedRowAdapter.addAll(0, it)
                mainAdapter.add(ListRow(HeaderItem(RECOMMENDED_HEADER_ID.toLong(), getString(R.string.recommended)), recommendedRowAdapter))
            }
        }

        similarWorks?.let {
            if (it.isNotEmpty()) {
                actionAdapter.add(Action(ACTION_SIMILAR.toLong(), resources.getString(R.string.similar)))
                similarRowAdapter.addAll(0, it)
                mainAdapter.add(ListRow(HeaderItem(SIMILAR_HEADER_ID.toLong(), getString(R.string.similar)), similarRowAdapter))
            }
        }
    }

    override fun onRecommendationLoaded(works: List<Work>?) {
        works?.forEach { work ->
            if (recommendedRowAdapter.indexOf(work) == -1) {
                recommendedRowAdapter.add(work)
            }
        }
    }

    override fun onSimilarLoaded(works: List<Work>?) {
        works?.forEach { work ->
            if (similarRowAdapter.indexOf(work) == -1) {
                similarRowAdapter.add(work)
            }
        }
    }

    override fun onCardImageLoaded(resource: Drawable?) {
        detailsOverviewRow.imageDrawable = resource
        mainAdapter.notifyArrayItemRangeChanged(0, mainAdapter.size())
        presenter.loadBackdropImage(work)
    }

    override fun onBackdropImageLoaded(bitmap: Bitmap?) {
        detailsBackground.coverBitmap = bitmap
        mainAdapter.notifyArrayItemRangeChanged(0, mainAdapter.size())
    }

    private fun setupDetailsOverviewRow() {
        detailsOverviewRow = DetailsOverviewRow(work)
        presenter.loadCardImage(work)

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
                ACTION_FAVORITE -> presenter.setFavorite(work)
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
            when (row?.headerItem?.id?.toInt()) {
                RECOMMENDED_HEADER_ID -> {
                    item?.let {
                        if (recommendedRowAdapter.indexOf(it) >= recommendedRowAdapter.size() - 1) {
                            presenter.loadRecommendationByWork(work)
                        }
                    }
                }
                SIMILAR_HEADER_ID -> {
                    item?.let {
                        if (similarRowAdapter.indexOf(it) >= similarRowAdapter.size() - 1) {
                            presenter.loadSimilarByWork(work)
                        }
                    }
                }
            }
        }
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {

        override fun onItemClicked(itemViewHolder: Presenter.ViewHolder, item: Any, rowViewHolder: RowPresenter.ViewHolder, row: Row?) {
            when (row?.headerItem?.id?.toInt()) {
                CAST_HEAD_ID -> {
                    val cast = item as Cast
                    val castBundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            activity!!,
                            (itemViewHolder.view as ImageCardView).mainImageView,
                            CastDetailsFragment.SHARED_ELEMENT_NAME
                    ).toBundle()
                    startActivity(CastDetailsActivity.newInstance(context, cast), castBundle)
                }
                VIDEO_HEADER_ID -> {
                    val video = item as Video
                    val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(String.format(BuildConfig.YOUTUBE_BASE_URL, video.key))
                    )
                    try {
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Timber.e(e, "Failed to play a video")
                    }
                }
                RECOMMENDED_HEADER_ID, SIMILAR_HEADER_ID -> {
                    val work = item as Work
                    val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            activity!!,
                            (itemViewHolder.view as ImageCardView).mainImageView,
                            SHARED_ELEMENT_NAME
                    ).toBundle()
                    startActivity(WorkDetailsActivity.newInstance(context, work), bundle)
                }
            }
        }
    }

    companion object {

        const val SHARED_ELEMENT_NAME = "SHARED_ELEMENT_NAME"
        const val WORK = "WORK"

        private const val ACTION_FAVORITE = 1
        private const val ACTION_VIDEOS = 2
        private const val ACTION_CAST = 3
        private const val ACTION_RECOMMENDED = 4
        private const val ACTION_SIMILAR = 5
        private const val VIDEO_HEADER_ID = 1
        private const val RECOMMENDED_HEADER_ID = 2
        private const val SIMILAR_HEADER_ID = 3
        private const val CAST_HEAD_ID = 4

        fun newInstance() = WorkDetailsFragment()
    }
}