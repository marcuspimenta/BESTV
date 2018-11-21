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

package com.pimenta.bestv.feature.base

import android.support.v17.leanback.app.VerticalGridSupportFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

/**
 * Created by marcus on 09-02-2018.
 */
abstract class BaseVerticalGridFragment : VerticalGridSupportFragment() {

    protected lateinit var target: Fragment
    protected var targetCode: Int = 0

    /**
     * Optional target for this fragment.  This may be used, for example,
     * if this fragment is being started by another, and when done wants to
     * give a result back to the first.  The target set here is retained
     * across instances via [ FragmentManager.putFragment()][FragmentManager.putFragment].
     *
     * @param fragment    The fragment that is the target of this one.
     * @param requestCode Optional request code, for convenience if you
     * are going to call back with [.onActivityResult].
     */
    fun setTarget(fragment: Fragment, requestCode: Int) {
        target = fragment
        targetCode = requestCode
    }

    /**
     * Finishes and sets the result that your activity will return to its
     * caller.
     *
     * @param resultCode The result code to propagate back to the originating
     * activity, often RESULT_CANCELED or RESULT_OK
     */
    fun finishActivity(resultCode: Int) {
        activity?.run {
            setResult(resultCode)
            finish()
        }
    }

    /**
     * Replace an existing fragment that was added to a container.
     *
     * @param fragment The new fragment to place in the container.
     */
    protected fun replaceFragment(fragment: Fragment) {
        activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(android.R.id.content, fragment)
                ?.commit()
    }

    /**
     * Add a fragment to the activity state
     *
     * @param fragment The [Fragment] to be added. This fragment must not already be added to the activity.
     * @param tag      Optional tag name for the fragment.
     */
    protected fun addFragment(fragment: Fragment, tag: String) {
        activity?.supportFragmentManager
                ?.beginTransaction()
                ?.add(android.R.id.content, fragment)
                ?.addToBackStack(tag)?.commit()
    }

    /**
     * Pop the last fragment transition from the manager's fragment back stack. If there is nothing to pop, false is returned.
     * This function is asynchronous -- it enqueues the request to pop, but the action will not be performed until the
     * application returns to its event loop.
     *
     * @param name  The name of a previous back state to look for; if found, all states up to that state will be popped
     * @param flags Either 0 or POP_BACK_STACK_INCLUSIVE
     */
    protected fun popBackStack(name: String, flags: Int) {
        activity?.supportFragmentManager
                ?.popBackStackImmediate(name, flags)
    }
}