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
    return this.graphicsLayer {
        val layoutInfo = listState.layoutInfo
        val itemInfo = layoutInfo.visibleItemsInfo.firstOrNull { it.index == itemIndex }
        alpha = if (itemInfo == null) {
            1f
        } else {
            val itemTop = itemInfo.offset
            when {
                itemTop >= 0 -> 1f
                itemTop > -fadeThresholdPx -> (fadeThresholdPx + itemTop) / fadeThresholdPx
                else -> 0f
            }
        }
    }
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
    return this.graphicsLayer {
        val layoutInfo = listState.layoutInfo
        val itemInfo = layoutInfo.visibleItemsInfo.firstOrNull { it.index == itemIndex }
        alpha = if (itemInfo == null) {
            1f
        } else {
            val viewportHeight = layoutInfo.viewportEndOffset
            val itemBottom = itemInfo.offset + itemInfo.size
            when {
                itemBottom <= viewportHeight -> 1f
                itemBottom < viewportHeight + fadeThresholdPx ->
                    (viewportHeight + fadeThresholdPx - itemBottom) / fadeThresholdPx
                else -> 0f
            }
        }
    }
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
    return this.graphicsLayer {
        val layoutInfo = listState.layoutInfo
        val itemInfo = layoutInfo.visibleItemsInfo.firstOrNull { it.index == itemIndex }
        alpha = if (itemInfo == null) {
            1f
        } else {
            val itemTop = itemInfo.offset
            val viewportHeight = layoutInfo.viewportEndOffset
            val itemBottom = itemTop + itemInfo.size
            val topAlpha = when {
                itemTop >= 0 -> 1f
                itemTop > -fadeThresholdPx -> (fadeThresholdPx + itemTop) / fadeThresholdPx
                else -> 0f
            }
            val bottomAlpha = when {
                itemBottom <= viewportHeight -> 1f
                itemBottom < viewportHeight + fadeThresholdPx ->
                    (viewportHeight + fadeThresholdPx - itemBottom) / fadeThresholdPx
                else -> 0f
            }
            minOf(topAlpha, bottomAlpha)
        }
    }
}
