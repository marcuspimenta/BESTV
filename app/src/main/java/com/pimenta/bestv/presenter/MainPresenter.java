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

import com.pimenta.bestv.manager.RecommendationManager;
import com.pimenta.bestv.repository.MediaRepository;

import javax.inject.Inject;

import io.reactivex.schedulers.Schedulers;

/**
 * Created by marcus on 04-05-2018.
 */
public class MainPresenter extends BasePresenter<BasePresenter.Contract> {

    private static final String TAG = MainPresenter.class.getSimpleName();

    private final MediaRepository mMediaRepository;
    private final RecommendationManager mRecommendationManager;

    @Inject
    public MainPresenter(MediaRepository mediaRepository, RecommendationManager recommendationManager) {
        mMediaRepository = mediaRepository;
        mRecommendationManager = recommendationManager;
    }

    /**
     * Loads the recommendations
     */
    public void loadRecommendations() {
        getCompositeDisposable().add(mMediaRepository.loadWorkByType(1, MediaRepository.WorkType.POPULAR_MOVIES)
                .map(workPage -> mRecommendationManager.loadRecommendations(workPage.getWorks()))
                .subscribeOn(Schedulers.io())
                .subscribe(aBoolean -> Log.d(TAG, "Recommendations loaded")));
    }
}