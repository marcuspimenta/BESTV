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

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.pimenta.bestv.connector.TmdbConnector;
import com.pimenta.bestv.database.DatabaseHelper;
import com.pimenta.bestv.model.Movie;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by marcus on 05-03-2018.
 */
public class MovieManagerImpl implements MovieManager {

    private DatabaseHelper mDatabaseHelper;
    private TmdbConnector mTmdbConnector;
    private RuntimeExceptionDao<Movie, ?> mMovieDao;

    @Inject
    public MovieManagerImpl(DatabaseHelper databaseHelper, TmdbConnector tmdbConnector) {
        mDatabaseHelper = databaseHelper;
        mTmdbConnector = tmdbConnector;
        mMovieDao = mDatabaseHelper.getRuntimeExceptionDao(Movie.class);
    }

    @Override
    public boolean isFavorite(final Movie movie) {
        final List<Movie> movies = mMovieDao.queryForEq(Movie.FIELD_TMDB_ID, movie.getTmdbId());
        final Movie movieFind = movies != null && movies.size() > 0 ? movies.get(0) : null;
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
        return mMovieDao.create(movie) > 0;
    }

    @Override
    public boolean deleteFavoriteMovie(@NonNull final Movie movie) {
        return mMovieDao.delete(movie) > 0;
    }

    @Override
    public List<Movie> getFavoriteMovies() {
        final List<Movie> favoriteMovies = mMovieDao.queryForAll();
        final List<Movie> movies = new ArrayList<>();
        for (final Movie movie : favoriteMovies) {
            final Movie detailMovie = mTmdbConnector.getMovie(movie.getTmdbId());
            if (detailMovie != null) {
                detailMovie.setFavorite(true);
                movies.add(detailMovie);
            }
        }
        return movies;
    }
}