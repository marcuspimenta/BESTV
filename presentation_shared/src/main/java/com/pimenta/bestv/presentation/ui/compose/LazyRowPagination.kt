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

package com.pimenta.bestv.presentation.ui.compose

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

/**
 * Monitors a LazyListState and triggers pagination when the user scrolls near the end.
 *
 * @param listState The LazyListState to monitor
 * @param isLoadingMore Whether more items are currently being loaded
 * @param threshold Number of items from the end to trigger pagination (default: 3)
 * @param onLoadMore Callback to trigger when pagination should occur
 */
@Composable
fun LazyRowPagination(
    listState: LazyListState,
    isLoadingMore: Boolean,
    threshold: Int = 3,
    onLoadMore: () -> Unit
) {
    // Detect when we're near the end of the list
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItems = listState.layoutInfo.totalItemsCount

            // Trigger load when the last visible item is within threshold items of the end
            lastVisibleItem != null &&
            totalItems > 0 &&
            lastVisibleItem.index >= totalItems - threshold && !isLoadingMore
        }
    }

    // Trigger load more when condition is met
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onLoadMore()
        }
    }
}
