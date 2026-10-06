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

package com.pimenta.bestv.presentation.ui.compose.tv

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.pimenta.bestv.presentation.theme.BESTVTheme

/**
 * Applies a fade effect to items at the top edge of a LazyColumn.
 *
 * Items gradually become transparent as they scroll out of view at the top,
 * creating a smooth visual transition and allowing the background to show through.
 *
 * @param listState The LazyListState to monitor for item positions
 * @param itemIndex The index of this item in the list
 * @param fadeThreshold The distance from the top edge where fading begins. Default is 200dp.
 * @return A Modifier that applies the appropriate alpha based on the item's position
 */
@Composable
fun Modifier.fadeAtTopEdge(
    listState: LazyListState,
    itemIndex: Int,
    fadeThreshold: Dp = BESTVTheme.scale.s1000,
): Modifier {
    val fadeThresholdPx = with(LocalDensity.current) { fadeThreshold.toPx() }
    val itemInfo = listState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == itemIndex }

    val alpha =
        if (itemInfo != null) {
            val itemTop = itemInfo.offset

            when {
                itemTop >= 0 -> 1f // Item is fully visible
                itemTop > -fadeThresholdPx -> {
                    // Item is leaving the top, fade out proportionally
                    (fadeThresholdPx + itemTop) / fadeThresholdPx
                }
                else -> 0f // Item is completely off screen
            }
        } else {
            1f // Item not visible, default to fully opaque
        }

    return this.graphicsLayer { this.alpha = alpha }
}

/**
 * Applies a fade effect to items at the bottom edge of a LazyColumn.
 *
 * Items gradually become transparent as they scroll out of view at the bottom,
 * creating a smooth visual transition and allowing the background to show through.
 *
 * @param listState The LazyListState to monitor for item positions
 * @param itemIndex The index of this item in the list
 * @param fadeThreshold The distance from the bottom edge where fading begins. Default is 200dp.
 * @return A Modifier that applies the appropriate alpha based on the item's position
 */
@Composable
fun Modifier.fadeAtBottomEdge(
    listState: LazyListState,
    itemIndex: Int,
    fadeThreshold: Dp = BESTVTheme.scale.s1000,
): Modifier {
    val fadeThresholdPx = with(LocalDensity.current) { fadeThreshold.toPx() }
    val itemInfo = listState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == itemIndex }

    val alpha =
        if (itemInfo != null) {
            val viewportHeight = listState.layoutInfo.viewportEndOffset
            val itemBottom = itemInfo.offset + itemInfo.size

            when {
                itemBottom <= viewportHeight -> 1f // Item is fully visible
                itemBottom < viewportHeight + fadeThresholdPx -> {
                    // Item is leaving the bottom, fade out proportionally
                    (viewportHeight + fadeThresholdPx - itemBottom) / fadeThresholdPx
                }
                else -> 0f // Item is completely off screen
            }
        } else {
            1f // Item not visible, default to fully opaque
        }

    return this.graphicsLayer { this.alpha = alpha }
}

/**
 * Applies a fade effect to items at both top and bottom edges of a LazyColumn.
 *
 * Items gradually become transparent as they scroll out of view at either edge,
 * creating a smooth visual transition and allowing the background to show through.
 *
 * @param listState The LazyListState to monitor for item positions
 * @param itemIndex The index of this item in the list
 * @param fadeThreshold The distance from each edge where fading begins. Default is 200dp.
 * @return A Modifier that applies the appropriate alpha based on the item's position
 */
@Composable
fun Modifier.fadeAtBothEdges(
    listState: LazyListState,
    itemIndex: Int,
    fadeThreshold: Dp = BESTVTheme.scale.s1000,
): Modifier {
    val fadeThresholdPx = with(LocalDensity.current) { fadeThreshold.toPx() }
    val itemInfo = listState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == itemIndex }

    val alpha =
        if (itemInfo != null) {
            val itemTop = itemInfo.offset
            val viewportHeight = listState.layoutInfo.viewportEndOffset
            val itemBottom = itemInfo.offset + itemInfo.size

            // Calculate alpha for top edge
            val topAlpha =
                when {
                    itemTop >= 0 -> 1f
                    itemTop > -fadeThresholdPx -> (fadeThresholdPx + itemTop) / fadeThresholdPx
                    else -> 0f
                }

            // Calculate alpha for bottom edge
            val bottomAlpha =
                when {
                    itemBottom <= viewportHeight -> 1f
                    itemBottom < viewportHeight + fadeThresholdPx -> {
                        (viewportHeight + fadeThresholdPx - itemBottom) / fadeThresholdPx
                    }
                    else -> 0f
                }

            // Use the minimum of both alphas (most transparent)
            minOf(topAlpha, bottomAlpha)
        } else {
            1f // Item not visible, default to fully opaque
        }

    return this.graphicsLayer { this.alpha = alpha }
}
