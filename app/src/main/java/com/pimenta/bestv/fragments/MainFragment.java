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

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.DividerRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.PageRow;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.SectionRow;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pimenta.bestv.R;
import com.pimenta.bestv.connectors.TmdbConnectorImpl;
import com.pimenta.bestv.fragments.bases.BaseBrowseFragment;
import com.pimenta.bestv.models.Genre;
import com.pimenta.bestv.presenters.MainCallback;
import com.pimenta.bestv.presenters.MainPresenter;
import com.pimenta.bestv.widget.GenreHeaderItem;
import com.pimenta.bestv.widget.MovieListTypeHeaderItem;

import java.util.List;

/**
 * Created by marcus on 07-02-2018.
 */
public class MainFragment extends BaseBrowseFragment<MainPresenter> implements MainCallback {

    private static final String TAG = "MainFragment";
    private static final int TOP_MOVIES_LIST_ID = 1;
    private static final int GENRE_ID = 2;

    private ArrayObjectAdapter mRowsAdapter;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        //getProgressBarManager().show();
        setupMainList();
        mPresenter.loadGenres();
    }

    @Override
    public void onDestroy() {
        //getProgressBarManager().hide();
        super.onDestroy();
    }

    @Override
    public void onGenresLoaded(final List<Genre> genres) {
        //getProgressBarManager().hide();
        loadRows(genres);

        startEntranceTransition();
    }

    @Override
    public MainPresenter getPresenter() {
        return new MainPresenter();
    }

    private void setupUIElements() {
        //setTitle(getString(R.string.app_name));
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        //setBrandColor(getResources().getColor(R.color.fastlane_background, getActivity().getTheme()));
        //setSearchAffordanceColor(getResources().getColor(R.color.search_opaque, getActivity().getTheme()));
        getMainFragmentRegistry().registerFragment(PageRow.class, new PageRowFragmentFactory());

        BackgroundManager.getInstance(getActivity()).attach(getActivity().getWindow());
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mPresenter.getDisplayMetrics());

        prepareEntranceTransition();
    }

    private void setupMainList() {
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        setAdapter(mRowsAdapter);
        mRowsAdapter.add(new PageRow(new MovieListTypeHeaderItem(TOP_MOVIES_LIST_ID, TmdbConnectorImpl.MovieListType.NOW_PLAYING)));
        mRowsAdapter.add(new PageRow(new MovieListTypeHeaderItem(TOP_MOVIES_LIST_ID, TmdbConnectorImpl.MovieListType.POPULAR)));
        mRowsAdapter.add(new PageRow(new MovieListTypeHeaderItem(TOP_MOVIES_LIST_ID, TmdbConnectorImpl.MovieListType.TOP_RATED)));
        mRowsAdapter.add(new PageRow(new MovieListTypeHeaderItem(TOP_MOVIES_LIST_ID, TmdbConnectorImpl.MovieListType.UP_COMING)));
    }

    private void loadRows(final List<Genre> genres) {
        if (genres != null && genres.size() > 0) {
            mRowsAdapter.add(new DividerRow());
            mRowsAdapter.add(new SectionRow(getResources().getString(R.string.genres)));

            for (final Genre genre : genres) {
                mRowsAdapter.add(new PageRow(new GenreHeaderItem(GENRE_ID, genre)));
            }
        }
    }

    private class PageRowFragmentFactory extends BrowseFragment.FragmentFactory {

        @Override
        public Fragment createFragment(Object rowObj) {
            final Row row = (Row) rowObj;

            switch ((int) row.getHeaderItem().getId()) {
                case TOP_MOVIES_LIST_ID:
                    final MovieListTypeHeaderItem movieListTypeHeaderItem = (MovieListTypeHeaderItem) row.getHeaderItem();
                    MainFragment.this.setTitle(row.getHeaderItem().getName());
                    return TopMovieGridFragment.newInstance(movieListTypeHeaderItem.getMovieListType());
                case GENRE_ID:
                    final GenreHeaderItem genreHeaderItem = (GenreHeaderItem) row.getHeaderItem();
                    MainFragment.this.setTitle(genreHeaderItem.getGenre().getName());
                    return GenreMovieGridFragment.newInstance(genreHeaderItem.getGenre());
            }

            throw new IllegalArgumentException(String.format("Invalid row %s", rowObj));
        }
    }
}