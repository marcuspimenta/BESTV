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

package com.pimenta.bestv.feature.workbrowse.ui;

import android.content.Context;
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
import com.pimenta.bestv.feature.workbrowse.presenter.WorkBrowsePresenter;
import com.pimenta.bestv.feature.workbrowse.presenter.WorkBrowsePresenter.WorkBrowseContract;
import com.pimenta.bestv.repository.MediaRepository;
import com.pimenta.bestv.repository.entity.Genre;
import com.pimenta.bestv.repository.entity.MovieGenre;
import com.pimenta.bestv.repository.entity.TvShowGenre;
import com.pimenta.bestv.feature.search.ui.SearchActivity;
import com.pimenta.bestv.feature.base.BaseBrowseFragment;
import com.pimenta.bestv.feature.widget.GenreHeaderItem;
import com.pimenta.bestv.feature.widget.WorkTypeHeaderItem;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by marcus on 07-02-2018.
 */
public class WorkBrowseFragment extends BaseBrowseFragment implements WorkBrowseContract {

    public static final String TAG = WorkBrowseFragment.class.getSimpleName();
    private static final int TOP_WORK_LIST_ID = 1;
    private static final int WORK_GENRE_ID = 2;
    private static final int FAVORITE_INDEX = 0;

    private static final PageRow sFavoritePageRow = new PageRow(new WorkTypeHeaderItem(TOP_WORK_LIST_ID,
          MediaRepository.WorkType.FAVORITES_MOVIES));

    private int mCountFragment = 0;
    private boolean mShowProgress = false;
    private boolean mHasFavorite = false;
    private ArrayObjectAdapter mRowsAdapter;

    @Inject
    WorkBrowsePresenter mPresenter;

    public static WorkBrowseFragment newInstance() {
        return new WorkBrowseFragment();
    }

    @Override
    public boolean isShowingHeaders() {
        return super.isShowingHeaders();
    }

    @Override
    public void onAttach(@Nullable Context context) {
        super.onAttach(context);
        BesTV.getApplicationComponent().inject(this);
        mPresenter.register(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUIElements();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getProgressBarManager().setRootView(container);
        getProgressBarManager().enableProgressBar();
        getProgressBarManager().setInitialDelay(0);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getProgressBarManager().show();
        setupMainList();
        mPresenter.loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRowsAdapter.size() > 0) {
            mPresenter.hasFavorite();
        }
    }

    @Override
    public void onDestroy() {
        getProgressBarManager().hide();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        mPresenter.unRegister();
        super.onDetach();
    }

    @Override
    public void onDataLoaded(boolean hasFavorite, List<MovieGenre> movieGenres,
          final List<TvShowGenre> tvShowGenres) {
        mHasFavorite = hasFavorite;
        if (hasFavorite) {
            mRowsAdapter.add(sFavoritePageRow);
        }

        mRowsAdapter.add(new DividerRow());
        mRowsAdapter.add(new SectionRow(getResources().getString(R.string.movies)));
        mRowsAdapter.add(new PageRow(new WorkTypeHeaderItem(TOP_WORK_LIST_ID, MediaRepository.WorkType.NOW_PLAYING_MOVIES)));
        mRowsAdapter.add(new PageRow(new WorkTypeHeaderItem(TOP_WORK_LIST_ID, MediaRepository.WorkType.POPULAR_MOVIES)));
        mRowsAdapter.add(new PageRow(new WorkTypeHeaderItem(TOP_WORK_LIST_ID, MediaRepository.WorkType.TOP_RATED_MOVIES)));
        mRowsAdapter.add(new PageRow(new WorkTypeHeaderItem(TOP_WORK_LIST_ID, MediaRepository.WorkType.UP_COMING_MOVIES)));

        if (movieGenres != null) {
            for (final Genre genre : movieGenres) {
                mRowsAdapter.add(new PageRow(new GenreHeaderItem(WORK_GENRE_ID, genre)));
            }
        }

        mRowsAdapter.add(new DividerRow());
        mRowsAdapter.add(new SectionRow(getResources().getString(R.string.tv_shows)));
        mRowsAdapter.add(new PageRow(new WorkTypeHeaderItem(TOP_WORK_LIST_ID, MediaRepository.WorkType.AIRING_TODAY_TV_SHOWS)));
        mRowsAdapter.add(new PageRow(new WorkTypeHeaderItem(TOP_WORK_LIST_ID, MediaRepository.WorkType.ON_THE_AIR_TV_SHOWS)));
        mRowsAdapter.add(new PageRow(new WorkTypeHeaderItem(TOP_WORK_LIST_ID, MediaRepository.WorkType.TOP_RATED_TV_SHOWS)));
        mRowsAdapter.add(new PageRow(new WorkTypeHeaderItem(TOP_WORK_LIST_ID, MediaRepository.WorkType.POPULAR_TV_SHOWS)));

        if (tvShowGenres != null) {
            for (final Genre genre : tvShowGenres) {
                mRowsAdapter.add(new PageRow(new GenreHeaderItem(WORK_GENRE_ID, genre)));
            }
        }

        getProgressBarManager().hide();
        startEntranceTransition();
    }

    @Override
    public void onHasFavorite(final boolean hasFavorite) {
        mHasFavorite = hasFavorite;
        if (hasFavorite) {
            if (mRowsAdapter.indexOf(sFavoritePageRow) == -1) {
                mRowsAdapter.add(FAVORITE_INDEX, sFavoritePageRow);
            }
        } else {
            if (mRowsAdapter.indexOf(sFavoritePageRow) == FAVORITE_INDEX) {
                if (getSelectedPosition() == FAVORITE_INDEX) {
                    setSelectedPosition(FAVORITE_INDEX + 3);
                }
            }
        }
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
    }

    private class SearchClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            startActivity(SearchActivity.newInstance(getContext()));
        }
    }

    private class PageRowFragmentFactory extends BrowseSupportFragment.FragmentFactory {

        @Override
        public Fragment createFragment(Object rowObj) {
            if (mCountFragment++ >= 1) {
                mShowProgress = true;
            }
            if (!mHasFavorite && mRowsAdapter.indexOf(sFavoritePageRow) == FAVORITE_INDEX) {
                mRowsAdapter.remove(sFavoritePageRow);
            }

            Row row = (Row) rowObj;
            switch ((int) row.getHeaderItem().getId()) {
                case TOP_WORK_LIST_ID:
                    WorkTypeHeaderItem movieListTypeHeaderItem = (WorkTypeHeaderItem) row.getHeaderItem();
                    setTitle(row.getHeaderItem().getName());
                    return TopWorkGridFragment.newInstance(movieListTypeHeaderItem.getMovieListType(), mShowProgress);
                case WORK_GENRE_ID:
                    GenreHeaderItem genreHeaderItem = (GenreHeaderItem) row.getHeaderItem();
                    setTitle(genreHeaderItem.getGenre().getName());
                    return GenreWorkGridFragment.newInstance(genreHeaderItem.getGenre(), mShowProgress);
            }

            throw new IllegalArgumentException(String.format("Invalid row %s", rowObj));
        }
    }
}