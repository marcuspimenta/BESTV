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
import android.util.Log;

import com.pimenta.bestv.presenter.WorkBrowsePresenter.WorkBrowseContract;
import com.pimenta.bestv.repository.MediaRepository;
import com.pimenta.bestv.repository.entity.Genre;
import com.pimenta.bestv.repository.entity.MovieGenre;
import com.pimenta.bestv.repository.entity.MovieGenreList;
import com.pimenta.bestv.repository.entity.TvShowGenre;
import com.pimenta.bestv.repository.entity.TvShowGenreList;
import com.pimenta.bestv.repository.entity.Work;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by marcus on 06-02-2018.
 */
public class WorkBrowsePresenter extends BasePresenter<WorkBrowseContract> {

    private static final String TAG = WorkBrowsePresenter.class.getSimpleName();

    private final DisplayMetrics mDisplayMetrics;
    private final MediaRepository mMediaRepository;

    @Inject
    public WorkBrowsePresenter(DisplayMetrics displayMetrics, MediaRepository mediaRepository) {
        mDisplayMetrics = displayMetrics;
        mMediaRepository = mediaRepository;
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
     * Checks if there is any {@link Work} saved as favorite
     */
    public void hasFavorite() {
        getCompositeDisposable().add(mMediaRepository.hasFavorite()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (getContract() != null) {
                        getContract().onHasFavorite(result);
                    }
                }, throwable -> {
                    Log.e(TAG, "Error while checking if has any work as favorite", throwable);
                    if (getContract() != null) {
                        getContract().onHasFavorite(false);
                    }
                }));
    }

    /**
     * Loads the {@link List<Genre>} available at TMDb
     */
    public void loadData() {
        getCompositeDisposable().add(Single.zip(mMediaRepository.getMovieGenres(),
                mMediaRepository.getTvShowGenres(),
                mMediaRepository.hasFavorite(),
                BrowserWorkInfo::new)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (getContract() != null) {
                        getContract().onDataLoaded(result.hasFavoriteMovie(),
                                result.getMovieGenreList() != null ? result.getMovieGenreList().getGenres() : null,
                                result.getTvShowGenreList() != null ? result.getTvShowGenreList().getGenres() : null);
                    }
                }, throwable -> {
                    Log.e(TAG, "Error while loading data", throwable);
                    if (getContract() != null) {
                        getContract().onDataLoaded(false, null, null);
                    }
                }));
    }

    /**
     * Wrapper class to keep the movie info
     */
    private class BrowserWorkInfo {

        private final  MovieGenreList mMovieGenreList;
        private final TvShowGenreList mTvShowGenreList;
        private final boolean mHasFavoriteMovie;

        public BrowserWorkInfo(MovieGenreList movieGenreList, TvShowGenreList tvShowGenreList, boolean hasFavoriteMovie) {
            mMovieGenreList = movieGenreList;
            mTvShowGenreList = tvShowGenreList;
            mHasFavoriteMovie = hasFavoriteMovie;
        }

        public MovieGenreList getMovieGenreList() {
            return mMovieGenreList;
        }

        public TvShowGenreList getTvShowGenreList() {
            return mTvShowGenreList;
        }

        public boolean hasFavoriteMovie() {
            return mHasFavoriteMovie;
        }
    }

    public interface WorkBrowseContract extends BasePresenter.Contract {

        void onDataLoaded(boolean hasFavoriteMovie, List<MovieGenre> movieGenres, List<TvShowGenre> tvShowGenres);

        void onHasFavorite(boolean hasFavoriteMovie);

    }
}