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

package com.pimenta.bestv.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.pimenta.bestv.BesTV;
import com.pimenta.bestv.view.fragment.MovieBrowseFragment;
import com.pimenta.bestv.presenter.MainPresenter;

/**
 * Created by marcus on 11-02-2018.
 */
public class MainActivity extends BaseActivity<MainPresenter> {

    private static final int SPLASH_ACTIVITY_REQUEST_CODE = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.loadRecommendations();

        if (savedInstanceState == null) {
            startActivityForResult(SplashActivity.newInstance(this), SPLASH_ACTIVITY_REQUEST_CODE);
        } else {
            replaceFragment(MovieBrowseFragment.newInstance());
        }
    }

    @Override
    protected void injectPresenter() {
        BesTV.getApplicationComponent().inject(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SPLASH_ACTIVITY_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    replaceFragment(MovieBrowseFragment.newInstance());
                } else {
                    finish();
                }
                break;
        }
    }
}