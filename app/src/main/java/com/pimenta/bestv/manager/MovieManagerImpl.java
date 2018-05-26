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

package com.pimenta.bestv.manager;

import android.support.annotation.NonNull;

import com.pimenta.bestv.repository.database.dao.MovieDao;
import com.pimenta.bestv.repository.entity.Movie;
import com.pimenta.bestv.repository.entity.MovieList;
import com.pimenta.bestv.repository.remote.MediaRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by marcus on 05-03-2018.
 */
public class MovieManagerImpl implements MovieManager {

    private MediaRepository mMediaRepository;
    private MovieDao mMovieDao;

    @Inject
    public MovieManagerImpl(MediaRepository mediaRepository, MovieDao movieDao) {
        mMediaRepository = mediaRepository;
        mMovieDao = movieDao;
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
                final Movie detailMovie = mMediaRepository.getMovie(movie.getId());
                if (detailMovie != null) {
                    detailMovie.setFavorite(true);
                    movies.add(detailMovie);
                }
            }
            e.onSuccess(movies);
        });
    }

    @Override
    public Single<MovieList> loadMoviesByType(int page, MovieManager.MovieListType movieListType) {
        switch (movieListType) {
            case NOW_PLAYING:
                return mMediaRepository.getNowPlayingMovies(page);
            case POPULAR:
                return mMediaRepository.getPopularMovies(page);
            case TOP_RATED:
                return mMediaRepository.getTopRatedMovies(page);
            case UP_COMING:
                return mMediaRepository.getUpComingMovies(page);
            default:
                return Single.error(new Throwable());
        }
    }
}