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

import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.BringIntoViewSpec
import androidx.compose.foundation.gestures.LocalBringIntoViewSpec
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Custom BringIntoViewSpec that aligns focused items at the start position.
 * This ensures that when an item in the LazyRow receives focus, it scrolls to align
 * at the start of the container (after the content padding), providing a consistent
 * viewing experience across all rows in the TV app.
 */
@ExperimentalFoundationApi
private class StartAlignedBringIntoViewSpec(
    private val startPaddingPx: Float
) : BringIntoViewSpec {
    override val scrollAnimationSpec = spring<Float>()

    override fun calculateScrollDistance(
        offset: Float,
        size: Float,
        containerSize: Float
    ): Float {
        // Align the item's start edge at the container's start edge (accounting for padding)
        return offset - startPaddingPx
    }
}

/**
 * A LazyRow that automatically aligns focused items at the start position.
 *
 * This composable wraps the standard LazyRow with a custom BringIntoViewSpec that ensures
 * focused items always scroll to align at the start of the row (respecting content padding).
 * This is particularly useful for TV applications where consistent focus positioning
 * improves the user experience.
 *
 * @param modifier The modifier to be applied to the LazyRow
 * @param state The state object to control or observe the list's state
 * @param contentPadding The padding around the content
 * @param horizontalArrangement The arrangement of items horizontally
 * @param content The content of the LazyRow
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StartAlignedLazyRow(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: LazyListScope.() -> Unit
) {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    // Extract horizontal start padding for the BringIntoViewSpec
    val startPaddingPx = with(density) {
        contentPadding.calculateLeftPadding(layoutDirection).toPx()
    }

    val bringIntoViewSpec = remember(startPaddingPx) {
        StartAlignedBringIntoViewSpec(startPaddingPx)
    }

    CompositionLocalProvider(LocalBringIntoViewSpec provides bringIntoViewSpec) {
        LazyRow(
            modifier = modifier,
            state = state,
            contentPadding = contentPadding,
            horizontalArrangement = horizontalArrangement,
            content = content
        )
    }
}
