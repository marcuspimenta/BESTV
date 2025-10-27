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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import coil.compose.AsyncImage
import com.pimenta.bestv.model.presentation.model.CastViewModel

/**
 * A focusable card displaying a cast member's profile photo.
 *
 * This composable creates a TV-optimized circular card:
 * - Circular profile photo with AsyncImage
 * - Focus handling with D-pad navigation
 * - Visual feedback on focus (border and scale animation)
 * - Click handling for navigation
 *
 * @param cast The cast view model containing profile information
 * @param onClick Callback invoked when the card is clicked
 * @param modifier Optional modifier for customization
 * @param size The diameter of the circular card (default 200.dp)
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun CastCard(
    cast: CastViewModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp
) {
    Surface(
        onClick = onClick,
        modifier = modifier.size(size),
        shape = ClickableSurfaceDefaults.shape(shape = CircleShape),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.1f),
        border = ClickableSurfaceDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(3.dp, MaterialTheme.colorScheme.primary)
            )
        )
    ) {
        AsyncImage(
            model = cast.thumbnailUrl,
            contentDescription = cast.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Preview
@Composable
private fun CastCardPreview() {
    MaterialTheme {
        CastCard(
            cast = CastViewModel(
                id = 1,
                name = "Christian Bale",
                character = "Bruce Wayne",
                birthday = "January 30, 1974",
                deathDay = null,
                biography = "Christian Charles Philip Bale is an English actor.",
                thumbnailUrl = null,
                source = "TMDB"
            ),
            onClick = {}
        )
    }
}
