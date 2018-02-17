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

package com.pimenta.bestv.fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.DetailsFragmentBackgroundController;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewSharedElementHelper;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.pimenta.bestv.R;
import com.pimenta.bestv.fragments.bases.BaseDetailsFragment;
import com.pimenta.bestv.models.Cast;
import com.pimenta.bestv.models.Movie;
import com.pimenta.bestv.presenters.MovieDetailsCallback;
import com.pimenta.bestv.presenters.MovieDetailsPresenter;
import com.pimenta.bestv.widget.CastCardPresenter;
import com.pimenta.bestv.widget.DetailsDescriptionPresenter;
import com.pimenta.bestv.widget.MovieCardPresenter;

import java.util.List;

/**
 * Created by marcus on 07-02-2018.
 */
public class MovieDetailsFragment extends BaseDetailsFragment<MovieDetailsPresenter> implements MovieDetailsCallback {

    public static final String TAG = "MovieDetailsFragment";
    public static final String SHARED_ELEMENT_NAME = "hero";
    public static final String MOVIE = "Movie";

    private static final int ACTION_WATCH_TRAILER = 1;

    private ArrayObjectAdapter mAdapter;
    private ArrayObjectAdapter mRecommendedRowAdapter;
    private ArrayObjectAdapter mSimilarRowAdapter;
    private ClassPresenterSelector mPresenterSelector;
    private DetailsOverviewRow mDetailsOverviewRow;
    private DetailsFragmentBackgroundController mDetailsBackground;

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
        mPresenter.loadDataByMovie(mMovie);
    }

    @Override
    public void onDataLoaded(final List<Cast> casts, final List<Movie> recommendedMovies, final List<Movie> similarMovies) {
        final ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CastCardPresenter());
        listRowAdapter.addAll(0, casts);
        final HeaderItem header = new HeaderItem(0, getString(R.string.cast));
        mAdapter.add(new ListRow(header, listRowAdapter));

        mRecommendedRowAdapter = new ArrayObjectAdapter(new MovieCardPresenter());
        mRecommendedRowAdapter.addAll(0, recommendedMovies);
        final HeaderItem recommendedHeader = new HeaderItem(0, getString(R.string.recommended_movies));
        mAdapter.add(new ListRow(recommendedHeader, mRecommendedRowAdapter));

        mSimilarRowAdapter = new ArrayObjectAdapter(new MovieCardPresenter());
        mSimilarRowAdapter.addAll(0, similarMovies);
        final HeaderItem similarHeader = new HeaderItem(0, getString(R.string.similar_movies));
        mAdapter.add(new ListRow(similarHeader, mSimilarRowAdapter));
    }

    @Override
    public void onRecommendationLoaded(final List<Movie> movies) {
        mRecommendedRowAdapter.addAll(mRecommendedRowAdapter.size() - 1, movies);
    }

    @Override
    public void onSimilarLoaded(final List<Movie> movies) {
        mSimilarRowAdapter.addAll(mSimilarRowAdapter.size() - 1, movies);
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
    public MovieDetailsPresenter getPresenter() {
        return new MovieDetailsPresenter();
    }

    private void setupDetailsOverviewRow() {
        mPresenterSelector = new ClassPresenterSelector();
        mPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        mAdapter = new ArrayObjectAdapter(mPresenterSelector);

        mDetailsOverviewRow = new DetailsOverviewRow(mMovie);
        mDetailsOverviewRow.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.default_background));
        mPresenter.loadCardImage(mMovie);

        ArrayObjectAdapter actionAdapter = new ArrayObjectAdapter();
        actionAdapter.add(new Action(ACTION_WATCH_TRAILER, getResources().getString(R.string.watch_trailer)));
        mDetailsOverviewRow.setActionsAdapter(actionAdapter);
        mAdapter.add(mDetailsOverviewRow);
    }

    private void setupDetailsOverviewRowPresenter() {
        // Set detail background.
        final FullWidthDetailsOverviewRowPresenter detailsPresenter = new FullWidthDetailsOverviewRowPresenter(new DetailsDescriptionPresenter());
        //detailsPresenter.setBackgroundColor(getResources().getColor(R.color.selected_background, getActivity().getTheme()));

        // Hook up transition element.
        final FullWidthDetailsOverviewSharedElementHelper sharedElementHelper = new FullWidthDetailsOverviewSharedElementHelper();
        sharedElementHelper.setSharedElementEnterTransition(getActivity(), SHARED_ELEMENT_NAME);
        detailsPresenter.setListener(sharedElementHelper);
        detailsPresenter.setParticipatingEntranceTransition(true);
        detailsPresenter.setOnActionClickedListener(action -> {
            switch ((int) action.getId()) {
                case ACTION_WATCH_TRAILER:
                    /*Intent intent = new Intent(getActivity(), PlaybackActivity.class);
                    intent.putExtra(MOVIE, mMovie);
                    startActivity(intent);*/
                    break;
            }
        });
        mPresenterSelector.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);
    }

    private void setupBackgroundImage() {
        mDetailsBackground = new DetailsFragmentBackgroundController(this);
        mDetailsBackground.enableParallax();
        mPresenter.loadBackdropImage(mMovie);
    }
}