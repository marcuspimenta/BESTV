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

package com.pimenta.bestv.fragment.bases;

import android.content.Context;
import android.content.Intent;
import android.support.v17.leanback.app.VerticalGridSupportFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.pimenta.bestv.presenter.BasePresenter;

import javax.inject.Inject;

/**
 * Created by marcus on 09-02-2018.
 */
public abstract class BaseVerticalGridFragment<T extends BasePresenter> extends VerticalGridSupportFragment implements BasePresenter.Callback {

    protected Fragment mTargetFragment;
    protected int mTargetRequestCode;

    @Inject
    protected T mPresenter;

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        injectPresenter();
        mPresenter.register(this);
    }

    @Override
    public void onDetach() {
        mPresenter.unRegister();
        super.onDetach();
    }

    /**
     * Optional target for this fragment.  This may be used, for example,
     * if this fragment is being started by another, and when done wants to
     * give a result back to the first.  The target set here is retained
     * across instances via {@link FragmentManager#putFragment
     * FragmentManager.putFragment()}.
     *
     * @param fragment    The fragment that is the target of this one.
     * @param requestCode Optional request code, for convenience if you
     *                    are going to call back with {@link #onActivityResult(int, int, Intent)}.
     */
    public void setTarget(final Fragment fragment, final int requestCode) {
        mTargetFragment = fragment;
        mTargetRequestCode = requestCode;
    }

    /**
     * Replace an existing fragment that was added to a container.
     *
     * @param fragment The new fragment to place in the container.
     */
    protected void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
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
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
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
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if (fragmentManager != null) {
            fragmentManager.popBackStackImmediate(name, flags);
        }
    }

    /**
     * Injects the {@link BasePresenter}
     */
    protected abstract void injectPresenter();

}