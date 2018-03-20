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

package com.pimenta.bestv.fragment;

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
import com.pimenta.bestv.R;
import com.pimenta.bestv.activity.MovieDetailsActivity;
import com.pimenta.bestv.fragment.bases.BaseDetailsFragment;
import com.pimenta.bestv.model.Cast;
import com.pimenta.bestv.model.Movie;
import com.pimenta.bestv.model.Video;
import com.pimenta.bestv.presenter.MovieDetailsCallback;
import com.pimenta.bestv.presenter.MovieDetailsPresenter;
import com.pimenta.bestv.widget.CastCardPresenter;
import com.pimenta.bestv.widget.DetailsDescriptionPresenter;
import com.pimenta.bestv.widget.MovieCardPresenter;
import com.pimenta.bestv.widget.VideoCardPresenter;

import java.util.List;

/**
 * Created by marcus on 07-02-2018.
 */
public class MovieDetailsFragment extends BaseDetailsFragment<MovieDetailsPresenter> implements MovieDetailsCallback {

    public static final String TAG = "MovieDetailsFragment";
    public static final String SHARED_ELEMENT_NAME = "SHARED_ELEMENT_NAME";
    public static final String MOVIE = "MOVIE";

    private static final int ACTION_FAVORITE = 1;
    private static final int ACTION_VIDEOS = 2;
    private static final int ACTION_CAST = 3;
    private static final int ACTION_RECOMMENDED = 4;
    private static final int ACTION_SIMILAR = 5;
    private static final int VIDEO_HEADER_ID = 1;
    private static final int RECOMMENDED_HEADER_ID = 2;
    private static final int SIMILAR_HEADER_ID = 3;

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

    private Movie mMovie;

    public static MovieDetailsFragment newInstance() {
        return new MovieDetailsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mMovie == null) {
            mMovie = (Movie) getActivity().getIntent().getSerializableExtra(MOVIE);
        }

