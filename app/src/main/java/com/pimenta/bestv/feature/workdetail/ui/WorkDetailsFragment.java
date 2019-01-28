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

package com.pimenta.bestv.feature.workdetail.ui;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.DetailsSupportFragmentBackgroundController;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewSharedElementHelper;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pimenta.bestv.BesTV;
import com.pimenta.bestv.BuildConfig;
import com.pimenta.bestv.R;
import com.pimenta.bestv.feature.base.BaseDetailsFragment;
import com.pimenta.bestv.feature.castdetail.ui.CastDetailsActivity;
import com.pimenta.bestv.feature.castdetail.ui.CastDetailsFragment;
import com.pimenta.bestv.feature.widget.render.WorkDetailsDescriptionRender;
import com.pimenta.bestv.feature.widget.render.CastCardRender;
import com.pimenta.bestv.feature.widget.render.VideoCardRender;
import com.pimenta.bestv.feature.widget.render.WorkCardRenderer;
import com.pimenta.bestv.feature.workdetail.presenter.WorkDetailsPresenter;
import com.pimenta.bestv.repository.entity.Cast;
import com.pimenta.bestv.repository.entity.Video;
import com.pimenta.bestv.repository.entity.Work;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by marcus on 07-02-2018.
 */
public class WorkDetailsFragment extends BaseDetailsFragment implements WorkDetailsPresenter.WorkDetailsView {

    public static final String TAG = "WorkDetailsFragment";
    public static final String SHARED_ELEMENT_NAME = "SHARED_ELEMENT_NAME";
    public static final String WORK = "WORK";

    private static final int ACTION_FAVORITE = 1;
    private static final int ACTION_VIDEOS = 2;
    private static final int ACTION_CAST = 3;
    private static final int ACTION_RECOMMENDED = 4;
    private static final int ACTION_SIMILAR = 5;
    private static final int VIDEO_HEADER_ID = 1;
    private static final int RECOMMENDED_HEADER_ID = 2;
    private static final int SIMILAR_HEADER_ID = 3;
    private static final int CAST_HEAD_ID = 4;

    private Action mFavoriteAction;
    private ArrayObjectAdapter mAdapter;
    private ArrayObjectAdapter mActionAdapter;
    private ArrayObjectAdapter mVideoRowAdapter;
    private ArrayObjectAdapter mCastRowAdapter;
    private ArrayObjectAdapter mRecommendedRowAdapter;
    private ArrayObjectAdapter mSimilarRowAdapter;
    private ClassPresenterSelector mPresenterSelector;
    private DetailsOverviewRow mDetailsOverviewRow;
    private DetailsSupportFragmentBackgroundController mDetailsBackground;

    private Work mWork;

    @Inject
    WorkDetailsPresenter mPresenter;

    public static WorkDetailsFragment newInstance() {
        return new WorkDetailsFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        BesTV.applicationComponent.inject(this);
        mPresenter.register(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mWork == null) {
            mWork = (Work) getActivity().getIntent().getSerializableExtra(WORK);
        }

        setupDetailsOverviewRow();
        setupDetailsOverviewRowPresenter();
        setAdapter(mAdapter);
    }

    @Override
    public void onViewCreated(android.view.View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFavoriteAction = new Action(ACTION_FAVORITE, mPresenter.isFavorite(mWork) ? getResources().getString(R.string.remove_favorites) :
                getResources().getString(R.string.save_favorites));
        mActionAdapter.add(mFavoriteAction);
        mPresenter.loadDataByWork(mWork);
    }

    @Override
    public void onDetach() {
        mPresenter.unRegister();
        super.onDetach();
    }

    @Override
    public void onResultSetFavoriteMovie(boolean success) {
        if (success) {
            mFavoriteAction.setLabel1(mWork.isFavorite() ? getResources().getString(R.string.remove_favorites) : getResources().getString(R.string.save_favorites));
            mActionAdapter.notifyItemRangeChanged(mActionAdapter.indexOf(mFavoriteAction), 1);
        }
    }

