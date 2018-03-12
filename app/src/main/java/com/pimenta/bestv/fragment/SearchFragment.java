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

import com.pimenta.bestv.fragment.bases.BaseSearchFragment;
import com.pimenta.bestv.presenter.SearchPresenter;

/**
 * Created by marcus on 12-03-2018.
 */
public class SearchFragment extends BaseSearchFragment<SearchPresenter> {

    public static final String TAG = "SearchFragment";

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    protected SearchPresenter getPresenter() {
        return new SearchPresenter();
    }
}