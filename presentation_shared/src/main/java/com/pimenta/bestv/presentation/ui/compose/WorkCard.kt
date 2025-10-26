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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.model.presentation.model.WorkType

/**
 * A TV-optimized card component for displaying work (movie/TV show) items.
 *
 * This composable creates a card with:
 * - Work poster image loaded via Coil
 * - Title overlay with gradient background
 * - TV focus behavior with scale animation
 * - Standard poster aspect ratio (2:3)
 *
 * @param work The work view model containing poster URL and title
 * @param onClick Callback invoked when the card is clicked
 * @param modifier Optional modifier for customization
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun WorkCard(
    work: WorkViewModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .width(150.dp)
            .aspectRatio(2f / 3f),
        shape = CardDefaults.shape(shape = RoundedCornerShape(8.dp)),
        scale = CardDefaults.scale(
            focusedScale = 1.1f,
            pressedScale = 1.05f
        ),
        border = CardDefaults.border(
            focusedBorder = androidx.tv.material3.Border(
                border = androidx.compose.foundation.BorderStroke(
                    width = 3.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Poster image
            AsyncImage(
                model = work.posterUrl,
                contentDescription = work.title,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            // Title overlay with gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = work.title ?: work.originalTitle ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview
@Composable
private fun WorkCardPreview() {
    MaterialTheme {
        WorkCard(
            work = WorkViewModel(
                id = 1,
                title = "The Dark Knight",
                originalTitle = "The Dark Knight",
                posterUrl = null,
                type = WorkType.MOVIE,
                source = "TMDB"
            ),
            onClick = {}
        )
    }
}