    @Override
    public void onDataLoaded(List<Cast> casts, List<? extends Work> recommendedWorks, List<? extends Work> similarWorks, List<? extends Video> videos) {
        if (videos != null && videos.size() > 0) {
            mActionAdapter.add(new Action(ACTION_VIDEOS, getResources().getString(R.string.videos)));
            VideoCardRender videoCardRender = new VideoCardRender();
            mVideoRowAdapter = new ArrayObjectAdapter(videoCardRender);
            mVideoRowAdapter.addAll(0, videos);
            final HeaderItem recommendedHeader = new HeaderItem(VIDEO_HEADER_ID, getString(R.string.videos));
            mAdapter.add(new ListRow(recommendedHeader, mVideoRowAdapter));
        }

        if (casts != null && casts.size() > 0) {
            mActionAdapter.add(new Action(ACTION_CAST, getResources().getString(R.string.cast)));
            CastCardRender castCardRender = new CastCardRender();
            mCastRowAdapter = new ArrayObjectAdapter(castCardRender);
            mCastRowAdapter.addAll(0, casts);
            final HeaderItem header = new HeaderItem(CAST_HEAD_ID, getString(R.string.cast));
            mAdapter.add(new ListRow(header, mCastRowAdapter));
        }

        if (recommendedWorks != null && recommendedWorks.size() > 0) {
            mActionAdapter.add(new Action(ACTION_RECOMMENDED, getResources().getString(R.string.recommended)));
            WorkCardRenderer workCardRenderer = new WorkCardRenderer();
            mRecommendedRowAdapter = new ArrayObjectAdapter(workCardRenderer);
            mRecommendedRowAdapter.addAll(0, recommendedWorks);
            final HeaderItem recommendedHeader = new HeaderItem(RECOMMENDED_HEADER_ID, getString(R.string.recommended));
            mAdapter.add(new ListRow(recommendedHeader, mRecommendedRowAdapter));
        }

        if (similarWorks != null && similarWorks.size() > 0) {
            mActionAdapter.add(new Action(ACTION_SIMILAR, getResources().getString(R.string.similar)));
            WorkCardRenderer workCardRenderer = new WorkCardRenderer();
            mSimilarRowAdapter = new ArrayObjectAdapter(workCardRenderer);
            mSimilarRowAdapter.addAll(0, similarWorks);
            final HeaderItem similarHeader = new HeaderItem(SIMILAR_HEADER_ID, getString(R.string.similar));
            mAdapter.add(new ListRow(similarHeader, mSimilarRowAdapter));
        }
    }

    @Override
    public void onRecommendationLoaded(List<? extends Work> works) {
        if (works != null) {
            for (final Work work : works) {
                if (mRecommendedRowAdapter.indexOf(work) == -1) {
                    mRecommendedRowAdapter.add(work);
                }
            }
        }
    }

    @Override
    public void onSimilarLoaded(List<? extends Work> works) {
        if (works != null) {
            for (final Work work : works) {
                if (mSimilarRowAdapter.indexOf(work) == -1) {
                    mSimilarRowAdapter.add(work);
                }
            }
        }
    }

    @Override
    public void onCardImageLoaded(Drawable resource) {
        mDetailsOverviewRow.setImageDrawable(resource);
        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
        setupBackgroundImage();
    }

    @Override
    public void onBackdropImageLoaded(Bitmap bitmap) {
        mDetailsBackground.setCoverBitmap(bitmap);
        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
    }

    private void setupDetailsOverviewRow() {
        mPresenterSelector = new ClassPresenterSelector();
        mPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        mAdapter = new ArrayObjectAdapter(mPresenterSelector);

        mDetailsOverviewRow = new DetailsOverviewRow(mWork);
        mPresenter.loadCardImage(mWork);

        mActionAdapter = new ArrayObjectAdapter();
        mDetailsOverviewRow.setActionsAdapter(mActionAdapter);
        mAdapter.add(mDetailsOverviewRow);

        setOnItemViewSelectedListener(new ItemViewSelectedListener());
        setOnItemViewClickedListener(new ItemViewClickedListener());
    }

