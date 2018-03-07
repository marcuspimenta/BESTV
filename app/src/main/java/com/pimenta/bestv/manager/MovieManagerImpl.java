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

import com.pimenta.bestv.connector.TmdbConnector;
import com.pimenta.bestv.models.Movie;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by marcus on 05-03-2018.
 */
public class MovieManagerImpl implements MovieManager {

    @Inject
    TmdbConnector mTmdbConnector;

    @Inject
    public MovieManagerImpl() {
    }

    @Override
    public boolean saveFavoriteMovie(@NonNull final Movie movie) {
        return movie.create() > 0;
    }

    @Override
    public boolean deleteFavoriteMovie(@NonNull final Movie movie) {
        return movie.delete() > 0;
    }

    @Override
    public List<Movie> getFavoriteMovies() {
        return Movie.getAll();
    }
}