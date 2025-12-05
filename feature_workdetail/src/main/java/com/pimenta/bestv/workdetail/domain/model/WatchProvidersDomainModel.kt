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

package com.pimenta.bestv.workdetail.domain.model

/**
 * Domain model for watch providers.
 * Data powered by JustWatch.
 */
data class WatchProvidersDomainModel(
    val tmdbLink: String?,
    val streaming: List<WatchProviderDomainModel>,
    val rent: List<WatchProviderDomainModel>,
    val buy: List<WatchProviderDomainModel>
) {
    val hasAnyProvider: Boolean
        get() = streaming.isNotEmpty() || rent.isNotEmpty() || buy.isNotEmpty()
}

/**
 * Domain model for an individual watch provider.
 */
data class WatchProviderDomainModel(
    val id: Int,
    val name: String,
    val logoPath: String?,
    val displayPriority: Int
)
