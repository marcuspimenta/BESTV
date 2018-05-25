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

package com.pimenta.bestv.repository.database.dao;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.pimenta.bestv.repository.database.DatabaseHelper;
import com.pimenta.bestv.repository.entity.Movie;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by marcus on 15-04-2018.
 */
@Singleton
public class MovieDao implements BaseDao<Movie> {

    private RuntimeExceptionDao<Movie, Integer> mMovieDao;

    @Inject
    public MovieDao(DatabaseHelper databaseHelper) {
        mMovieDao = databaseHelper.getRuntimeExceptionDao(Movie.class);
    }

    @Override
    public List<Movie> queryForAll() {
        return mMovieDao.queryForAll();
    }

    @Override
    public Movie getMovieById(final int id) {
        return mMovieDao.queryForId(id);
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