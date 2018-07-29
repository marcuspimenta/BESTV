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

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.pimenta.bestv.BesTV;
import com.pimenta.bestv.repository.entity.Genre;

/**
 * Created by marcus on 11-02-2018.
 */
public class GenreWorkGridFragment extends AbstractWorkGridFragment {

    private static final String GENRE = "GENRE";

    private Genre mGenre;

    public static GenreWorkGridFragment newInstance(Genre genre, boolean showProgress) {
        Bundle args = new Bundle();
        args.putSerializable(GENRE, genre);
        args.putBoolean(SHOW_PROGRESS, showProgress);

        GenreWorkGridFragment genreMovieGridFragment = new GenreWorkGridFragment();
        genreMovieGridFragment.setArguments(args);
        genreMovieGridFragment.mGenre = genre;
        genreMovieGridFragment.mShowProgress = showProgress;
        return genreMovieGridFragment;
    }

    @Override
    public void onAttach(@Nullable Context context) {
        BesTV.getApplicationComponent().inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mGenre == null) {
            mGenre = (Genre) getArguments().getSerializable(GENRE);
            mShowProgress = getArguments().getBoolean(SHOW_PROGRESS);
        }
    }

    @Override
    void loadData() {
        mPresenter.loadWorkByGenre(mGenre);
    }

    @Override
    public void refreshDada() {
        // DO ANYTHING
    }
}