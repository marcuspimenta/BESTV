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

package com.pimenta.bestv.fragments.bases;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BrowseFragment;
import android.view.View;

import com.pimenta.bestv.presenters.BasePresenter;

/**
 * Created by marcus on 06-02-2018.
 */
public abstract class BaseBrowseFragment<T extends BasePresenter> extends BrowseFragment implements BasePresenter.Callback {

    protected final T mPresenter = getPresenter();

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.register(this);
    }

    @Override
    public void onDestroyView() {
        mPresenter.unRegister();
        super.onDestroyView();
    }

    /**
     * Replace an existing fragment that was added to a container.
     *
     * @param fragment The new fragment to place in the container.
     */
    protected void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        if (fragmentManager != null) {
            fragmentManager.beginTransaction()
                    .replace(android.R.id.content, fragment)
                    .commit();
        }
    }

    /**
     * Add a fragment to the activity state
     *
     * @param fragment The {@link Fragment} to be added. This fragment must not already be added to the activity.
     * @param tag      Optional tag name for the fragment.
     */
    protected void addFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        if (fragmentManager != null) {
            fragmentManager.beginTransaction()
                    .add(android.R.id.content, fragment)
                    .addToBackStack(tag)
                    .commit();
        }
    }

    /**
     * Pop the last fragment transition from the manager's fragment back stack. If there is nothing to pop, false is returned.
     * This function is asynchronous -- it enqueues the request to pop, but the action will not be performed until the
     * application returns to its event loop.
     *
     * @param name  The name of a previous back state to look for; if found, all states up to that state will be popped
     * @param flags Either 0 or POP_BACK_STACK_INCLUSIVE
     */
    protected void popBackStack(String name, int flags) {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        if (fragmentManager != null) {
            fragmentManager.popBackStackImmediate(name, flags);
        }
    }

    protected abstract T getPresenter();

}