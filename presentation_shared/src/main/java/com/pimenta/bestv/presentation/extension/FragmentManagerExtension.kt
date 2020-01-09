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

package com.pimenta.bestv.presentation.extension

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit

fun FragmentManager.replaceFragment(@IdRes containerViewId: Int = android.R.id.content, fragment: Fragment) {
    commit {
        replace(containerViewId, fragment)
    }
}

fun FragmentManager.addFragment(@IdRes containerViewId: Int = android.R.id.content, fragment: Fragment, tag: String) {
    commit {
        add(containerViewId, fragment)
        addToBackStack(tag)
    }
}
