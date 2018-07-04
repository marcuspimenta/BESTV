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

package com.pimenta.bestv.manager;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;

/**
 * Created by marcus on 04/07/18.
 */
public class PreferenceManagerImpl implements PreferenceManager {

    private static final String PREFERENCE_NAME = "BesTV";

    private SharedPreferences mSharedPreferences;

    @Inject
    public PreferenceManagerImpl(Application application) {
        mSharedPreferences = application.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public String getFromPersistence(final String key, final String defValue) {
        return mSharedPreferences.getString(key, defValue);
    }

    @Override
    public void applyToPersistence(final String key, final String value) {
        mSharedPreferences.edit().putString(key, value).apply();
    }

}