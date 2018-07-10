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

package com.pimenta.bestv.view.fragment;

import android.content.ActivityNotFoundException;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pimenta.bestv.BesTV;
import com.pimenta.bestv.BuildConfig;
import com.pimenta.bestv.R;
import com.pimenta.bestv.presenter.MovieDetailsContract;
import com.pimenta.bestv.presenter.MovieDetailsPresenter;
import com.pimenta.bestv.repository.entity.Cast;
import com.pimenta.bestv.repository.entity.Video;
import com.pimenta.bestv.repository.entity.Work;
import com.pimenta.bestv.view.activity.CastDetailsActivity;
import com.pimenta.bestv.view.activity.WorkDetailsActivity;
import com.pimenta.bestv.view.fragment.base.BaseDetailsFragment;
import com.pimenta.bestv.view.widget.CastCardPresenter;
import com.pimenta.bestv.view.widget.VideoCardPresenter;
import com.pimenta.bestv.view.widget.WorkCardPresenter;
import com.pimenta.bestv.view.widget.WorkDetailsDescriptionPresenter;

import java.util.List;

/**
 * Created by marcus on 07-02-2018.
 */
public class WorkDetailsFragment extends BaseDetailsFragment<MovieDetailsPresenter> implements MovieDetailsContract {

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

    public static WorkDetailsFragment newInstance() {
        return new WorkDetailsFragment();
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
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFavoriteAction = new Action(ACTION_FAVORITE, mPresenter.isFavorite(mWork) ? getResources().getString(R.string.remove_favorites) :
                getResources().getString(R.string.save_favorites));
        mActionAdapter.add(mFavoriteAction);
        mPresenter.loadDataByWork(mWork);
    }

    @Override
    public void onResultSetFavoriteMovie(final boolean success) {
        if (success) {
            mFavoriteAction.setLabel1(mWork.isFavorite() ? getResources().getString(R.string.remove_favorites) : getResources().getString(R.string.save_favorites));
            mActionAdapter.notifyItemRangeChanged(mActionAdapter.indexOf(mFavoriteAction), 1);
        }
    }

    @Override
    public void onDataLoaded(final List<Cast> casts, final List<Work> recommendedWorks, final List<Work> similarWorks, final List<Video> videos) {
        if (videos != null && videos.size() > 0) {
            mActionAdapter.add(new Action(ACTION_VIDEOS, getResources().getString(R.string.videos)));
            VideoCardPresenter videoCardPresenter = new VideoCardPresenter();
            videoCardPresenter.setLoadVideoThumbnailListener((video, imageView) -> mPresenter.loadVideoThumbnailImage(video, imageView));
            mVideoRowAdapter = new ArrayObjectAdapter(videoCardPresenter);
            mVideoRowAdapter.addAll(0, videos);
            final HeaderItem recommendedHeader = new HeaderItem(VIDEO_HEADER_ID, getString(R.string.videos));
            mAdapter.add(new ListRow(recommendedHeader, mVideoRowAdapter));
        }

        if (casts != null && casts.size() > 0) {
            mActionAdapter.add(new Action(ACTION_CAST, getResources().getString(R.string.cast)));
            CastCardPresenter castCardPresenter = new CastCardPresenter();
            castCardPresenter.setLoadCastProfileListener((cast, imageView) -> mPresenter.loadCastProfileImage(cast, imageView));
            mCastRowAdapter = new ArrayObjectAdapter(castCardPresenter);
            mCastRowAdapter.addAll(0, casts);
            final HeaderItem header = new HeaderItem(CAST_HEAD_ID, getString(R.string.cast));
            mAdapter.add(new ListRow(header, mCastRowAdapter));
        }

        if (recommendedWorks != null && recommendedWorks.size() > 0) {
            mActionAdapter.add(new Action(ACTION_RECOMMENDED, getResources().getString(R.string.recommended)));
            WorkCardPresenter workCardPresenter = new WorkCardPresenter();
            workCardPresenter.setLoadWorkPosterListener((movie, imageView) -> mPresenter.loadWorkPosterImage(movie, imageView));
            mRecommendedRowAdapter = new ArrayObjectAdapter(workCardPresenter);
            mRecommendedRowAdapter.addAll(0, recommendedWorks);
            final HeaderItem recommendedHeader = new HeaderItem(RECOMMENDED_HEADER_ID, getString(R.string.recommended_movies));
            mAdapter.add(new ListRow(recommendedHeader, mRecommendedRowAdapter));
        }

        if (similarWorks != null && similarWorks.size() > 0) {
            mActionAdapter.add(new Action(ACTION_SIMILAR, getResources().getString(R.string.similar)));
            WorkCardPresenter workCardPresenter = new WorkCardPresenter();
            workCardPresenter.setLoadWorkPosterListener((movie, imageView) -> mPresenter.loadWorkPosterImage(movie, imageView));
            mSimilarRowAdapter = new ArrayObjectAdapter(workCardPresenter);
            mSimilarRowAdapter.addAll(0, similarWorks);
            final HeaderItem similarHeader = new HeaderItem(SIMILAR_HEADER_ID, getString(R.string.similar_movies));
            mAdapter.add(new ListRow(similarHeader, mSimilarRowAdapter));
        }
    }

