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

package com.pimenta.bestv.feature.splash.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pimenta.bestv.BesTV;
import com.pimenta.bestv.R;
import com.pimenta.bestv.feature.splash.presenter.SplashPresenter;
import com.pimenta.bestv.feature.base.BaseFragment;

import java.util.Set;

import javax.inject.Inject;

/**
 * Created by marcus on 04-05-2018.
 */
public class SplashFragment extends BaseFragment implements SplashPresenter.View {

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Inject
    SplashPresenter mPresenter;

    public static SplashFragment newInstance() {
        return new SplashFragment();
    }

    @Override
    public void onAttach(@Nullable Context context) {
        super.onAttach(context);
        BesTV.getApplicationComponent().inject(this);
        mPresenter.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.loadPermissions();
    }

    @Override
    public void onDetach() {
        mPresenter.unRegister();
        super.onDetach();
    }

    @Override
    public void onSplashFinished(boolean success) {
        finishActivity(success ? Activity.RESULT_OK : Activity.RESULT_CANCELED);
    }

    @Override
    public void onPermissionsLoaded(Set<String> permissions) {
        requestPermissions(permissions.toArray(new String[permissions.size()]), PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                mPresenter.hasAllPermissions();
                break;
        }
    }
}