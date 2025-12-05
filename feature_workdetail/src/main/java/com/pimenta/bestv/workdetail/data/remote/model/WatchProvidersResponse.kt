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

import com.google.gson.annotations.SerializedName

/**
 * Response model for TMDB watch providers API.
 * Data powered by JustWatch.
 */
data class WatchProvidersResponse(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("results") val results: Map<String, CountryWatchProvidersResponse>? = null
)

/**
 * Watch providers for a specific country.
 */
data class CountryWatchProvidersResponse(
    @SerializedName("link") val link: String? = null,
    @SerializedName("flatrate") val flatrate: List<WatchProviderResponse>? = null,
    @SerializedName("rent") val rent: List<WatchProviderResponse>? = null,
    @SerializedName("buy") val buy: List<WatchProviderResponse>? = null,
    @SerializedName("ads") val ads: List<WatchProviderResponse>? = null,
    @SerializedName("free") val free: List<WatchProviderResponse>? = null
)

/**
 * Individual watch provider information.
 */
data class WatchProviderResponse(
    @SerializedName("display_priority") val displayPriority: Int? = null,
    @SerializedName("logo_path") val logoPath: String? = null,
    @SerializedName("provider_id") val providerId: Int? = null,
    @SerializedName("provider_name") val providerName: String? = null
)
