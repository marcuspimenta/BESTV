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

import com.pimenta.bestv.connector.TmdbConnector;
import com.pimenta.bestv.manager.MovieManager;
import com.pimenta.bestv.manager.RecommendationManager;
import com.pimenta.bestv.model.Genre;
import com.pimenta.bestv.model.Movie;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by marcus on 06-02-2018.
 */
public class MovieBrowsePresenter extends BasePresenter<MovieBrowseContract> {

    private DisplayMetrics mDisplayMetrics;
    private RecommendationManager mRecommendationManager;
    private MovieManager mMovieManager;
    private TmdbConnector mTmdbConnector;

    @Inject
    public MovieBrowsePresenter(DisplayMetrics displayMetrics, RecommendationManager recommendationManager, MovieManager movieManager,
            TmdbConnector tmdbConnector) {
        super();
        mDisplayMetrics = displayMetrics;
        mRecommendationManager = recommendationManager;
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
     * Loads the recommendations
     */
    public void loadRecommendations() {
        mCompositeDisposable.add(Single.create((SingleOnSubscribe<Void>) e -> mRecommendationManager.loadRecommendations())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

    /**
     * Loads the {@link List<Genre>} available at TMDb
     */
    public void loadData() {
        mCompositeDisposable.add(Single.create((SingleOnSubscribe<Data>) e -> {
                    final boolean hasFavoriteMovie = mMovieManager.hasFavoriteMovie();
                    final List<Genre> genres = mTmdbConnector.getGenres();
                    final Data data = new Data(hasFavoriteMovie, genres);
                    if (genres != null) {
                        e.onSuccess(data);
                    } else {
                        e.onError(new AssertionError());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    if (mContract != null) {
                        mContract.onDataLoaded(data.hasFavoriteMovie(), data.getGenres());
                    }
                }, throwable -> {
                    if (mContract != null) {
                        mContract.onDataLoaded(false, null);
                    }
                }));
    }

    /**
     * Wrapper to keep the data to be handle by callback
     */
    private class Data {

        private boolean mHasFavoriteMovie;
        private List<Genre> mGenres;

        public Data(final boolean hasFavoriteMovie, final List<Genre> genres) {
            mHasFavoriteMovie = hasFavoriteMovie;
            mGenres = genres;
        }

        public boolean hasFavoriteMovie() {
            return mHasFavoriteMovie;
        }

        public List<Genre> getGenres() {
            return mGenres;
        }
    }
}