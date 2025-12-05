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

package com.pimenta.bestv.workdetail.data.remote.mapper

import com.pimenta.bestv.workdetail.data.remote.model.WatchProviderResponse
import com.pimenta.bestv.workdetail.data.remote.model.WatchProvidersResponse
import com.pimenta.bestv.workdetail.domain.model.WatchProviderDomainModel
import com.pimenta.bestv.workdetail.domain.model.WatchProvidersDomainModel

/**
 * Maps WatchProvidersResponse to domain model for a specific country.
 *
 * @param countryCode ISO 3166-1 country code (e.g., "US", "BR")
 * @return Domain model or null if no providers found for the country
 */
fun WatchProvidersResponse.toDomainModel(countryCode: String): WatchProvidersDomainModel? {
    val countryProviders = results?.get(countryCode) ?: return null
    return WatchProvidersDomainModel(
        tmdbLink = countryProviders.link,
        streaming = countryProviders.flatrate?.mapNotNull { it.toDomainModel() }.orEmpty(),
        rent = countryProviders.rent?.mapNotNull { it.toDomainModel() }.orEmpty(),
        buy = countryProviders.buy?.mapNotNull { it.toDomainModel() }.orEmpty()
    )
}

/**
 * Maps WatchProviderResponse to domain model.
 *
 * @return Domain model or null if required fields are missing
 */
fun WatchProviderResponse.toDomainModel(): WatchProviderDomainModel? {
    val id = providerId ?: return null
    val name = providerName ?: return null
    return WatchProviderDomainModel(
        id = id,
        name = name,
        logoPath = logoPath,
        displayPriority = displayPriority ?: Int.MAX_VALUE
    )
}
