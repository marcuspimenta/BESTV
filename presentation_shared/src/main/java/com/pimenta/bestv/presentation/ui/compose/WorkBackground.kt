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

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.material3.MaterialTheme
import coil.compose.AsyncImage

@Composable
fun WorkBackground(
    backdropUrl: String?,
    modifier: Modifier = Modifier,
    targetAlpha: Float = 0.3f,
    animationDuration: Int = 2500
) {
    var isBackgroundVisible by remember { mutableStateOf(false) }

    LaunchedEffect(backdropUrl) {
        isBackgroundVisible = backdropUrl != null
    }

    val backgroundAlpha by animateFloatAsState(
        targetValue = if (isBackgroundVisible) targetAlpha else 0f,
        animationSpec = tween(durationMillis = animationDuration),
        label = "backgroundFadeIn"
    )

    backdropUrl?.let {
        AsyncImage(
            model = it,
            contentDescription = null,
            modifier = modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = backgroundAlpha
        )
    }
}

@Preview
@Composable
private fun WorkBackgroundPreview() {
    MaterialTheme {
        WorkBackground(
            backdropUrl = "https://example.com/backdrop.jpg"
        )
    }
}
