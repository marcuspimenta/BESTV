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

import android.os.Bundle;

import com.pimenta.bestv.connectors.TmdbConnectorImpl;

/**
 * Created by marcus on 11-02-2018.
 */
public class TopMovieGridFragment extends AbstractMovieGridFragment {

    private static final String TYPE = "TYPE";

    private TmdbConnectorImpl.MovieListType mMovieListType;

    public static TopMovieGridFragment newInstance(TmdbConnectorImpl.MovieListType movieListType, boolean showProgress) {
        Bundle args = new Bundle();
        args.putSerializable(TYPE, movieListType);
        args.putBoolean(SHOW_PROGRESS, showProgress);

        TopMovieGridFragment topMovieGridFragment = new TopMovieGridFragment();
        topMovieGridFragment.setArguments(args);
        topMovieGridFragment.mMovieListType = movieListType;
        topMovieGridFragment.mShowProgress = showProgress;
        return topMovieGridFragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mMovieListType == null) {
            mMovieListType = (TmdbConnectorImpl.MovieListType) getArguments().getSerializable(TYPE);
            mShowProgress = getArguments().getBoolean(SHOW_PROGRESS);
        }
    }

    @Override
    void loadData() {
        mPresenter.loadToMoviesByType(mMovieListType);
    }
}