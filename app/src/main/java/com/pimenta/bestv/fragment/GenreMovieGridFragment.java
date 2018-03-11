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

import android.os.Bundle;

import com.pimenta.bestv.model.Genre;

/**
 * Created by marcus on 11-02-2018.
 */
public class GenreMovieGridFragment extends AbstractMovieGridFragment {

    private static final String GENRE = "GENRE";

    private Genre mGenre;

    public static GenreMovieGridFragment newInstance(Genre genre, boolean showProgress) {
        Bundle args = new Bundle();
        args.putSerializable(GENRE, genre);
        args.putBoolean(SHOW_PROGRESS, showProgress);

        GenreMovieGridFragment genreMovieGridFragment = new GenreMovieGridFragment();
        genreMovieGridFragment.setArguments(args);
        genreMovieGridFragment.mGenre = genre;
        genreMovieGridFragment.mShowProgress = showProgress;
        return genreMovieGridFragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mGenre == null) {
            mGenre = (Genre) getArguments().getSerializable(GENRE);
            mShowProgress = getArguments().getBoolean(SHOW_PROGRESS);
        }
    }

    @Override
    void loadData() {
        mPresenter.loadMoviesByGenre(mGenre);
    }

    @Override
    public void refreshDada() {
        // DO ANYTHING
    }
}