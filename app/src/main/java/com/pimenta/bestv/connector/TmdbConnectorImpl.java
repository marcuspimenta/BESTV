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

import com.pimenta.bestv.BesTV;
import com.pimenta.bestv.R;
import com.pimenta.bestv.api.tmdb.GenreApi;
import com.pimenta.bestv.api.tmdb.MovieApi;
import com.pimenta.bestv.api.tmdb.PersonApi;
import com.pimenta.bestv.model.Cast;
import com.pimenta.bestv.model.CastList;
import com.pimenta.bestv.model.Genre;
import com.pimenta.bestv.model.Movie;
import com.pimenta.bestv.model.MovieList;
import com.pimenta.bestv.model.VideoList;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by marcus on 08-02-2018.
 */
public class TmdbConnectorImpl implements TmdbConnector {

    private static final String TAG = "TmdbConnectorImpl";

    private String mApiKey;
    private String mLanguage;

    private GenreApi mGenreApi;
    private MovieApi mMovieApi;
    private PersonApi mPersonApi;

    @Inject
    public TmdbConnectorImpl(Application application, GenreApi genreApi, MovieApi movieApi, PersonApi personApi) {
        mApiKey = application.getString(R.string.tmdb_api_key);
        mLanguage = application.getString(R.string.tmdb_filter_language);
        mGenreApi = genreApi;
        mMovieApi = movieApi;
        mPersonApi = personApi;
    }

    @Override
    public List<Genre> getGenres() {
        try {
            return mGenreApi.getGenres(mApiKey, mLanguage).execute().body().getGenres();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get the genres", e);
            return null;
        }
    }

    @Override
    public MovieList getMoviesByGenre(final Genre genre, int page) {
        try {
            return mGenreApi.getMovies(genre.getId(), mApiKey, mLanguage, false, page).execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get the movies by genre", e);
            return null;
        }
    }

    @Override
    public Movie getMovie(final int movieId) {
        try {
            return mMovieApi.getMovie(movieId, mApiKey, mLanguage).execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get the movie", e);
            return null;
        }
    }

    @Override
    public CastList getCastByMovie(final Movie movie) {
        try {
            return mMovieApi.getCastByMovie(movie.getTmdbId(), mApiKey, mLanguage).execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get the cast by movie", e);
            return null;
        }
    }

    @Override
    public MovieList getRecommendationByMovie(final Movie movie, final int page) {
        try {
            return mMovieApi.getRecommendationByMovie(movie.getTmdbId(), mApiKey, mLanguage, page).execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get the recommendations", e);
            return null;
        }
    }

    @Override
    public MovieList getSimilarByMovie(final Movie movie, final int page) {
        try {
            return mMovieApi.getSimilarByMovie(movie.getTmdbId(), mApiKey, mLanguage, page).execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get the similar", e);
            return null;
        }
    }

    @Override
    public VideoList getVideosByMovie(final Movie movie) {
        try {
            return mMovieApi.getVideosByMovie(movie.getTmdbId(), mApiKey, mLanguage).execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get the videos", e);
            return null;
        }
    }

    @Override
    public MovieList getNowPlayingMovies(int page) {
        try {
            return mMovieApi.getNowPlayingMovies(mApiKey, mLanguage, page).execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get the now playing movies", e);
            return null;
        }
    }

    @Override
    public MovieList getPopularMovies(int page) {
        try {
            return mMovieApi.getPopularMovies(mApiKey, mLanguage, page).execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get the popular movies", e);
            return null;
        }
    }

    @Override
    public MovieList getTopRatedMovies(int page) {
        try {
            return mMovieApi.getTopRatedMovies(mApiKey, mLanguage, page).execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get the rated movies", e);
            return null;
        }
    }

    @Override
    public MovieList getUpComingMovies(int page) {
        try {
            return mMovieApi.getUpComingMovies(mApiKey, mLanguage, page).execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get the up coming movies", e);
            return null;
        }
    }

    @Override
    public MovieList searchMoviesByQuery(final String query, final int page) {
        try {
            return mMovieApi.searchMoviesByQuery(mApiKey, URLEncoder.encode(query, "UTF-8"), mLanguage, page).execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Failed to search the movies by query", e);
            return null;
        }
    }

    @Override
    public Cast getCastDetails(final Cast cast) {
        try {
            return mPersonApi.getCastDetails(cast.getId(), mApiKey, mLanguage).execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Failed to search the movies by query", e);
            return null;
        }
    }

    /**
     * Represents the movie list type
     */
    public enum MovieListType {
        FAVORITES(R.string.favorites),
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