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

package com.pimenta.bestv.castdetail.presentation.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.tv.material3.MaterialTheme
import com.pimenta.bestv.castdetail.presentation.ui.compose.CastDetailsScreen
import com.pimenta.bestv.castdetail.presentation.viewmodel.CastDetailsViewModel
import com.pimenta.bestv.route.castdetail.getCastDeepLink
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * Created by marcus on 04-04-2018.
 */
class CastDetailsActivity : ComponentActivity() {

    private val viewModel: CastDetailsViewModel by viewModel {
        parametersOf(
            intent.getCastDeepLink()
                ?: throw IllegalStateException("CastViewModel not found in intent")
        )
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                CastDetailsScreen(
                    viewModel = viewModel,
                    openIntent = { openIntent(it) },
                )
            }
        }
    }

    private fun openIntent(intent: Intent) {
        startActivity(intent)
    }
}
