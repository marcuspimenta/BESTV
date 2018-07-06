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

package com.pimenta.bestv.repository;

import android.support.annotation.NonNull;

import com.pimenta.bestv.repository.database.dao.MovieDao;
import com.pimenta.bestv.repository.entity.Cast;
import com.pimenta.bestv.repository.entity.CastList;
import com.pimenta.bestv.repository.entity.Genre;
import com.pimenta.bestv.repository.entity.GenreList;
import com.pimenta.bestv.repository.entity.Movie;
import com.pimenta.bestv.repository.entity.MoviePage;
import com.pimenta.bestv.repository.entity.VideoList;
import com.pimenta.bestv.repository.remote.MediaRemote;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by marcus on 05-03-2018.
 */
public class MediaRepositoryImpl implements MediaRepository {

    private MovieDao mMovieDao;
    private MediaRemote mMediaRemote;

    @Inject
    public MediaRepositoryImpl(MovieDao movieDao, MediaRemote mediaRemote) {
        mMovieDao = movieDao;
        mMediaRemote = mediaRemote;
    }

    @Override
    public boolean isFavorite(final Movie movie) {
        final Movie movieFind = mMovieDao.getMovieById(movie.getId());
        if (movieFind != null) {
            movie.setId(movieFind.getId());
            return true;
        }
        return false;
    }

    @Override
    public boolean hasFavoriteMovie() {
        final List<Movie> favoriteMovies = mMovieDao.queryForAll();
        return favoriteMovies != null ? favoriteMovies.size() > 0 : false;
    }

    @Override
    public boolean saveFavoriteMovie(@NonNull final Movie movie) {
        return mMovieDao.create(movie);
    }

    @Override
    public boolean deleteFavoriteMovie(@NonNull final Movie movie) {
        return mMovieDao.delete(movie);
    }

    @Override
    public Single<List<Movie>> getFavoriteMovies() {
        return Single.create(e -> {
            final List<Movie> favoriteMovies = mMovieDao.queryForAll();
            final List<Movie> movies = new ArrayList<>();
            for (final Movie movie : favoriteMovies) {
                final Movie detailMovie = mMediaRemote.getMovie(movie.getId());
                if (detailMovie != null) {
                    detailMovie.setFavorite(true);
                    movies.add(detailMovie);
                }
            }
            e.onSuccess(movies);
        });
    }

    @Override
    public Single<MoviePage> loadMoviesByType(int page, MediaRepository.MovieListType movieListType) {
        switch (movieListType) {
            case NOW_PLAYING:
                return mMediaRemote.getNowPlayingMovies(page);
            case POPULAR:
                return mMediaRemote.getPopularMovies(page);
            case TOP_RATED:
                return mMediaRemote.getTopRatedMovies(page);
            case UP_COMING:
                return mMediaRemote.getUpComingMovies(page);
            default:
                return Single.error(new Throwable());
        }
    }

    @Override
    public Single<GenreList> getMovieGenres() {
        return mMediaRemote.getMovieGenres();
    }

    @Override
    public Single<MoviePage> getMoviesByGenre(final Genre genre, final int page) {
        return mMediaRemote.getMoviesByGenre(genre, page);
    }

    @Override
    public Movie getMovie(final int movieId) {
        return mMediaRemote.getMovie(movieId);
    }

    @Override
    public Single<CastList> getCastByMovie(final Movie movie) {
        return mMediaRemote.getCastByMovie(movie);
    }

    @Override
    public Single<MoviePage> getRecommendationByMovie(final Movie movie, final int page) {
        return mMediaRemote.getRecommendationByMovie(movie, page);
    }

    @Override
    public Single<MoviePage> getSimilarByMovie(final Movie movie, final int page) {
        return mMediaRemote.getSimilarByMovie(movie, page);
    }

    @Override
    public Single<VideoList> getVideosByMovie(final Movie movie) {
        return mMediaRemote.getVideosByMovie(movie);
    }

    @Override
    public Single<MoviePage> getNowPlayingMovies(final int page) {
        return mMediaRemote.getNowPlayingMovies(page);
    }

    @Override
    public Single<MoviePage> getPopularMovies(final int page) {
        return mMediaRemote.getPopularMovies(page);
    }

    @Override
    public Single<MoviePage> getTopRatedMovies(final int page) {
        return mMediaRemote.getTopRatedMovies(page);
    }

    @Override
    public Single<MoviePage> getUpComingMovies(final int page) {
        return mMediaRemote.getUpComingMovies(page);
    }

    @Override
    public Single<MoviePage> searchMoviesByQuery(final String query, final int page) {
        return mMediaRemote.searchMoviesByQuery(query, page);
    }

    @Override
    public Single<Cast> getCastDetails(final Cast cast) {
        return mMediaRemote.getCastDetails(cast);
    }
}