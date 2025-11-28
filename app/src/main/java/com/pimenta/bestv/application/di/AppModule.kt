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

package com.pimenta.bestv.application.di

import com.pimenta.bestv.castdetail.di.castDetailModule
import com.pimenta.bestv.data.di.databaseModule
import com.pimenta.bestv.data.di.networkModule
import com.pimenta.bestv.presentation.di.presentationModule
import com.pimenta.bestv.recommendation.di.recommendationModule
import com.pimenta.bestv.route.di.routeModule
import com.pimenta.bestv.search.di.searchModule
import com.pimenta.bestv.workbrowse.di.workBrowseModule
import com.pimenta.bestv.workdetail.di.workDetailModule
import org.koin.dsl.module

val appModule = module {
    includes(
        // Shared modules
        networkModule,
        databaseModule,
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