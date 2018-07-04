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
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by marcus on 04/07/18.
 */
public class PermissionManagerImpl implements PermissionManager {

    private Application mApplication;
    private Map<String, Boolean> mPermissions;

    @Inject
    public PermissionManagerImpl(Application application, Map<String, Boolean> permissions) {
        mApplication = application;
        mPermissions = permissions;
    }

    @Override
    public Single<Boolean> hasAllPermissions() {
        return Single.create(emitter -> {
            boolean hasAllPermissions = true;
            for (final String permission : mPermissions.keySet()) {
                hasAllPermissions &= ContextCompat.checkSelfPermission(mApplication, permission) == PackageManager.PERMISSION_GRANTED;
                mPermissions.put(permission, hasAllPermissions);
            }
            emitter.onSuccess(hasAllPermissions);
        });
    }

    @Override
    public Set<String> getPermissions() {
        return mPermissions.keySet();
    }
}