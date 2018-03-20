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

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;

import com.pimenta.bestv.service.RecommendationService;

import javax.inject.Inject;

/**
 * Created by marcus on 06-03-2018.
 */
public class BootPresenter extends AbstractPresenter<BasePresenter.Callback> {

    private static final long INITIAL_DELAY = 5000;

    private Application mApplication;
    private AlarmManager mAlarmManager;

    @Inject
    public BootPresenter(Application application, AlarmManager alarmManager) {
        super();
        mApplication = application;
        mAlarmManager = alarmManager;
    }

    /**
     * Schedules the recommendation update
     */
    public void scheduleRecommendationUpdate() {
        final PendingIntent alarmIntent = PendingIntent.getService(mApplication, 0, RecommendationService.newInstance(mApplication), 0);
        mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, INITIAL_DELAY, AlarmManager.INTERVAL_HALF_HOUR, alarmIntent);
    }
}