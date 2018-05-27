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

package com.pimenta.bestv.repository.remote;

import android.app.Application;
import android.util.Log;

import com.pimenta.bestv.R;
import com.pimenta.bestv.repository.entity.Cast;
import com.pimenta.bestv.repository.entity.CastList;
import com.pimenta.bestv.repository.entity.Genre;
import com.pimenta.bestv.repository.entity.GenreList;
import com.pimenta.bestv.repository.entity.Movie;
import com.pimenta.bestv.repository.entity.MovieList;
import com.pimenta.bestv.repository.entity.VideoList;
import com.pimenta.bestv.repository.remote.api.tmdb.GenreApi;
import com.pimenta.bestv.repository.remote.api.tmdb.MovieApi;
import com.pimenta.bestv.repository.remote.api.tmdb.PersonApi;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by marcus on 08-02-2018.
 */
public class TmdbMediaRemote implements MediaRemote {

    private static final String TAG = TmdbMediaRemote.class.getSimpleName();

    private String mApiKey;
    private String mLanguage;

    private GenreApi mGenreApi;
    private MovieApi mMovieApi;
    private PersonApi mPersonApi;

    @Inject
    public TmdbMediaRemote(Application application, GenreApi genreApi, MovieApi movieApi, PersonApi personApi) {
        mApiKey = application.getString(R.string.tmdb_api_key);
        mLanguage = application.getString(R.string.tmdb_filter_language);
        mGenreApi = genreApi;
        mMovieApi = movieApi;
        mPersonApi = personApi;
    }

    @Override
    public Single<GenreList> getGenres() {
        return mGenreApi.getGenres(mApiKey, mLanguage);
    }

    @Override
    public Single<MovieList> getMoviesByGenre(final Genre genre, int page) {
        return mGenreApi.getMovies(genre.getId(), mApiKey, mLanguage, false, page);
    }

    @Override
    public Movie getMovie(final int movieId) {
        try {
            return mMovieApi.getMovie(movieId, mApiKey, mLanguage).execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Error while getting a movie", e);
            return null;
        }
    }

    @Override
    public Single<CastList> getCastByMovie(final Movie movie) {
        return mMovieApi.getCastByMovie(movie.getId(), mApiKey, mLanguage);
    }

    @Override
    public Single<MovieList> getRecommendationByMovie(final Movie movie, final int page) {
        return mMovieApi.getRecommendationByMovie(movie.getId(), mApiKey, mLanguage, page);
    }

    @Override
    public Single<MovieList> getSimilarByMovie(final Movie movie, final int page) {
        return mMovieApi.getSimilarByMovie(movie.getId(), mApiKey, mLanguage, page);
    }

    @Override
    public Single<VideoList> getVideosByMovie(final Movie movie) {
        return mMovieApi.getVideosByMovie(movie.getId(), mApiKey, mLanguage);
    }

    @Override
    public Single<MovieList> getNowPlayingMovies(int page) {
        return mMovieApi.getNowPlayingMovies(mApiKey, mLanguage, page);
    }

    @Override
    public Single<MovieList> getPopularMovies(int page) {
        return mMovieApi.getPopularMovies(mApiKey, mLanguage, page);
    }

    @Override
    public Single<MovieList> getTopRatedMovies(int page) {
        return mMovieApi.getTopRatedMovies(mApiKey, mLanguage, page);
    }

    @Override
    public Single<MovieList> getUpComingMovies(int page) {
        return mMovieApi.getUpComingMovies(mApiKey, mLanguage, page);
    }

    @Override
    public Single<MovieList> searchMoviesByQuery(final String query, final int page) {
        return mMovieApi.searchMoviesByQuery(mApiKey, query, mLanguage, page);
    }

    @Override
    public Single<Cast> getCastDetails(final Cast cast) {
        return mPersonApi.getCastDetails(cast.getId(), mApiKey, mLanguage);
    }
}