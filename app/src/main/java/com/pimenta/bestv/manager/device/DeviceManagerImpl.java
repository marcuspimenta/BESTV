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

package com.pimenta.bestv.manager.device;

import com.pimenta.bestv.data.entity.IpInfo;
import com.pimenta.bestv.data.repository.remote.api.ip.InfoApi;

import java.io.IOException;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by marcus on 11-02-2018.
 */
public class DeviceManagerImpl implements DeviceManager {

    private IpInfo mIpInfo;
    private InfoApi mInfoApi;

    @Inject
    public DeviceManagerImpl(InfoApi infoApi) {
        mInfoApi = infoApi;
    }

    @Override
    public String getCountryCode() {
        try {
            if (mInfoApi == null) {
                mIpInfo = mInfoApi.getIpInfo().execute().body();
            }
        } catch (IOException exception) {
            Timber.e(exception, "Failed to get the ip info");
        }

        return mIpInfo != null ? mIpInfo.getCountryCode() : null;
    }
}