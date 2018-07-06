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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseSupportFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.DividerRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.PageRow;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.SectionRow;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pimenta.bestv.BesTV;
import com.pimenta.bestv.R;
import com.pimenta.bestv.repository.MediaRepository;
import com.pimenta.bestv.repository.entity.Genre;
import com.pimenta.bestv.presenter.MovieBrowseContract;
import com.pimenta.bestv.presenter.MovieBrowsePresenter;
import com.pimenta.bestv.view.fragment.base.BaseBrowseFragment;
import com.pimenta.bestv.view.widget.GenreHeaderItem;
import com.pimenta.bestv.view.widget.MovieListTypeHeaderItem;

import java.util.List;

/**
 * Created by marcus on 07-02-2018.
 */
public class MovieBrowseFragment extends BaseBrowseFragment<MovieBrowsePresenter> implements MovieBrowseContract {

    public static final String TAG = "MovieBrowseFragment";
    private static final int TOP_MOVIES_LIST_ID = 1;
    private static final int GENRE_ID = 2;
    private static final int FAVORITE_INDEX = 4;

    private static final PageRow sFavoritePageRow = new PageRow(
            new MovieListTypeHeaderItem(TOP_MOVIES_LIST_ID, MediaRepository.WorkType.FAVORITES_MOVIES));

    private int mCountFragment = 0;
    private boolean mShowProgress = false;
    private ArrayObjectAdapter mRowsAdapter;

    public static MovieBrowseFragment newInstance() {
        return new MovieBrowseFragment();
    }

    @Override
    public boolean isShowingHeaders() {
        return super.isShowingHeaders();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUIElements();
    }

    @Override
    public void onDestroy() {
        getProgressBarManager().hide();
        super.onDestroy();
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
        setupMainList();
        mPresenter.loadData();
    }

    @Override
    protected void injectPresenter() {
        BesTV.getApplicationComponent().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter.hasFavoriteMovies()) {
            if (mRowsAdapter.indexOf(sFavoritePageRow) == -1) {
                mRowsAdapter.add(FAVORITE_INDEX, sFavoritePageRow);
            }
        } else {
            if (mRowsAdapter.indexOf(sFavoritePageRow) == FAVORITE_INDEX) {
                if (getSelectedPosition() == FAVORITE_INDEX) {
                    setSelectedPosition(FAVORITE_INDEX - 1);
                }
                mRowsAdapter.remove(sFavoritePageRow);
            }
        }
    }

    @Override
    public void onDataLoaded(final boolean hasFavoriteMovie, final List<Genre> genres) {
        if (hasFavoriteMovie && mRowsAdapter.indexOf(sFavoritePageRow) == -1) {
            mRowsAdapter.add(FAVORITE_INDEX, sFavoritePageRow);
        }

        if (genres != null && genres.size() > 0) {
            mRowsAdapter.add(new DividerRow());
            mRowsAdapter.add(new SectionRow(getResources().getString(R.string.genres)));

            for (final Genre genre : genres) {
                mRowsAdapter.add(new PageRow(new GenreHeaderItem(GENRE_ID, genre)));
            }
        }

        getProgressBarManager().hide();
        startEntranceTransition();
    }

    private void setupUIElements() {
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);
        setOnSearchClickedListener(new SearchClickListener());

        setSearchAffordanceColor(getResources().getColor(R.color.background_color, getActivity().getTheme()));
        getMainFragmentRegistry().registerFragment(PageRow.class, new PageRowFragmentFactory());

        BackgroundManager.getInstance(getActivity()).attach(getActivity().getWindow());
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mPresenter.getDisplayMetrics());

        prepareEntranceTransition();
    }

    private void setupMainList() {
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        setAdapter(mRowsAdapter);
        mRowsAdapter.add(new PageRow(new MovieListTypeHeaderItem(TOP_MOVIES_LIST_ID, MediaRepository.WorkType.NOW_PLAYING_MOVIES)));
        mRowsAdapter.add(new PageRow(new MovieListTypeHeaderItem(TOP_MOVIES_LIST_ID, MediaRepository.WorkType.POPULAR_MOVIES)));
        mRowsAdapter.add(new PageRow(new MovieListTypeHeaderItem(TOP_MOVIES_LIST_ID, MediaRepository.WorkType.TOP_RATED_MOVIES)));
        mRowsAdapter.add(new PageRow(new MovieListTypeHeaderItem(TOP_MOVIES_LIST_ID, MediaRepository.WorkType.UP_COMING_MOVIES)));
    }

    private class SearchClickListener implements View.OnClickListener {

        @Override
        public void onClick(final View v) {
            addFragment(SearchFragment.newInstance(), SearchFragment.TAG);
        }
    }

    private class PageRowFragmentFactory extends BrowseSupportFragment.FragmentFactory {

        @Override
        public Fragment createFragment(final Object rowObj) {
            if (mCountFragment++ >= 1) {
                mShowProgress = true;
            }

            final Row row = (Row) rowObj;
            switch ((int) row.getHeaderItem().getId()) {
                case TOP_MOVIES_LIST_ID:
                    final MovieListTypeHeaderItem movieListTypeHeaderItem = (MovieListTypeHeaderItem) row.getHeaderItem();
                    MovieBrowseFragment.this.setTitle(row.getHeaderItem().getName());
                    return TopMovieGridFragment.newInstance(movieListTypeHeaderItem.getMovieListType(), mShowProgress);
                case GENRE_ID:
                    final GenreHeaderItem genreHeaderItem = (GenreHeaderItem) row.getHeaderItem();
                    MovieBrowseFragment.this.setTitle(genreHeaderItem.getGenre().getName());
                    return GenreMovieGridFragment.newInstance(genreHeaderItem.getGenre(), mShowProgress);
            }

            throw new IllegalArgumentException(String.format("Invalid row %s", rowObj));
        }
    }
}