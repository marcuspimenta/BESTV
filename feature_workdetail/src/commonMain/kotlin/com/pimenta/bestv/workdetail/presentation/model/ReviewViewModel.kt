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

import com.pimenta.bestv.presentation.ui.compose.LazyContentTypes

/**
 * Created by marcus on 20-04-2020.
 */
data class ReviewViewModel(
    val id: String? = null,
    val author: String? = null,
    val content: String? = null,
    val url: String? = null,
    val fallbackKey: String? = null,
) {
    fun lazyListKey(): String =
        id?.let { "${LazyContentTypes.ID_KEY_PREFIX}$it" }
            ?: url?.let { "${LazyContentTypes.URL_KEY_PREFIX}$it" }
            ?: fallbackKey
            ?: "${LazyContentTypes.REVIEW_KEY_PREFIX}${author.orEmpty()}:${content.orEmpty()}"
}