    @Override
    public void onRecommendationLoaded(final List<Work> works) {
        if (works != null) {
            for (final Work work : works) {
                if (mRecommendedRowAdapter.indexOf(work) == -1) {
                    mRecommendedRowAdapter.add(work);
                }
            }
        }
    }

    @Override
    public void onSimilarLoaded(final List<Work> works) {
        if (works != null) {
            for (final Work work : works) {
                if (mSimilarRowAdapter.indexOf(work) == -1) {
                    mSimilarRowAdapter.add(work);
                }
            }
        }
    }

    @Override
    public void onCardImageLoaded(final Drawable resource) {
        mDetailsOverviewRow.setImageDrawable(resource);
        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
        setupBackgroundImage();
    }

    @Override
    public void onBackdropImageLoaded(final Bitmap bitmap) {
        mDetailsBackground.setCoverBitmap(bitmap);
        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
    }

    @Override
    protected void injectPresenter() {
        BesTV.getApplicationComponent().inject(this);
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
        final FullWidthDetailsOverviewRowPresenter detailsPresenter = new FullWidthDetailsOverviewRowPresenter(new WorkDetailsDescriptionPresenter()) {

            private ImageView mDetailsImageView;

            @Override
            protected RowPresenter.ViewHolder createRowViewHolder(final ViewGroup parent) {
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
                        final Work recommendedWork = (Work) item;
                        if (mRecommendedRowAdapter.indexOf(recommendedWork) >= mRecommendedRowAdapter.size() - 1) {
                            mPresenter.loadRecommendationByWork(mWork);
                        }
                        break;
                    case SIMILAR_HEADER_ID:
                        final Work similarWork = (Work) item;
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
                        final Cast cast = (Cast) item;
                        final Bundle castBundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                ((ImageCardView) itemViewHolder.view).getMainImageView(), CastDetailsFragment.SHARED_ELEMENT_NAME).toBundle();
                        startActivity(CastDetailsActivity.newInstance(getContext(), cast), castBundle);
                        break;
                    case VIDEO_HEADER_ID:
                        final Video video = (Video) item;
                        final Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(String.format(BuildConfig.YOUTUBE_BASE_URL, video.getKey())));
                        try {
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Log.e(TAG, "Failed to play a video", e);
                        }
                        break;
                    case RECOMMENDED_HEADER_ID:
                    case SIMILAR_HEADER_ID:
                        final Work work = (Work) item;
                        final Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                ((ImageCardView) itemViewHolder.view).getMainImageView(), WorkDetailsFragment.SHARED_ELEMENT_NAME).toBundle();
                        startActivity(WorkDetailsActivity.newInstance(getContext(), work), bundle);
                        break;
                }
            }
        }
    }
}