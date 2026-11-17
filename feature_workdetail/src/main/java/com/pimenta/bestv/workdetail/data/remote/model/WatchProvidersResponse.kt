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

package com.pimenta.bestv.workdetail.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for TMDB watch providers API.
 * Data powered by JustWatch.
 */
@Serializable
data class WatchProvidersResponse(
    @SerialName("id") val id: Int = 0,
    @SerialName("results") val results: Map<String, CountryWatchProvidersResponse>? = null,
)

/**
 * Watch providers for a specific country.
 */
@Serializable
data class CountryWatchProvidersResponse(
    @SerialName("link") val link: String? = null,
    @SerialName("flatrate") val flatrate: List<WatchProviderResponse>? = null,
    @SerialName("rent") val rent: List<WatchProviderResponse>? = null,
    @SerialName("buy") val buy: List<WatchProviderResponse>? = null,
    @SerialName("ads") val ads: List<WatchProviderResponse>? = null,
    @SerialName("free") val free: List<WatchProviderResponse>? = null,
)

/**
 * Individual watch provider information.
 */
@Serializable
data class WatchProviderResponse(
    @SerialName("display_priority") val displayPriority: Int? = null,
    @SerialName("logo_path") val logoPath: String? = null,
    @SerialName("provider_id") val providerId: Int? = null,
    @SerialName("provider_name") val providerName: String? = null,
)
