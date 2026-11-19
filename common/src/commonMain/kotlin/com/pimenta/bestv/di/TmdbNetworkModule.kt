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

import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module

private const val TMDB_FILTER_LANGUAGE = "en-US"
fun tmdbNetworkModule(clientFactory: Scope.() -> HttpClient) = module {
    single(named("tmdbFilterLanguage")) { TMDB_FILTER_LANGUAGE }
    single { clientFactory() }
}
