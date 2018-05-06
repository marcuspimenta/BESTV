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

import com.pimenta.bestv.manager.RecommendationManager;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by marcus on 04-05-2018.
 */
public class MainPresenter extends BasePresenter<BasePresenter.Contract> {

    private RecommendationManager mRecommendationManager;

    @Inject
    public MainPresenter(RecommendationManager recommendationManager) {
        super();
        mRecommendationManager = recommendationManager;
    }

    /**
     * Loads the recommendations
     */
    public void loadRecommendations() {
        mCompositeDisposable.add(Single.create((SingleOnSubscribe<Void>) e -> mRecommendationManager.loadRecommendations())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

}