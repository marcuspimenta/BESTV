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

package com.pimenta.bestv.connector;

import android.app.Application;
import android.support.annotation.StringRes;
import android.util.Log;

import com.google.gson.Gson;
import com.pimenta.bestv.BesTV;
import com.pimenta.bestv.R;
import com.pimenta.bestv.api.tmdb.Tmdb;
import com.pimenta.bestv.manager.DeviceManager;
import com.pimenta.bestv.models.CastList;
import com.pimenta.bestv.models.Genre;
import com.pimenta.bestv.models.Movie;
import com.pimenta.bestv.models.MovieList;
import com.pimenta.bestv.models.VideoList;

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

    private Tmdb mTmdb;

    @Inject
    public TmdbConnectorImpl(Application application, Gson gson, Executor threadPool) {
        mApiKey = application.getString(R.string.tmdb_api_key);
        mLanguage = application.getString(R.string.tmdb_filter_language);
        mTmdb = new Tmdb(application.getString(R.string.tmdb_base_url_api), gson, threadPool);
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
    public MovieList getMoviesByGenre(final Genre genre, int page) {
        try {
            return mTmdb.getGenreApi().getMovies(genre.getId(), mApiKey, mLanguage, false, page).execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get the movies by genre", e);
            return null;
        }
    }

    @Override
    public Movie getMovie(final int movieId) {
        try {
            return mTmdb.getMovieApi().getMovie(movieId, mApiKey, mLanguage).execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get the movie", e);
            return null;
        }
    }

    @Override
    public CastList getCastByMovie(final Movie movie) {
        try {
            return mTmdb.getMovieApi().getCastByMovie(movie.getId(), mApiKey, mLanguage).execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get the cast by movie", e);
            return null;
        }
    }

    @Override
    public MovieList getRecommendationByMovie(final Movie movie, final int page) {
        try {
            return mTmdb.getMovieApi().getRecommendationByMovie(movie.getId(), mApiKey, mLanguage, page).execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get the recommendations", e);
            return null;
        }
    }

    @Override
    public MovieList getSimilarByMovie(final Movie movie, final int page) {
        try {
            return mTmdb.getMovieApi().getSimilarByMovie(movie.getId(), mApiKey, mLanguage, page).execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get the similar", e);
            return null;
        }
    }

    @Override
    public VideoList getVideosByMovie(final Movie movie) {
        try {
            return mTmdb.getMovieApi().getVideosByMovie(movie.getId(), mApiKey, mLanguage).execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get the videos", e);
            return null;
        }
    }

    @Override
    public MovieList getNowPlayingMovies(int page) {
        try {
            return mTmdb.getMovieApi().getNowPlayingMovies(mApiKey, mLanguage, page).execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get the now playing movies", e);
            return null;
        }
    }

    @Override
    public MovieList getPopularMovies(int page) {
        try {
            return mTmdb.getMovieApi().getPopularMovies(mApiKey, mLanguage, page).execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get the popular movies", e);
            return null;
        }
    }

    @Override
    public MovieList getTopRatedMovies(int page) {
        try {
            return mTmdb.getMovieApi().getTopRatedMovies(mApiKey, mLanguage, page).execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get the rated movies", e);
            return null;
        }
    }

    @Override
    public MovieList getUpComingMovies(int page) {
        try {
            return mTmdb.getMovieApi().getUpComingMovies(mApiKey, mLanguage, page).execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get the up coming movies", e);
            return null;
        }
    }

    /**
     * Represents the movie list type
     */
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