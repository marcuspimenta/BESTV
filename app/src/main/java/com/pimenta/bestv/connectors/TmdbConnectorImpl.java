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

package com.pimenta.bestv.connectors;

import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.pimenta.bestv.BesTV;
import com.pimenta.bestv.R;
import com.pimenta.bestv.api.tmdb.Tmdb;
import com.pimenta.bestv.managers.DeviceManager;
import com.pimenta.bestv.models.Genre;
import com.pimenta.bestv.models.Movie;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;

/**
 * Created by marcus on 08-02-2018.
 */
public class TmdbConnectorImpl implements TmdbConnector {

    private static final String TAG = "TmdbConnectorImpl";

    @Inject
    DeviceManager mDeviceManager;

    private String mApiKey;
    private String mLanguage;
    private String mSortBy;

    private Tmdb mTmdb;

    @Inject
    public TmdbConnectorImpl(Gson gson, Executor threadPool) {
        mApiKey = BesTV.get().getString(R.string.tmdb_api_key);
        mLanguage = BesTV.get().getString(R.string.tmdb_filter_language);
        mSortBy = BesTV.get().getString(R.string.tmdb_filer_sort_by_desc);
        mTmdb = new Tmdb(BesTV.get().getString(R.string.tmdb_base_url_api), gson, threadPool);
    }

    @Override
    public List<Genre> getGenres() {
        try {
            return mTmdb.getGenreApi().getGenres(mApiKey, mLanguage).execute().body().getGenres();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get the genres", e);
            return null;
        }
    }

    @Override
    public List<Movie> getMoviesByGenre(final Genre genre) {
        try {
            return mTmdb.getGenreApi().getMovies(genre.getId(), mApiKey, mLanguage, false, mSortBy).execute().body().getMovies();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get the movies by genre", e);
            return null;
        }
    }

    @Override
    public List<Movie> getNowPlayingMovies() {
        try {
            final String region = mDeviceManager.getCountryCode();
            return mTmdb.getMovieApi().getNowPlayingMovies(mApiKey, mLanguage, !TextUtils.isEmpty(region) ? region : "").execute().body().getMovies();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get the movies by genre", e);
            return null;
        }
    }

    @Override
    public List<Movie> getPopularMovies() {
        try {
            return mTmdb.getMovieApi().getPopularMovies(mApiKey, mLanguage).execute().body().getMovies();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get the movies by genre", e);
            return null;
        }
    }

    @Override
    public List<Movie> getTopRatedMovies() {
        try {
            return mTmdb.getMovieApi().getTopRatedMovies(mApiKey, mLanguage).execute().body().getMovies();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get the movies by genre", e);
            return null;
        }
    }

    @Override
    public List<Movie> getUpComingMovies() {
        try {
            return mTmdb.getMovieApi().getUpComingMovies(mApiKey, mLanguage).execute().body().getMovies();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get the movies by genre", e);
            return null;
        }
    }

    public enum MovieListType {
        NOW_PLAYING(R.string.now_playing),
        POPULAR(R.string.popular),
        TOP_RATED(R.string.top_rated),
        UP_COMING(R.string.up_coming);

        private String mName;

        MovieListType(@StringRes int nameResource) {
            mName = BesTV.get().getString(nameResource);
        }

        public String getName() {
            return mName;
        }
    }

}