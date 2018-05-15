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

import com.pimenta.bestv.connector.TmdbConnector;
import com.pimenta.bestv.model.Movie;
import com.pimenta.bestv.model.MovieList;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by marcus on 12-03-2018.
 */
public class SearchPresenter extends BasePresenter<SearchContract> {

    private int mResultPage = 0;

    private TmdbConnector mTmdbConnector;
    private Disposable mDisposable;

    @Inject
    public SearchPresenter(TmdbConnector tmdbConnector) {
        super();
        mTmdbConnector = tmdbConnector;
    }

    @Override
    public void unRegister() {
        disposeSearchMovie();
        super.unRegister();
    }

    /**
     * Searches the movies by a query
     *
     * @param query Query to search the movies
     */
    public void searchMoviesByQuery(String query) {
        disposeSearchMovie();
        mDisposable = Single.create((SingleOnSubscribe<List<Movie>>) e -> {
                    int resultPage = mResultPage + 1;
                    final MovieList movieList = mTmdbConnector.searchMoviesByQuery(query, resultPage);
                    if (movieList != null && movieList.getPage() <= movieList.getTotalPages()) {
                        mResultPage = movieList.getPage();
                        e.onSuccess(movieList.getMovies());
                    } else {
                        e.onError(new AssertionError());
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movies -> {
                    if (mContract != null) {
                        mContract.onResultLoaded(movies);
                    }
                }, throwable -> {
                    if (mContract != null) {
                        mContract.onResultLoaded(null);
                    }
                });
    }

    /**
     * Disposes the search movies.
     */
    private void disposeSearchMovie() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }
}