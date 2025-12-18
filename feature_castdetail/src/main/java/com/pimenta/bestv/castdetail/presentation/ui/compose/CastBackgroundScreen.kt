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

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.material3.MaterialTheme
import coil3.compose.AsyncImage

@Composable
fun CastBackgroundScreen(
    backdropUrl: String?,
    modifier: Modifier = Modifier,
    animationDuration: Int = 1200,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Crossfade(
            targetState = backdropUrl,
            label = "background_transition",
            animationSpec = tween(durationMillis = animationDuration),
            modifier = Modifier
                .fillMaxWidth(0.60f)
                .align(Alignment.TopEnd)
        ) { url ->
            url?.let {
                AsyncImage(
                    model = it,
                    contentDescription = null,
                    modifier = Modifier
                        .aspectRatio(2f / 3f),
                    contentScale = ContentScale.Fit,
                )
            }
        }

        // Left edge fade gradient
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.20f)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Black,
                            Color.Transparent,
                        )
                    )
                )
                .align(Alignment.Center)
        )

        // Bottom edge fade gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.20f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black,
                        )
                    )
                )
                .align(Alignment.Center)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )
    }
}

@Preview
@Composable
private fun BackgroundScreenPreview() {
    MaterialTheme {
        BackgroundScreen(
            backdropUrl = "https://example.com/backdrop.jpg"
        )
    }
}
