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

package com.pimenta.bestv.feature.workdetail.presentation.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.pimenta.bestv.common.extension.replaceFragment
import com.pimenta.bestv.common.presentation.model.WorkViewModel
import com.pimenta.bestv.feature.workdetail.di.WorkDetailsActivityComponent
import com.pimenta.bestv.feature.workdetail.presentation.processor.WorkProcessor
import com.pimenta.bestv.feature.workdetail.presentation.ui.fragment.WorkDetailsFragment
import javax.inject.Inject

/**
 * Created by marcus on 11-02-2018.
 */
class WorkDetailsActivity : FragmentActivity() {

    @Inject
    lateinit var workProcessor: WorkProcessor

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WorkDetailsActivityComponent.build().inject(this)

        when (val workViewModel = workProcessor(intent)) {
            null -> finish()
            else -> replaceFragment(WorkDetailsFragment.newInstance(workViewModel))
        }
    }

    companion object {

        const val WORK = "WORK"

        fun newInstance(context: Context, workViewModel: WorkViewModel) =
                Intent(context, WorkDetailsActivity::class.java).apply {
                    putExtra(WORK, workViewModel)
                }
    }
}