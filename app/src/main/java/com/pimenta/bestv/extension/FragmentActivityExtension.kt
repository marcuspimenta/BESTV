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

package com.pimenta.bestv.extension

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.transaction

fun FragmentActivity?.replaceFragment(fragment: Fragment) {
    this?.run {
        supportFragmentManager.transaction {
            replace(android.R.id.content, fragment)
        }
    }
}

fun FragmentActivity?.addFragment(fragment: Fragment, tag: String) {
    this?.run {
        supportFragmentManager.transaction {
            add(android.R.id.content, fragment)
            addToBackStack(tag)
        }
    }
}

fun FragmentActivity?.popBackStack(name: String, flags: Int) {
    this?.run {
        supportFragmentManager.popBackStackImmediate(name, flags)
    }
}

fun FragmentActivity?.finishActivity(resultCode: Int) {
    this?.run {
        setResult(resultCode)
        finish()
    }
}