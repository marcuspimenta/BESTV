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

package com.pimenta.bestv.workdetail.presentation.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.pimenta.bestv.presentation.ui.compose.StartAlignedLazyRow
import com.pimenta.bestv.workdetail.presentation.model.VideoViewModel

@Composable
fun VideoRow(
    videos: List<VideoViewModel>,
    onVideoClick: (VideoViewModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(vertical = 20.dp)
    ) {
        // Section title
        Text(
            text = "Videos",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 48.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Videos list with start-aligned focus behavior
        StartAlignedLazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 48.dp)
        ) {
            items(
                items = videos,
                key = { it.id ?: it.hashCode() }
            ) { video ->
                VideoCard(
                    video = video,
                    onClick = { onVideoClick(video) }
                )
            }
        }
    }
}

@Composable
private fun VideoCard(
    video: VideoViewModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column (
        modifier = modifier.width(280.dp)
    ){
        Card(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            scale = CardDefaults.scale(focusedScale = 1.05f),
            border = CardDefaults.border(
                focusedBorder = Border(
                    border = androidx.compose.foundation.BorderStroke(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Thumbnail with play icon overlay
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AsyncImage(
                        model = video.thumbnailUrl,
                        contentDescription = video.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Play icon overlay
                    Surface(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(64.dp),
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.4f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Play video",
                            tint = Color.White,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                        )
                    }
                }
            }
        }
        Text(
            text = video.name ?: "Untitled",
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

@Preview
@Composable
private fun VideoRowPreview() {
    MaterialTheme {
        VideoRow(
            videos = listOf(
                VideoViewModel(
                    id = "1",
                    name = "Official Trailer Official Trailer Official Trailer Official Trailer Official Trailer",
                    thumbnailUrl = null,
                    youtubeUrl = null
                ),
                VideoViewModel(
                    id = "2",
                    name = "Behind the Scenes",
                    thumbnailUrl = null,
                    youtubeUrl = null
                ),
                VideoViewModel(
                    id = "3",
                    name = "Interview with Director",
                    thumbnailUrl = null,
                    youtubeUrl = null
                )
            ),
            onVideoClick = {}
        )
    }
}
