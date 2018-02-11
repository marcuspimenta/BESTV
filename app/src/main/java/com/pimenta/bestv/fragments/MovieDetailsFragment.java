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

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v17.leanback.app.DetailsFragmentBackgroundController;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewSharedElementHelper;
import android.support.v4.content.ContextCompat;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.pimenta.bestv.R;
import com.pimenta.bestv.activities.PlaybackActivity;
import com.pimenta.bestv.fragments.bases.BaseDetailsFragment;
import com.pimenta.bestv.models.Movie;
import com.pimenta.bestv.presenters.MovieDetailsCallback;
import com.pimenta.bestv.presenters.MovieDetailsPresenter;
import com.pimenta.bestv.widget.DetailsDescriptionPresenter;

/**
 * Created by marcus on 07-02-2018.
 */
public class MovieDetailsFragment extends BaseDetailsFragment<MovieDetailsPresenter> implements MovieDetailsCallback {

    private static final String TAG = "MovieDetailsFragment";
    public static final String SHARED_ELEMENT_NAME = "hero";
    public static final String MOVIE = "Movie";

    private static final int ACTION_WATCH_TRAILER = 1;

    private ArrayObjectAdapter mAdapter;
    private ClassPresenterSelector mPresenterSelector;
    private DetailsOverviewRow mDetailsOverviewRow;
    private DetailsFragmentBackgroundController mDetailsBackground;

    private Movie mMovie;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMovie = (Movie) getActivity().getIntent().getSerializableExtra(MOVIE);

        setupDetailsOverviewRow();
        setupDetailsOverviewRowPresenter();
        setupBackgroundImage();
        setAdapter(mAdapter);
    }

    @Override
    public void onCardImageLoaded(final GlideDrawable resource) {
        mDetailsOverviewRow.setImageDrawable(resource);
        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
    }

    @Override
    public void onBackgroundImageLoaded(final Bitmap bitmap) {
        mDetailsBackground.setCoverBitmap(bitmap);
        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
    }

    @Override
    public MovieDetailsPresenter getPresenter() {
        return new MovieDetailsPresenter();
    }

    private void setupDetailsOverviewRow() {
        mPresenterSelector = new ClassPresenterSelector();
        mAdapter = new ArrayObjectAdapter(mPresenterSelector);

        mDetailsOverviewRow = new DetailsOverviewRow(mMovie);
        mDetailsOverviewRow.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.default_background));
        mPresenter.loadCardImage(mMovie);

        ArrayObjectAdapter actionAdapter = new ArrayObjectAdapter();
        actionAdapter.add(new Action(ACTION_WATCH_TRAILER, getResources().getString(R.string.watch_trailer_1), getResources().getString(R.string.watch_trailer_2)));
        mDetailsOverviewRow.setActionsAdapter(actionAdapter);
        mAdapter.add(mDetailsOverviewRow);
    }

    private void setupDetailsOverviewRowPresenter() {
        // Set detail background.
        final FullWidthDetailsOverviewRowPresenter detailsPresenter = new FullWidthDetailsOverviewRowPresenter(new DetailsDescriptionPresenter());
        detailsPresenter.setBackgroundColor(getResources().getColor(R.color.selected_background, getActivity().getTheme()));

        // Hook up transition element.
        final FullWidthDetailsOverviewSharedElementHelper sharedElementHelper = new FullWidthDetailsOverviewSharedElementHelper();
        sharedElementHelper.setSharedElementEnterTransition(getActivity(), SHARED_ELEMENT_NAME);
        detailsPresenter.setListener(sharedElementHelper);
        detailsPresenter.setParticipatingEntranceTransition(true);
        detailsPresenter.setOnActionClickedListener(action -> {
            switch ((int) action.getId()) {
                case ACTION_WATCH_TRAILER:
                    Intent intent = new Intent(getActivity(), PlaybackActivity.class);
                    intent.putExtra(MOVIE, mMovie);
                    startActivity(intent);
                    break;
            }
        });
        mPresenterSelector.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);
    }

    private void setupBackgroundImage() {
        mDetailsBackground = new DetailsFragmentBackgroundController(this);
        mDetailsBackground.enableParallax();
        mPresenter.loadBackgroundImage(mMovie);
    }
}