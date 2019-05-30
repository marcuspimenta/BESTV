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

package com.pimenta.bestv.common.presentation.ui.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.leanback.app.ErrorSupportFragment

/**
 * Created by marcus on 11-02-2018.
 */
abstract class BaseErrorFragment : ErrorSupportFragment() {

    protected var target: Fragment? = null
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
}