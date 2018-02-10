/*
 * Copyright (C) 2017 The Android Open Source Project
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

package com.pimenta.bestv.presenters;

import android.util.DisplayMetrics;

import com.pimenta.bestv.BesTV;
import com.pimenta.bestv.connectors.TmdbConnector;
import com.pimenta.bestv.models.Genre;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by marcus on 06-02-2018.
 */
public class MainPresenter extends AbstractPresenter<MainCallback> {

    @Inject
    DisplayMetrics mDisplayMetrics;

    @Inject
    TmdbConnector mTmdbConnector;

    public MainPresenter() {
        BesTV.getApplicationComponent().inject(this);
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
     * Loads the {@link List<Genre>} available at TMDb
     */
    public void loadGenres() {
        mCompositeDisposable.add(Single.create((SingleOnSubscribe<List<Genre>>) e -> e.onSuccess(mTmdbConnector.getGenres()))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(genres -> {
                if (mCallback != null) {
                    mCallback.onGenresLoaded(genres);
                }
            }));
    }

}