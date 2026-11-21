/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pimenta.bestv.di

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import com.pimenta.bestv.castdetail.presentation.navigation.castDetailsNavigationEntryProvider
import com.pimenta.bestv.castdetail.presentation.ui.compose.CastDetailsWrapperScreen
import com.pimenta.bestv.presentation.route.navigation.navigationEntryProviderModule
import com.pimenta.bestv.search.presentation.navigation.searchNavigationEntryProvider
import com.pimenta.bestv.search.presentation.ui.compose.SearchWrapperScreen
import com.pimenta.bestv.workbrowse.presentation.navigation.workBrowseNavigationEntryProvider
import com.pimenta.bestv.workbrowse.presentation.ui.compose.WorkBrowseWrapperScreen
import com.pimenta.bestv.workdetail.presentation.model.ErrorType.FailedToOpenYouTubeVideo
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.ShowError
import com.pimenta.bestv.workdetail.presentation.navigation.workDetailsNavigationEntryProvider
import com.pimenta.bestv.workdetail.presentation.ui.compose.WorkDetailsWrapperScreen
import androidx.core.net.toUri

val navigationModule = navigationEntryProviderModule(
    workBrowseProvider = workBrowseNavigationEntryProvider { viewModel, navigate, onBack ->
        WorkBrowseWrapperScreen(viewModel, navigate, onBack)
    },
    searchProvider = searchNavigationEntryProvider { viewModel, navigate, onBack ->
        SearchWrapperScreen(viewModel, navigate, onBack)
    },
    workDetailsProvider = workDetailsNavigationEntryProvider { viewModel, navigate, _, onBack ->
        val context = LocalContext.current
        WorkDetailsWrapperScreen(
            viewModel = viewModel,
            navigate = navigate,
            openExternalUrl = { url ->
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
                } catch (_: ActivityNotFoundException) {
                    viewModel.handleEvent(ShowError(FailedToOpenYouTubeVideo))
                }
            },
            onBack = onBack,
        )
    },
    castDetailsProvider = castDetailsNavigationEntryProvider { viewModel, navigate, onBack ->
        CastDetailsWrapperScreen(viewModel, navigate, onBack)
    },
)
