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

package com.pimenta.bestv.workdetail.presentation.ui.compose.tv

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.material3.Card
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.StandardCardContainer
import androidx.tv.material3.Text
import coil3.compose.SubcomposeAsyncImage
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.presentation.ui.compose.tv.TVStartAlignedLazyRow
import com.pimenta.bestv.workdetail.R
import com.pimenta.bestv.workdetail.presentation.model.VideoViewModel
import com.pimenta.bestv.presentation.R as PresentationR

@Composable
fun TVVideoRow(
    videos: List<VideoViewModel>,
    onVideoClick: (VideoViewModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(vertical = BESTVTheme.scale.s100),
    ) {
        // Section title
        Text(
            text = stringResource(R.string.videos),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = BESTVTheme.colors.white,
            modifier =
                Modifier
                    .padding(horizontal = BESTVTheme.scale.s240),
        )

        // Videos list with start-aligned focus behavior
        TVStartAlignedLazyRow(
            contentPadding = PaddingValues(horizontal = BESTVTheme.scale.s240),
            horizontalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s120),
            modifier =
                Modifier
                    .padding(top = BESTVTheme.scale.s090),
        ) {
            items(
                items = videos,
                key = { it.id ?: it.hashCode() },
            ) { video ->
                TVVideoCard(
                    video = video,
                    onClick = { onVideoClick(video) },
                )
            }
        }
    }
}

@Composable
private fun TVVideoCard(
    video: VideoViewModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    StandardCardContainer(
        modifier =
            Modifier
                .width(BESTVTheme.scale.s1250),
        imageCard = { interactionSource ->
            Card(
                onClick = onClick,
                modifier =
                    modifier
                        .fillMaxWidth()
                        .height(BESTVTheme.scale.s715),
                interactionSource = interactionSource,
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize(),
                ) {
                    SubcomposeAsyncImage(
                        model = video.thumbnailUrl,
                        contentDescription = video.name,
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(BESTVTheme.scale.s040)),
                        contentScale = ContentScale.Crop,
                        loading = {
                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(
                                    modifier =
                                        Modifier
                                            .size(BESTVTheme.scale.s240),
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        },
                    )

                    Surface(
                        modifier =
                            Modifier
                                .align(Alignment.Center)
                                .size(BESTVTheme.scale.s320),
                        shape = CircleShape,
                        color = BESTVTheme.colors.imageScrim,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = stringResource(R.string.play_video),
                            tint = BESTVTheme.colors.white,
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .padding(BESTVTheme.scale.s060),
                        )
                    }
                }
            }
        },
        title = {
            Text(
                text = video.name ?: stringResource(PresentationR.string.untitled),
                style = MaterialTheme.typography.labelLarge,
                color = BESTVTheme.colors.white,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = BESTVTheme.scale.s090),
            )
        },
    )
}

@Preview
@Composable
private fun TVVideoRowPreview() {
    MaterialTheme {
        TVVideoRow(
            videos =
                listOf(
                    VideoViewModel(
                        id = "1",
                        name = "Official Trailer Official Trailer Official Trailer Official Trailer Official Trailer",
                        thumbnailUrl = null,
                        youtubeUrl = null,
                    ),
                    VideoViewModel(
                        id = "2",
                        name = "Behind the Scenes",
                        thumbnailUrl = null,
                        youtubeUrl = null,
                    ),
                    VideoViewModel(
                        id = "3",
                        name = "Interview with Director",
                        thumbnailUrl = null,
                        youtubeUrl = null,
                    ),
                ),
            onVideoClick = {},
        )
    }
}
