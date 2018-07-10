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

import java.util.List;

/**
 * Created by marcus on 15-04-2018.
 */
public interface BaseDao<T> {

    /**
     * @see com.j256.ormlite.dao.Dao#queryForAll()
     */
    List<T> queryForAll();

    /**
     * @see com.j256.ormlite.dao.Dao#queryForId(Object)
     */
    T getById(int id);

    /**
     * @see com.j256.ormlite.dao.Dao#create(Object)
     */
    boolean create(T model);

    /**
     * @see com.j256.ormlite.dao.Dao#update(Object)
     */
    boolean update(T model);

    /**
     * @see com.j256.ormlite.dao.Dao#delete(Object)
     */
    boolean delete(T model);
}