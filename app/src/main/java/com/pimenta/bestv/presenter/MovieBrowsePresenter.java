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

package com.pimenta.bestv.presenter;

import android.util.DisplayMetrics;
import android.util.Pair;

import com.pimenta.bestv.connector.TmdbConnector;
import com.pimenta.bestv.manager.MovieManager;
import com.pimenta.bestv.model.Genre;
import com.pimenta.bestv.model.Movie;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by marcus on 06-02-2018.
 */
public class MovieBrowsePresenter extends BasePresenter<MovieBrowseContract> {

    private DisplayMetrics mDisplayMetrics;
    private MovieManager mMovieManager;
    private TmdbConnector mTmdbConnector;

    @Inject
    public MovieBrowsePresenter(DisplayMetrics displayMetrics, MovieManager movieManager, TmdbConnector tmdbConnector) {
        super();
        mDisplayMetrics = displayMetrics;
        mMovieManager = movieManager;
        mTmdbConnector = tmdbConnector;
    }

    /**
     * Gets the {@link DisplayMetrics} instance
     *
     * @return {@link DisplayMetrics}
     */
    public DisplayMetrics getDisplayMetrics() {
        return mDisplayMetrics;
    }

    /**
     * Checks if there is any {@link Movie} saved as favorite
     *
     * @return          {@code true} if there any {@link Movie} saved as favorite,
     *                  {@code false} otherwise
     */
    public boolean hasFavoriteMovies() {
        return mMovieManager.hasFavoriteMovie();
    }

    /**
     * Loads the {@link List<Genre>} available at TMDb
     */
    public void loadData() {
        mCompositeDisposable.add(Maybe.create((MaybeOnSubscribe<Pair<Boolean, List<Genre>>>) e -> {
                    final boolean hasFavoriteMovie = mMovieManager.hasFavoriteMovie();
                    final List<Genre> genres = mTmdbConnector.getGenres();
                    if (genres != null) {
                        e.onSuccess(new Pair<>(hasFavoriteMovie, genres));
                    } else {
                        e.onComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (mContract != null) {
                        mContract.onDataLoaded(result.first, result.second);
                    }
                }, throwable -> {
                    if (mContract != null) {
                        mContract.onDataLoaded(false, null);
                    }
                }));
    }
}