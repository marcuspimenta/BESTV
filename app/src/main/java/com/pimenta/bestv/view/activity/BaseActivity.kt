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

package com.pimenta.bestv.view.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.pimenta.bestv.presenter.BasePresenter
import javax.inject.Inject

/**
 * Created by marcus on 14-02-2018.
 */
abstract class BaseActivity<T : BasePresenter<BasePresenter.Contract>> : FragmentActivity(), BasePresenter.Contract {

    @Inject protected lateinit var presenter: T

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectPresenter()
        presenter.register(this)
    }

    override fun onDestroy() {
        presenter.unRegister()
        super.onDestroy()
    }

    /**
     * Replace an existing fragment that was added to a container.
     *
     * @param fragment The new fragment to place in the container.
     */
    protected fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        fragmentManager?.beginTransaction()?.replace(android.R.id.content, fragment)?.commit()
    }

    /**
     * Add a fragment to the activity state
     *
     * @param fragment The [Fragment] to be added. This fragment must not already be added to the activity.
     * @param tag      Optional tag name for the fragment.
     */
    protected fun addFragment(fragment: Fragment, tag: String) {
        val fragmentManager = supportFragmentManager
        fragmentManager?.beginTransaction()?.add(android.R.id.content, fragment)?.addToBackStack(tag)?.commit()
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
        val fragmentManager = supportFragmentManager
        fragmentManager?.popBackStackImmediate(name, flags)
    }

    /**
     * Injects the [BasePresenter]
     */
    protected abstract fun injectPresenter()

}