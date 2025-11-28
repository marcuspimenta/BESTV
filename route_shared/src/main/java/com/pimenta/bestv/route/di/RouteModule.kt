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

package com.pimenta.bestv.route.di

import com.pimenta.bestv.route.castdetail.CastDetailsRoute
import com.pimenta.bestv.route.search.SearchRoute
import com.pimenta.bestv.route.workbrowse.WorkBrowseRoute
import com.pimenta.bestv.route.workdetail.WorkDetailsRoute
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val routeModule = module {
    singleOf(::WorkDetailsRoute)
    singleOf(::CastDetailsRoute)
    singleOf(::SearchRoute)
    singleOf(::WorkBrowseRoute)
}