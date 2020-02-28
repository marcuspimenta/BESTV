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

package com.pimenta.bestv.workdetail.presentation.ui.activity

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.pimenta.bestv.presentation.extension.replaceFragment
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
import com.pimenta.bestv.workdetail.di.WorkDetailsActivityComponent
import com.pimenta.bestv.workdetail.di.WorkDetailsActivityComponentProvider
import com.pimenta.bestv.workdetail.presentation.ui.fragment.WorkDetailsFragment
import javax.inject.Inject

/**
 * Created by marcus on 11-02-2018.
 */
class WorkDetailsActivity : FragmentActivity() {

    lateinit var workDetailsActivityComponent: WorkDetailsActivityComponent

    @Inject
    lateinit var workDetailsRoute: WorkDetailsRoute

    public override fun onCreate(savedInstanceState: Bundle?) {
        workDetailsActivityComponent = (application as WorkDetailsActivityComponentProvider)
                .workDetailsActivityComponent()
                .also {
                    it.inject(this)
                }
        super.onCreate(savedInstanceState)

        when (val workViewModel = workDetailsRoute.getWorkDetail(intent)) {
            null -> finish()
            else -> replaceFragment(WorkDetailsFragment.newInstance(workViewModel))
        }
    }
}
