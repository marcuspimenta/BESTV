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

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.ProgressBarManager;
import android.support.v17.leanback.app.SearchSupportFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pimenta.bestv.BesTV;
import com.pimenta.bestv.R;
import com.pimenta.bestv.presenter.SearchContract;
import com.pimenta.bestv.presenter.SearchPresenter;
import com.pimenta.bestv.repository.entity.Work;
import com.pimenta.bestv.view.activity.WorkDetailsActivity;
import com.pimenta.bestv.view.fragment.base.BaseSearchFragment;
import com.pimenta.bestv.view.widget.WorkCardPresenter;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by marcus on 12-03-2018.
 */
public class SearchFragment extends BaseSearchFragment<SearchPresenter> implements SearchContract,
        SearchSupportFragment.SearchResultProvider {

    public static final String TAG = SearchFragment.class.getSimpleName();

    private static final int SEARCH_FRAGMENT_REQUEST_CODE = 1;
    private static final int MOVIE_HEADER_ID = 1;
    private static final int TV_SHOW_HEADER_ID = 2;
    private static final int BACKGROUND_UPDATE_DELAY = 300;

    private static final Handler sHandler = new Handler();
    private static final ProgressBarManager sProgressBarManager = new ProgressBarManager();

    private ArrayObjectAdapter mRowsAdapter;
    private ArrayObjectAdapter mMovieRowAdapter;
    private ArrayObjectAdapter mTvShowRowAdapter;
    private BackgroundManager mBackgroundManager;

    private Work mWorkSelected;
    private Timer mBackgroundTimer;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sProgressBarManager.setRootView(container);
        sProgressBarManager.enableProgressBar();
        sProgressBarManager.setInitialDelay(0);

        final View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            view.setBackgroundColor(getResources().getColor(android.support.v17.leanback.R.color.lb_playback_controls_background_light));
        }
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        clearAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mWorkSelected != null) {
            loadBackdropImage(false);
        }
    }

    @Override
    public void onDestroyView() {
        sProgressBarManager.hide();
        if (mBackgroundTimer != null) {
            mBackgroundTimer.cancel();
        }
        super.onDestroyView();
    }

    @Override
    protected void injectPresenter() {
        BesTV.getApplicationComponent().inject(this);
    }

    @Override
    public void onResultLoaded(final List<? extends Work> movies, final List<? extends Work> tvShows) {
        boolean hasMovies = movies != null && movies.size() > 0;
        boolean hasTvShows = tvShows != null && tvShows.size() > 0;

        sProgressBarManager.hide();
        if (hasMovies || hasTvShows) {
            mRowsAdapter.clear();

            final WorkCardPresenter workCardPresenter = new WorkCardPresenter();
            workCardPresenter.setLoadWorkPosterListener((movie, imageView) -> mPresenter.loadWorkPosterImage(movie, imageView));

            if (hasMovies) {
                final HeaderItem header = new HeaderItem(MOVIE_HEADER_ID, getString(R.string.movies));
                mMovieRowAdapter = new ArrayObjectAdapter(workCardPresenter);
                mMovieRowAdapter.addAll(0, movies);
                mRowsAdapter.add(new ListRow(header, mMovieRowAdapter));
            }
            if (hasTvShows) {
                final HeaderItem header = new HeaderItem(TV_SHOW_HEADER_ID, getString(R.string.tv_shows));
                mTvShowRowAdapter = new ArrayObjectAdapter(workCardPresenter);
                mTvShowRowAdapter.addAll(0, tvShows);
                mRowsAdapter.add(new ListRow(header, mTvShowRowAdapter));
            }
        } else {
            clearAdapter();
        }
    }

    @Override
    public void onMoviesLoaded(final List<? extends Work> movies) {
        if (movies != null) {
            for (final Work work : movies) {
                if (mMovieRowAdapter.indexOf(work) == -1) {
                    mMovieRowAdapter.add(work);
                }
            }
        }
    }

    @Override
    public void onTvShowsLoaded(final List<? extends Work> tvShows) {
        if (tvShows != null) {
            for (final Work work : tvShows) {
                if (mTvShowRowAdapter.indexOf(work) == -1) {
                    mTvShowRowAdapter.add(work);
                }
            }
        }
    }

    @Override
    public void onBackdropImageLoaded(final Bitmap bitmap) {
        mBackgroundManager.setBitmap(bitmap);
    }

    @Override
    public ObjectAdapter getResultsAdapter() {
        return mRowsAdapter;
    }

    @Override
    public boolean onQueryTextChange(final String query) {
        searchQuery(query);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(final String query) {
        searchQuery(query);
        return true;
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case SEARCH_FRAGMENT_REQUEST_CODE:
                final View view = getView();
                if (view != null) {
                    view.requestFocus();
                }
                break;
        }
    }

    private void setupUI() {
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mPresenter.getDisplayMetrics());

        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        setSearchResultProvider(this);
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
        setOnItemViewClickedListener(new ItemViewClickedListener());
    }

    private void searchQuery(String query) {
        mRowsAdapter.clear();
        sProgressBarManager.show();
        mPresenter.searchWorksByQuery(query);
    }

    private void clearAdapter() {
        mBackgroundManager.setBitmap(null);
        mRowsAdapter.clear();
        final ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new WorkCardPresenter());
        final HeaderItem header = new HeaderItem(0, getString(R.string.no_results));
        mRowsAdapter.add(new ListRow(header, listRowAdapter));
    }

    private void loadBackdropImage(boolean delay) {
        if (mWorkSelected == null) {
            return;
        }

        if (mBackgroundTimer != null) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                sHandler.post(() -> {
                    mPresenter.loadBackdropImage(mWorkSelected);
                    mBackgroundTimer.cancel();
                });
            }
        }, delay ? BACKGROUND_UPDATE_DELAY : 0);
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {

        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            mWorkSelected = (Work) item;
            loadBackdropImage(true);

            if (row != null && row.getHeaderItem() != null) {
                switch ((int) row.getHeaderItem().getId()) {
                    case MOVIE_HEADER_ID:
                        if (mMovieRowAdapter.indexOf(mWorkSelected) >= mMovieRowAdapter.size() - 1) {
                            mPresenter.loadMovies();
                        }
                        break;
                    case TV_SHOW_HEADER_ID:
                        if (mTvShowRowAdapter.indexOf(mWorkSelected) >= mTvShowRowAdapter.size() - 1) {
                            mPresenter.loadTvShows();
                        }
                        break;
                }
            }
        }
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {

        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            final Work work = (Work) item;
            final Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                    ((ImageCardView) itemViewHolder.view).getMainImageView(), WorkDetailsFragment.SHARED_ELEMENT_NAME).toBundle();
            startActivityForResult(WorkDetailsActivity.newInstance(getContext(), work), SEARCH_FRAGMENT_REQUEST_CODE, bundle);
        }
    }
}