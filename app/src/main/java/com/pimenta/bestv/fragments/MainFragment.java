/*
 * Copyright (C) 2017 The Android Open Source Project
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

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pimenta.bestv.R;
import com.pimenta.bestv.models.Genre;
import com.pimenta.bestv.presenters.MainCallback;
import com.pimenta.bestv.presenters.MainPresenter;
import com.pimenta.bestv.widget.CardPresenter;

import java.util.List;
import java.util.Timer;

/**
 * Created by marcus on 07-02-2018.
 */
public class MainFragment extends BaseBrowseFragment<MainPresenter> implements MainCallback {

    private static final String TAG = "MainFragment";
    private static final int BACKGROUND_UPDATE_DELAY = 300;

    private final Handler mHandler = new Handler();
    private ArrayObjectAdapter mRowsAdapter;
    private Timer mBackgroundTimer;
    private BackgroundManager mBackgroundManager;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prepareBackgroundManager();
        setupUIElements();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        getProgressBarManager().setRootView(container);
        getProgressBarManager().enableProgressBar();
        getProgressBarManager().setInitialDelay(0);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getProgressBarManager().show();
        mController.loadGenres();
    }

    @Override
    public void onDestroy() {
        getProgressBarManager().hide();
        if (mBackgroundTimer != null) {
            mBackgroundTimer.cancel();
        }
        super.onDestroy();
    }

    @Override
    public void onGenresLoaded(final List<Genre> genres) {
        getProgressBarManager().hide();
        loadRows(genres);
    }

    @Override
    public MainPresenter getController() {
        return new MainPresenter();
    }

    private void prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mController.getDisplayMetrics());
    }

    private void setupUIElements() {
        setTitle(getString(R.string.app_name));
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        setBrandColor(getResources().getColor(R.color.fastlane_background, getActivity().getTheme()));
        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque, getActivity().getTheme()));

        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    private void loadRows(final List<Genre> genres) {
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

        for (final Genre genre : genres) {
            final ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
            final HeaderItem header = new HeaderItem(genre.getName());
            mRowsAdapter.add(new ListRow(header, listRowAdapter));
        }

        setAdapter(mRowsAdapter);
    }


    private final class ItemViewClickedListener implements OnItemViewClickedListener {

        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            /*if (item instanceof Movie) {
                Movie movie = (Movie) item;
                Log.d(TAG, "Item: " + item.toString());
                Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                intent.putExtra(MovieDetailsFragment.MOVIE, movie);

                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
                        MovieDetailsFragment.SHARED_ELEMENT_NAME).toBundle();
                getActivity().startActivity(intent, bundle);
            }*/
        }
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {

        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            /*if (item instanceof Movie) {
                final Movie movie = (Movie) item;
                if (mBackgroundTimer != null) {
                    mBackgroundTimer.cancel();
                }
                mBackgroundTimer = new Timer();
                mBackgroundTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mHandler.post(() -> {
                            mController.loadImage(mBackgroundManager, movie.getBackgroundImageUrl());
                            mBackgroundTimer.cancel();
                        });
                    }
                }, BACKGROUND_UPDATE_DELAY);
            }*/
        }
    }
}