        setupDetailsOverviewRow();
        setupDetailsOverviewRowPresenter();
        setAdapter(mAdapter);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFavoriteAction = new Action(ACTION_FAVORITE, mPresenter.isMovieFavorite(mMovie) ? getResources().getString(R.string.remove_favorite) :
                getResources().getString(R.string.save_favorite));
        mActionAdapter.add(mFavoriteAction);
        mPresenter.loadDataByMovie(mMovie);
    }

    @Override
    public void onResultSetFavoriteMovie(final boolean success) {
        if (success) {
            mFavoriteAction.setLabel1(mMovie.isFavorite() ? getResources().getString(R.string.remove_favorite) : getResources().getString(R.string.save_favorite));
            mActionAdapter.notifyItemRangeChanged(mActionAdapter.indexOf(mFavoriteAction), 1);
        }
    }

    @Override
    public void onDataLoaded(final List<Cast> casts, final List<Movie> recommendedMovies, final List<Movie> similarMovies, final List<Video> videos) {
        if (videos != null && videos.size() > 0) {
            mActionAdapter.add(new Action(ACTION_VIDEOS, getResources().getString(R.string.videos)));
            mVideoRowAdapter = new ArrayObjectAdapter(new VideoCardPresenter());
            mVideoRowAdapter.addAll(0, videos);
            final HeaderItem recommendedHeader = new HeaderItem(VIDEO_HEADER_ID, getString(R.string.videos));
            mAdapter.add(new ListRow(recommendedHeader, mVideoRowAdapter));
        }

        if (casts != null && casts.size() > 0) {
            mActionAdapter.add(new Action(ACTION_CAST, getResources().getString(R.string.cast)));
            mCastRowAdapter = new ArrayObjectAdapter(new CastCardPresenter());
            mCastRowAdapter.addAll(0, casts);
            final HeaderItem header = new HeaderItem(0, getString(R.string.cast));
            mAdapter.add(new ListRow(header, mCastRowAdapter));
        }

        if (recommendedMovies != null && recommendedMovies.size() > 0) {
            mActionAdapter.add(new Action(ACTION_RECOMMENDED, getResources().getString(R.string.recommended)));
            mRecommendedRowAdapter = new ArrayObjectAdapter(new MovieCardPresenter());
            mRecommendedRowAdapter.addAll(0, recommendedMovies);
            final HeaderItem recommendedHeader = new HeaderItem(RECOMMENDED_HEADER_ID, getString(R.string.recommended_movies));
            mAdapter.add(new ListRow(recommendedHeader, mRecommendedRowAdapter));
        }

        if (similarMovies != null && similarMovies.size() > 0) {
            mActionAdapter.add(new Action(ACTION_SIMILAR, getResources().getString(R.string.similar)));
            mSimilarRowAdapter = new ArrayObjectAdapter(new MovieCardPresenter());
            mSimilarRowAdapter.addAll(0, similarMovies);
            final HeaderItem similarHeader = new HeaderItem(SIMILAR_HEADER_ID, getString(R.string.similar_movies));
            mAdapter.add(new ListRow(similarHeader, mSimilarRowAdapter));
        }
    }

    @Override
    public void onRecommendationLoaded(final List<Movie> movies) {
        if (movies != null) {
            for (final Movie movie : movies) {
                if (mRecommendedRowAdapter.indexOf(movie) == -1) {
                    mRecommendedRowAdapter.add(movie);
                }
            }
        }
    }

    @Override
    public void onSimilarLoaded(final List<Movie> movies) {
        if (movies != null) {
            for (final Movie movie : movies) {
                if (mSimilarRowAdapter.indexOf(movie) == -1) {
                    mSimilarRowAdapter.add(movie);
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

        mDetailsOverviewRow = new DetailsOverviewRow(mMovie);
        mPresenter.loadCardImage(mMovie);

        mActionAdapter = new ArrayObjectAdapter();
        mDetailsOverviewRow.setActionsAdapter(mActionAdapter);
        mAdapter.add(mDetailsOverviewRow);

        setOnItemViewSelectedListener(new ItemViewSelectedListener());
        setOnItemViewClickedListener(new ItemViewClickedListener());
    }

    private void setupDetailsOverviewRowPresenter() {
        // Set detail background.
        final FullWidthDetailsOverviewRowPresenter detailsPresenter = new FullWidthDetailsOverviewRowPresenter(new DetailsDescriptionPresenter()) {

            private ImageView mDetailsImageView;

            @Override
            protected RowPresenter.ViewHolder createRowViewHolder(final ViewGroup parent) {
                RowPresenter.ViewHolder viewHolder = super.createRowViewHolder(parent);
                mDetailsImageView = viewHolder.view.findViewById(R.id.details_overview_image);
                ViewGroup.LayoutParams lp = mDetailsImageView.getLayoutParams();
                lp.width = getResources().getDimensionPixelSize(R.dimen.movie_card_width);
                lp.height = getResources().getDimensionPixelSize(R.dimen.movie_card_height);
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
                    mPresenter.setFavoriteMovie(mMovie);
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
        mPresenter.loadBackdropImage(mMovie);
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {

        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (row != null && row.getHeaderItem() != null) {
                switch ((int) row.getHeaderItem().getId()) {
                    case RECOMMENDED_HEADER_ID:
                        final Movie recommendedMovie = (Movie) item;
                        if (mRecommendedRowAdapter.indexOf(recommendedMovie) >= mRecommendedRowAdapter.size() - 1) {
                            mPresenter.loadRecommendationByMovie(mMovie);
                        }
                        break;
                    case SIMILAR_HEADER_ID:
                        final Movie similarMovie = (Movie) item;
                        if (mSimilarRowAdapter.indexOf(similarMovie) >= mSimilarRowAdapter.size() - 1) {
                            mPresenter.loadSimilarByMovie(mMovie);
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
                    case VIDEO_HEADER_ID:
                        final Video video = (Video) item;
                        final Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(String.format(getString(R.string.youtube_video_base_url), video.getKey())));
                        try {
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Log.e(TAG, "Failed to play a video", e);
                        }
                        break;
                    case RECOMMENDED_HEADER_ID:
                    case SIMILAR_HEADER_ID:
                        final Movie movie = (Movie) item;
                        final Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                ((ImageCardView) itemViewHolder.view).getMainImageView(), MovieDetailsFragment.SHARED_ELEMENT_NAME).toBundle();
                        startActivity(MovieDetailsActivity.newInstance(getContext(), movie), bundle);
                        break;
                }
            }
        }
    }
}