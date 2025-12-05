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

package com.pimenta.bestv.workdetail.presentation.mapper

import com.pimenta.bestv.workdetail.domain.model.WatchProviderDomainModel
import com.pimenta.bestv.workdetail.domain.model.WatchProvidersDomainModel
import com.pimenta.bestv.workdetail.presentation.model.WatchProviderViewModel
import com.pimenta.bestv.workdetail.presentation.model.WatchProvidersViewModel

private const val TMDB_LOGO_BASE_URL = "https://image.tmdb.org/t/p/w92%s"

fun WatchProvidersDomainModel.toViewModel() = WatchProvidersViewModel(
    tmdbLink = tmdbLink,
    providers = streaming.map { it.toViewModel() } +
            rent.map { it.toViewModel() } +
            buy.map { it.toViewModel() }
)

fun WatchProviderDomainModel.toViewModel() = WatchProviderViewModel(
    id = id,
    name = name,
    logoUrl = logoPath?.let { String.format(TMDB_LOGO_BASE_URL, it) }
)
