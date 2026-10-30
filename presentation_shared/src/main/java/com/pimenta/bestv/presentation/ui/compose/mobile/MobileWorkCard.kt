/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.presentation.ui.compose.mobile

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.theme.BESTVTheme

@Composable
fun MobileWorkCard(
    work: WorkViewModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = BESTVTheme.colors.mobileCardSurface,
) {
    Card(
        onClick = onClick,
        modifier = modifier.width(BESTVTheme.scale.s600),
        shape = RoundedCornerShape(BESTVTheme.scale.mobileCardCornerRadius),
        border = BorderStroke(BESTVTheme.scale.s005, BESTVTheme.colors.mobileCardOutline),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = BESTVTheme.colors.white,
        ),
    ) {
        Column {
            AsyncImage(
                model = work.posterUrl,
                contentDescription = null,
                placeholder = ColorPainter(containerColor),
                error = ColorPainter(containerColor),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(BESTVTheme.scale.s900)
                    .clip(RoundedCornerShape(BESTVTheme.scale.mobileCardCornerRadius)),
            )
            Column(
                modifier = Modifier.padding(BESTVTheme.scale.s040),
                verticalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s015),
            ) {
                Text(
                    text = work.title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s030)) {
                    if (work.releaseDate.isNotBlank()) {
                        Text(
                            text = work.releaseDate.take(4),
                            style = MaterialTheme.typography.labelSmall,
                            color = BESTVTheme.colors.secondaryText,
                            maxLines = 1,
                        )
                    }
                    if (work.voteAverage > 0f) {
                        Text(
                            text = "★ ${"%.1f".format(work.voteAverage)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = BESTVTheme.colors.secondaryInfoText,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MobileWorkCardPreview() {
    BESTVTheme {
        MobileWorkCard(
            work = WorkViewModel(
                id = 1,
                originalLanguage = "en",
                overview = "A preview description.",
                source = "Movie",
                backdropUrl = "",
                posterUrl = "",
                title = "Preview Movie",
                originalTitle = "Preview Movie",
                releaseDate = "2025",
                type = WorkType.MOVIE,
                voteAverage = 8.0f,
            ),
            onClick = {},
        )
    }
}
