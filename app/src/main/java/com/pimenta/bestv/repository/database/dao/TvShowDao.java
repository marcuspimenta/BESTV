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
import com.pimenta.bestv.repository.entity.TvShow;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by marcus on 10/07/18.
 */
@Singleton
public class TvShowDao implements BaseDao<TvShow> {

    private RuntimeExceptionDao<TvShow, Integer> mTvShowDao;

    @Inject
    public TvShowDao(DatabaseHelper databaseHelper) {
        mTvShowDao = databaseHelper.getRuntimeExceptionDao(TvShow.class);
    }

    @Override
    public List<TvShow> queryForAll() {
        return mTvShowDao.queryForAll();
    }

    @Override
    public TvShow getById(final int id) {
        return mTvShowDao.queryForId(id);
    }

    @Override
    public boolean create(final TvShow model) {
        return mTvShowDao.create(model) > 0;
    }

    @Override
    public boolean update(final TvShow model) {
        return mTvShowDao.update(model) > 0;
    }

    @Override
    public boolean delete(final TvShow model) {
        return mTvShowDao.delete(model) > 0;
    }
}