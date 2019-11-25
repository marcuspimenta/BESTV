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

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

fun FragmentActivity.replaceFragment(fragment: Fragment) {
    supportFragmentManager.replaceFragment(fragment = fragment)
}

fun FragmentActivity.addFragment(fragment: Fragment, tag: String) {
    supportFragmentManager.addFragment(fragment = fragment, tag = tag)
}

fun FragmentActivity.popBackStack(name: String, flags: Int) {
    supportFragmentManager.popBackStackImmediate(name, flags)
}

fun FragmentActivity.finish(resultCode: Int = Activity.RESULT_OK) {
    setResult(resultCode)
    finish()
}