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

import android.util.Log;

import com.pimenta.bestv.BuildConfig;
import com.pimenta.bestv.repository.entity.Cast;
import com.pimenta.bestv.repository.entity.CastList;
import com.pimenta.bestv.repository.entity.Genre;
import com.pimenta.bestv.repository.entity.GenreList;
import com.pimenta.bestv.repository.entity.Movie;
import com.pimenta.bestv.repository.entity.MoviePage;
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

    private GenreApi mGenreApi;
    private MovieApi mMovieApi;
    private PersonApi mPersonApi;

    @Inject
    public TmdbMediaRemote(GenreApi genreApi, MovieApi movieApi, PersonApi personApi) {
        mGenreApi = genreApi;
        mMovieApi = movieApi;
        mPersonApi = personApi;
    }

    @Override
    public Single<GenreList> getMovieGenres() {
        return mGenreApi.getMovieGenres(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE);
    }

    @Override
    public Single<MoviePage> getMoviesByGenre(final Genre genre, int page) {
        return mGenreApi.getMoviesByGenre(genre.getId(), BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, false, page);
    }

    @Override
    public Movie getMovie(final int movieId) {
        try {
            return mMovieApi.getMovie(movieId, BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE).execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Error while getting a movie", e);
            return null;
        }
    }

    @Override
    public Single<CastList> getCastByMovie(final Movie movie) {
        return mMovieApi.getCastByMovie(movie.getId(), BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE);
    }

    @Override
    public Single<MoviePage> getRecommendationByMovie(final Movie movie, final int page) {
        return mMovieApi.getRecommendationByMovie(movie.getId(), BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page);
    }

    @Override
    public Single<MoviePage> getSimilarByMovie(final Movie movie, final int page) {
        return mMovieApi.getSimilarByMovie(movie.getId(), BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page);
    }

    @Override
    public Single<VideoList> getVideosByMovie(final Movie movie) {
        return mMovieApi.getVideosByMovie(movie.getId(), BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE);
    }

    @Override
    public Single<MoviePage> getNowPlayingMovies(int page) {
        return mMovieApi.getNowPlayingMovies(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page);
    }

    @Override
    public Single<MoviePage> getPopularMovies(int page) {
        return mMovieApi.getPopularMovies(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page);
    }

    @Override
    public Single<MoviePage> getTopRatedMovies(int page) {
        return mMovieApi.getTopRatedMovies(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page);
    }

    @Override
    public Single<MoviePage> getUpComingMovies(int page) {
        return mMovieApi.getUpComingMovies(BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE, page);
    }

    @Override
    public Single<MoviePage> searchMoviesByQuery(final String query, final int page) {
        return mMovieApi.searchMoviesByQuery(BuildConfig.TMDB_API_KEY, query, BuildConfig.TMDB_FILTER_LANGUAGE, page);
    }

    @Override
    public Single<Cast> getCastDetails(final Cast cast) {
        return mPersonApi.getCastDetails(cast.getId(), BuildConfig.TMDB_API_KEY, BuildConfig.TMDB_FILTER_LANGUAGE);
    }
}