    private void setupDetailsOverviewRowPresenter() {
        // Set detail background.
        final FullWidthDetailsOverviewRowPresenter detailsPresenter = new FullWidthDetailsOverviewRowPresenter(new WorkDetailsDescriptionRender()) {

            private ImageView mDetailsImageView;

            @Override
            protected RowPresenter.ViewHolder createRowViewHolder(ViewGroup parent) {
                RowPresenter.ViewHolder viewHolder = super.createRowViewHolder(parent);
                mDetailsImageView = viewHolder.view.findViewById(R.id.details_overview_image);
                ViewGroup.LayoutParams lp = mDetailsImageView.getLayoutParams();
                lp.width = getResources().getDimensionPixelSize(R.dimen.movie_width);
                lp.height = getResources().getDimensionPixelSize(R.dimen.movie_height);
                mDetailsImageView.setLayoutParams(lp);
                return viewHolder;
            }
        };
        detailsPresenter.setActionsBackgroundColor(getResources().getColor(R.color.detail_view_actionbar_background, getActivity().getTheme()));
        detailsPresenter.setBackgroundColor(getResources().getColor(R.color.detail_view_background, getActivity().getTheme()));

        // Hook up transition element.
        final FullWidthDetailsOverviewSharedElementHelper sharedElementHelper = new FullWidthDetailsOverviewSharedElementHelper();
        sharedElementHelper.setSharedElementEnterTransition(getActivity(), SHARED_ELEMENT_NAME);
        detailsPresenter.setListener(sharedElementHelper);
        detailsPresenter.setParticipatingEntranceTransition(true);
        detailsPresenter.setOnActionClickedListener(action -> {
            int position = 0;
            switch ((int) action.getId()) {
                case ACTION_FAVORITE:
                    mPresenter.setFavorite(mWork);
                    break;
                case ACTION_SIMILAR:
                    if (mSimilarRowAdapter != null && mSimilarRowAdapter.size() > 0) {
                        position++;
                    }
                case ACTION_RECOMMENDED:
                    if (mRecommendedRowAdapter != null && mRecommendedRowAdapter.size() > 0) {
                        position++;
                    }
                case ACTION_CAST:
                    if (mCastRowAdapter != null && mCastRowAdapter.size() > 0) {
                        position++;
                    }
                case ACTION_VIDEOS:
                    if (mVideoRowAdapter != null && mVideoRowAdapter.size() > 0) {
                        position++;
                    }
                    setSelectedPosition(position);
            }
        });
        mPresenterSelector.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);
    }

    private void setupBackgroundImage() {
        mDetailsBackground = new DetailsSupportFragmentBackgroundController(this);
        mDetailsBackground.enableParallax();
        mPresenter.loadBackdropImage(mWork);
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {

        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (row != null && row.getHeaderItem() != null) {
                switch ((int) row.getHeaderItem().getId()) {
                    case RECOMMENDED_HEADER_ID:
                        Work recommendedWork = (Work) item;
                        if (mRecommendedRowAdapter.indexOf(recommendedWork) >= mRecommendedRowAdapter.size() - 1) {
                            mPresenter.loadRecommendationByWork(mWork);
                        }
                        break;
                    case SIMILAR_HEADER_ID:
                        Work similarWork = (Work) item;
                        if (mSimilarRowAdapter.indexOf(similarWork) >= mSimilarRowAdapter.size() - 1) {
                            mPresenter.loadSimilarByWork(mWork);
                        }
                        break;
                }
            }
        }
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {

        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (row != null && row.getHeaderItem() != null) {
                switch ((int) row.getHeaderItem().getId()) {
                    case CAST_HEAD_ID:
                        Cast cast = (Cast) item;
                        Bundle castBundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                ((ImageCardView) itemViewHolder.view).getMainImageView(), CastDetailsFragment.SHARED_ELEMENT_NAME).toBundle();
                        startActivity(CastDetailsActivity.Companion.newInstance(getContext(), cast), castBundle);
                        break;
                    case VIDEO_HEADER_ID:
                        Video video = (Video) item;
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(String.format(BuildConfig.YOUTUBE_BASE_URL, video.getKey())));
                        try {
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Log.e(TAG, "Failed to play a video", e);
                        }
                        break;
                    case RECOMMENDED_HEADER_ID:
                    case SIMILAR_HEADER_ID:
                        Work work = (Work) item;
                        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                ((ImageCardView) itemViewHolder.view).getMainImageView(), SHARED_ELEMENT_NAME).toBundle();
                        startActivity(WorkDetailsActivity.newInstance(getContext(), work), bundle);
                        break;
                }
            }
        }
    }
}