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
import android.util.Pair;

import com.pimenta.bestv.repository.MediaRepository;
import com.pimenta.bestv.repository.entity.Movie;
import com.pimenta.bestv.repository.entity.TvShow;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by marcus on 12-03-2018.
 */
public class SearchPresenter extends BasePresenter<SearchContract> {

    private static final String TAG = SearchPresenter.class.getSimpleName();

    private MediaRepository mMediaRepository;
    private Disposable mDisposable;

    private String mQuery;
    private int mResultMoviePage = 0;
    private int mResultTvShowPage = 0;

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
    public void searchWorksByQuery(String query) {
        disposeSearchMovie();
        try {
            String queryEncode = URLEncoder.encode(query, "UTF-8");
            if (!queryEncode.equals(mQuery)) {
                mResultMoviePage = 0;
                mResultTvShowPage = 0;
            }
            mQuery = queryEncode;
            int resultMoviePage = mResultMoviePage + 1;
            int resultTvShowPage = mResultTvShowPage + 1;
            mDisposable = Single.zip(mMediaRepository.searchMoviesByQuery(mQuery, resultMoviePage),
                    mMediaRepository.searchTvShowsByQuery(mQuery, resultTvShowPage),
                    Pair::new)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(pair -> {
                        if (mContract != null) {
                            List<Movie> movies = null;
                            if (pair.first != null && pair.first.getPage() <= pair.first.getTotalPages()) {
                                mResultMoviePage = pair.first.getPage();
                                movies = pair.first.getWorks();
                            }
                            List<TvShow> tvShows = null;
                            if (pair.second != null && pair.second.getPage() <= pair.second.getTotalPages()) {
                                mResultTvShowPage = pair.second.getPage();
                                tvShows = pair.second.getWorks();
                            }
                            mContract.onResultLoaded(movies, tvShows);
                        }
                    }, throwable -> {
                        Log.e(TAG, "Error while searching movies by query", throwable);
                        if (mContract != null) {
                            mContract.onResultLoaded(null, null);
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