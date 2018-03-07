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

package com.pimenta.bestv.service;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.pimenta.bestv.presenter.RecommendationCallbak;
import com.pimenta.bestv.presenter.RecommendationPresenter;

/**
 * Created by marcus on 07-03-2018.
 */
public class RecommendationService extends BaseService<RecommendationPresenter> implements RecommendationCallbak {

    public static Intent newInstance(Context context) {
        return new Intent(context, RecommendationService.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPresenter.loadRecommendations();
    }

    @Override
    public void onLoadRecommendationFinished() {
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    protected RecommendationPresenter getPresenter() {
        return new RecommendationPresenter();
    }
}