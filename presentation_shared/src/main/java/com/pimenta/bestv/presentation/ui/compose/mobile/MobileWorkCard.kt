/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.presentation.ui.compose.mobile

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
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
) {
    Card(
        onClick = onClick,
        modifier = modifier.width(BESTVTheme.scale.s520),
        colors = CardDefaults.cardColors(containerColor = BESTVTheme.colors.reviewCardSurface),
    ) {
        AsyncImage(
            model = work.posterUrl,
            contentDescription = work.title,
            placeholder = ColorPainter(BESTVTheme.colors.reviewCardSurface),
            error = ColorPainter(BESTVTheme.colors.reviewCardSurface),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(BESTVTheme.scale.s850)
                .clip(RoundedCornerShape(BESTVTheme.scale.s040)),
        )
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
