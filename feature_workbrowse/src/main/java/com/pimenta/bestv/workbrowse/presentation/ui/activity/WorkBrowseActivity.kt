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

package com.pimenta.bestv.workbrowse.presentation.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.pimenta.bestv.route.search.SearchRoute
import com.pimenta.bestv.workbrowse.presentation.ui.compose.WorkBrowseWrapperScreen
import com.pimenta.bestv.workbrowse.presentation.viewmodel.WorkBrowseViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Created by marcus on 11-02-2018.
 */
class WorkBrowseActivity : ComponentActivity() {
    private val viewModel: WorkBrowseViewModel by viewModel()
    private val searchRoute: SearchRoute by inject()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WorkBrowseWrapperScreen(
                viewModel = viewModel,
                openIntent = ::openIntent,
                openSearch = { startActivity(searchRoute.buildSearchIntent()) },
                closeScreen = ::finish,
            )
        }
    }

    private fun openIntent(intent: Intent) {
        startActivity(intent)
    }
}
