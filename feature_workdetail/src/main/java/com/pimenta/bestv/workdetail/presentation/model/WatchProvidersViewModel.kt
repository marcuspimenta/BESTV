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

package com.pimenta.bestv.workdetail.presentation.model

/**
 * Presentation model for watch providers.
 * Data powered by JustWatch.
 */
data class WatchProvidersViewModel(
    val tmdbLink: String?,
    val providers : List<WatchProviderViewModel>,
) {
    val hasAnyProvider: Boolean
        get() = providers.isNotEmpty()
}

/**
 * Presentation model for an individual watch provider.
 */
data class WatchProviderViewModel(
    val id: Int,
    val name: String,
    val logoUrl: String?
)
