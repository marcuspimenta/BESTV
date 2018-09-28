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

package com.pimenta.bestv.feature.base;

import android.app.Service;

import com.pimenta.bestv.feature.base.BasePresenter;

import javax.inject.Inject;

/**
 * Created by marcus on 07-03-2018.
 */
public abstract class BaseService<T extends BasePresenter> extends Service implements BasePresenter.Contract {

    @Inject
    protected T mPresenter;

    @Override
    public void onCreate() {
        super.onCreate();
        injectPresenter();
        mPresenter.register(this);
    }

    @Override
    public void onDestroy() {
        mPresenter.unRegister();
        super.onDestroy();
    }

    /**
     * Injects the {@link BasePresenter}
     */
    protected abstract void injectPresenter();
}