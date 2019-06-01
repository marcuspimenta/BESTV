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
package com.pimenta.bestv.feature.error

import android.app.Activity
import android.os.Bundle
import androidx.leanback.app.ErrorSupportFragment
import com.pimenta.bestv.R

/**
 * Created by marcus on 11-02-2018.
 */
class ErrorFragment : ErrorSupportFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageDrawable = resources.getDrawable(R.drawable.lb_ic_sad_cloud, activity!!.theme)
        message = resources.getString(R.string.error_fragment_message)
        setDefaultBackground(true)

        buttonText = resources.getString(R.string.error_fragment_button)
        setButtonClickListener {
            targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
        }
    }

    companion object {

        const val TAG = "ErrorFragment"

        fun newInstance() = ErrorFragment()
    }
}