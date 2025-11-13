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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

/**
 * Custom BringIntoViewSpec that aligns focused items at the top position.
 * This ensures that when an item in the LazyColumn receives focus, it scrolls to align
 * at the top of the container (after the content padding), providing a consistent
 * viewing experience across all columns in the TV app.
 */
@ExperimentalFoundationApi
private class TopAlignedBringIntoViewSpec(
    private val topPaddingPx: Float
) : BringIntoViewSpec {
    override val scrollAnimationSpec = spring<Float>()

    override fun calculateScrollDistance(
        offset: Float,
        size: Float,
        containerSize: Float
    ): Float {
        // Align the item's top edge at the container's top edge (accounting for padding)
        return offset - topPaddingPx
    }
}

/**
 * A LazyColumn that automatically aligns focused items at the top position.
 *
 * This composable wraps the standard LazyColumn with a custom BringIntoViewSpec that ensures
 * focused items always scroll to align at the top of the column (respecting content padding).
 * This is particularly useful for TV applications where consistent focus positioning
 * improves the user experience.
 *
 * @param modifier The modifier to be applied to the LazyColumn
 * @param state The state object to control or observe the list's state
 * @param contentPadding The padding around the content
 * @param verticalArrangement The arrangement of items vertically
 * @param content The content of the LazyColumn
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TopAlignedLazyColumn(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: LazyListScope.() -> Unit
) {
    val density = LocalDensity.current

    // Extract vertical top padding for the BringIntoViewSpec
    val topPaddingPx = with(density) {
        contentPadding.calculateTopPadding().toPx()
    }

    val bringIntoViewSpec = remember(topPaddingPx) {
        TopAlignedBringIntoViewSpec(topPaddingPx)
    }

    CompositionLocalProvider(LocalBringIntoViewSpec provides bringIntoViewSpec) {
        LazyColumn(
            modifier = modifier,
            state = state,
            contentPadding = contentPadding,
            verticalArrangement = verticalArrangement,
            content = content
        )
    }
}