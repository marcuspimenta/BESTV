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

package com.pimenta.bestv.feature.boot.broadcast;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pimenta.bestv.BesTV;
import com.pimenta.bestv.feature.base.BaseBroadcastReceiver;
import com.pimenta.bestv.feature.boot.presenter.BootPresenter;

/**
 * Created by marcus on 06-03-2018.
 */
public class BootBroadcastReceiver extends BaseBroadcastReceiver<BootPresenter> {

    private static final String TAG = "BootBroadcastReceiver";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        super.onReceive(context, intent);
        Log.d(TAG, "Boot initiated");
        mPresenter.scheduleRecommendationUpdate();
    }

    @Override
    protected void injectPresenter() {
        BesTV.applicationComponent.inject(this);
    }

}