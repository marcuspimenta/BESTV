/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workdetail.presentation.ui.compose.mobile

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.AsyncImage
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.presentation.ui.compose.LazyContentTypes
import com.pimenta.bestv.presentation.ui.compose.mobile.MobileSectionTitle
import com.pimenta.bestv.workdetail.R
import com.pimenta.bestv.workdetail.presentation.model.VideoViewModel
import androidx.compose.ui.tooling.preview.Preview

@Composable
internal fun MobileVideoRow(
    videos: List<VideoViewModel>,
    cardColor: Color = BESTVTheme.colors.reviewCardSurface,
    onClick: (VideoViewModel) -> Unit,
) {
    MobileSectionTitle(stringResource(R.string.videos))
    LazyRow(
        contentPadding = PaddingValues(
            horizontal = BESTVTheme.scale.s080,
        ),
        horizontalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s080),
    ) {
        items(videos, key = { it.lazyListKey() }, contentType = { LazyContentTypes.VIDEO }) { video ->
            Card(
                onClick = { onClick(video) },
                modifier = Modifier.width(BESTVTheme.scale.mobileVideoCardWidth),
                colors = CardDefaults.cardColors(containerColor = cardColor),
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(BESTVTheme.scale.s600),
                    ) {
                        AsyncImage(
                            model = video.thumbnailUrl,
                            contentDescription = stringResource(R.string.play_video),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                        Surface(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(BESTVTheme.scale.s320),
                            shape = CircleShape,
                            color = BESTVTheme.colors.imageScrim,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = null,
                                tint = BESTVTheme.colors.white,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(BESTVTheme.scale.s060),
                            )
                        }
                    }
                    Text(
                        text = video.name.orEmpty(),
                        style = MaterialTheme.typography.titleSmall,
                        color = BESTVTheme.colors.white,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(BESTVTheme.scale.s060),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MobileVideoRowPreview() {
    BESTVTheme {
        MobileVideoRow(
            videos = listOf(
                VideoViewModel(id = "1", name = "Trailer"),
                VideoViewModel(id = "2", name = "Behind the scenes"),
            ),
            onClick = {},
        )
    }
}
