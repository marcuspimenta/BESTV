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

import com.pimenta.bestv.repository.MediaRepository;
import com.pimenta.bestv.repository.entity.Genre;
import com.pimenta.bestv.repository.entity.Movie;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by marcus on 06-02-2018.
 */
public class MovieBrowsePresenter extends BasePresenter<MovieBrowseContract> {

    private DisplayMetrics mDisplayMetrics;
    private MediaRepository mMediaRepository;

    @Inject
    public MovieBrowsePresenter(DisplayMetrics displayMetrics, MediaRepository mediaRepository) {
        super();
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
     * Checks if there is any {@link Movie} saved as favorite
     *
     * @return          {@code true} if there any {@link Movie} saved as favorite,
     *                  {@code false} otherwise
     */
    public boolean hasFavoriteMovies() {
        return mMediaRepository.hasFavoriteMovie();
    }

    /**
     * Loads the {@link List<Genre>} available at TMDb
     */
    public void loadData() {
        mCompositeDisposable.add(mMediaRepository.getMovieGenres()
                .map(genreList -> new Pair<>(mMediaRepository.hasFavoriteMovie(), genreList))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (mContract != null) {
                        mContract.onDataLoaded(result.first, result.second != null ? result.second.getGenres() : null);
                    }
                }, throwable -> {
                    if (mContract != null) {
                        mContract.onDataLoaded(false, null);
                    }
                }));
    }
}