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

package com.pimenta.bestv.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pimenta.bestv.BesTV;
import com.pimenta.bestv.R;
import com.pimenta.bestv.fragment.base.BaseFragment;
import com.pimenta.bestv.presenter.SplashContract;
import com.pimenta.bestv.presenter.SplashPresenter;

/**
 * Created by marcus on 04-05-2018.
 */
public class SplashFragment extends BaseFragment<SplashPresenter> implements SplashContract {

    public static SplashFragment newInstance() {
        return new SplashFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.loadSplash();
    }

    @Override
    protected void injectPresenter() {
        BesTV.getApplicationComponent().inject(this);
    }

    @Override
    public void onSplashFinished() {
        finishActivity(Activity.RESULT_OK);
    }
}