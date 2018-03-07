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
import android.util.Log;

import com.google.gson.Gson;
import com.pimenta.bestv.R;
import com.pimenta.bestv.api.ip.Ip;
import com.pimenta.bestv.models.IpInfo;

import java.io.IOException;
import java.util.concurrent.Executor;

import javax.inject.Inject;

/**
 * Created by marcus on 11-02-2018.
 */
public class DeviceManagerImpl implements DeviceManager {

    private static final String TAG = "DeviceManagerImpl";

    private Ip mIp;
    private IpInfo mIpInfo;

    @Inject
    public DeviceManagerImpl(Application application, Gson gson, Executor threadPool) {
        mIp = new Ip(application.getString(R.string.ip_base_url_api), gson, threadPool);
    }

    @Override
    public String getCountryCode() {
        try {
            if (mIpInfo == null) {
                mIpInfo = mIp.getInfoApi().getIpInfo().execute().body();
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get the ip info", e);
        }

        return mIpInfo != null ? mIpInfo.getCountryCode() : null;
    }
}