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

import com.pimenta.bestv.manager.PermissionManager;
import com.pimenta.bestv.presenter.SplashPresenter.SplashContract;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by marcus on 04-05-2018.
 */
public class SplashPresenter extends BasePresenter<SplashContract> {

    private static final String TAG = SplashPresenter.class.getSimpleName();

    private static final int SPLASH_TIME_LOAD_SECONDS = 3;

    private final PermissionManager mPermissionManager;

    @Inject
    public SplashPresenter(PermissionManager permissionManager) {
        mPermissionManager = permissionManager;
    }

    /**
     * Loads all permissions
     */
    public void loadPermissions() {
        getCompositeDisposable().add(mPermissionManager.hasAllPermissions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .delay(SPLASH_TIME_LOAD_SECONDS, TimeUnit.SECONDS)
                .subscribe(aBoolean -> {
                    if (getContract() != null) {
                        if (aBoolean) {
                            getContract().onSplashFinished(true);
                        } else {
                            getContract().onPermissionsLoaded(mPermissionManager.getPermissions());
                        }
                    }
                }, throwable -> {
                    Log.e(TAG, "Error while loading permissions", throwable);
                    if (getContract() != null) {
                        getContract().onSplashFinished(false);
                    }
                }));
    }

    /**
     * Verifies if has all permissions
     */
    public void hasAllPermissions() {
        getCompositeDisposable().add(mPermissionManager.hasAllPermissions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (getContract() != null) {
                        getContract().onSplashFinished(aBoolean);
                    }
                }, throwable -> {
                    Log.e(TAG, "Error while checking if has all permissions", throwable);
                    if (getContract() != null) {
                        getContract().onSplashFinished(false);
                    }
                }));
    }

    public interface SplashContract extends BasePresenter.Contract {

        void onSplashFinished(boolean success);

        void onPermissionsLoaded(Set<String> permissions);

    }
}