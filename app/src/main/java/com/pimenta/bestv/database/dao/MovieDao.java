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

package com.pimenta.bestv.database.dao;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.pimenta.bestv.database.DatabaseHelper;
import com.pimenta.bestv.model.Movie;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by marcus on 15-04-2018.
 */
public class MovieDao implements Dao<Movie> {

    private RuntimeExceptionDao<Movie, ?> mMovieDao;

    @Inject
    public MovieDao(DatabaseHelper databaseHelper) {
        mMovieDao = databaseHelper.getRuntimeExceptionDao(Movie.class);
    }

    @Override
    public List<Movie> queryForAll() {
        return mMovieDao.queryForAll();
    }

    @Override
    public List<Movie> queryForEq(final String field, final Object value) {
        return mMovieDao.queryForEq(field, value);
    }

    @Override
    public boolean create(final Movie model) {
        return mMovieDao.create(model) > 0;
    }

    @Override
    public boolean update(final Movie model) {
        return mMovieDao.update(model) > 0;
    }

    @Override
    public boolean delete(final Movie model) {
        return mMovieDao.delete(model) > 0;
    }
}