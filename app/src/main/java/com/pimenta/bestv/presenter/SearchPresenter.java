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

import android.util.Log;

import com.pimenta.bestv.repository.MediaRepository;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by marcus on 12-03-2018.
 */
public class SearchPresenter extends BasePresenter<SearchContract> {

    private static final String TAG = SearchPresenter.class.getSimpleName();

    private int mResultPage = 0;

    private MediaRepository mMediaRepository;
    private Disposable mDisposable;

    @Inject
    public SearchPresenter(MediaRepository mediaRepository) {
        super();
        mMediaRepository = mediaRepository;
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
        try {
            String encodeQuery = URLEncoder.encode(query, "UTF-8");
            int resultPage = mResultPage + 1;
            mDisposable = mMediaRepository.searchMoviesByQuery(encodeQuery, resultPage)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(moviePage -> {
                        if (mContract != null) {
                            if (moviePage != null && moviePage.getPage() <= moviePage.getTotalPages()) {
                                mResultPage = moviePage.getPage();
                                mContract.onResultLoaded(moviePage.getMovies());
                            } else {
                                mContract.onResultLoaded(null);
                            }
                        }
                    }, throwable -> {
                        if (mContract != null) {
                            mContract.onResultLoaded(null);
                        }
                    });
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Error while encoding the query", e);
        }
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