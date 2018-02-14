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

package com.pimenta.bestv.activities;

import android.content.Intent;
import android.os.Bundle;

import com.pimenta.bestv.BesTV;
import com.pimenta.bestv.R;
import com.pimenta.bestv.fragments.MovieDetailsFragment;
import com.pimenta.bestv.models.Movie;
import com.pimenta.bestv.presenters.DefaultPresenter;

/**
 * Created by marcus on 11-02-2018.
 */
public class MovieDetailsActivity extends BaseActivity<DefaultPresenter> {

    public static Intent newInstance(Movie movie) {
        Intent intent = new Intent(BesTV.get(), MovieDetailsActivity.class);
        intent.putExtra(MovieDetailsFragment.MOVIE, movie);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
    }

    @Override
    protected DefaultPresenter getPresenter() {
        return new DefaultPresenter();
    }
}