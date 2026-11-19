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

package com.pimenta.bestv.di

import com.pimenta.bestv.application.AndroidDeviceCapabilities
import com.pimenta.bestv.castdetail.di.castDetailModule
import com.pimenta.bestv.castdetail.presentation.navigation.CastDetailsNavigationEntryProvider
import com.pimenta.bestv.presentation.platform.DeviceCapabilities
import com.pimenta.bestv.presentation.route.di.routeModule
import com.pimenta.bestv.presentation.route.navigation.NavigationEntryProvider
import com.pimenta.bestv.recommendation.di.recommendationModule
import com.pimenta.bestv.search.di.searchModule
import com.pimenta.bestv.search.presentation.navigation.SearchNavigationEntryProvider
import com.pimenta.bestv.workbrowse.di.workBrowseModule
import com.pimenta.bestv.workbrowse.presentation.navigation.WorkBrowseNavigationEntryProvider
import com.pimenta.bestv.workdetail.di.workDetailModule
import com.pimenta.bestv.workdetail.presentation.navigation.WorkDetailsNavigationEntryProvider
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {

    single<DeviceCapabilities> { AndroidDeviceCapabilities(androidContext()) }
    single<NavigationEntryProvider>(named("workBrowseEntryProvider")) { WorkBrowseNavigationEntryProvider() }
    single<NavigationEntryProvider>(named("searchEntryProvider")) { SearchNavigationEntryProvider() }
    single<NavigationEntryProvider>(named("workDetailsEntryProvider")) { WorkDetailsNavigationEntryProvider() }
    single<NavigationEntryProvider>(named("castDetailsEntryProvider")) { CastDetailsNavigationEntryProvider() }
    single { getAll<NavigationEntryProvider>() }

    includes(
        // Platform and shared modules
        networkModule,
        databaseDriverModule,
        databaseModule,
        dispatcherModule(Dispatchers.IO),
        presentationModule,
        routeModule,
        // Feature modules
        castDetailModule,
        recommendationModule,
        searchModule,
        workBrowseModule,
        workDetailModule,
    )
}
