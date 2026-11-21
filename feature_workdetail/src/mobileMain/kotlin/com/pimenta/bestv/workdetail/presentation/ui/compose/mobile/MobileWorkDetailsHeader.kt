/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workdetail.presentation.ui.compose.mobile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.AsyncImage
import com.pimenta.bestv.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.workdetail.presentation.model.WatchProvidersViewModel
import com.pimenta.bestv.workdetail.presentation.model.CrewViewModel
import com.pimenta.bestv.workdetail.presentation.ui.compose.WorkCrewDetails

@Composable
internal fun MobileWorkDetailsHeader(
    work: WorkViewModel,
    backgroundColor: Color,
    crew: List<CrewViewModel> = emptyList(),
    providers: WatchProvidersViewModel?,
    onTitleBottomChanged: (Float) -> Unit,
    onBackdropColorChanged: (String, Color) -> Unit,
) {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val heroHeight = BESTVTheme.scale.mobileBackdropHeight + BESTVTheme.scale.s320 + statusBarHeight
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(heroHeight),
        ) {
            AsyncImage(
                model = work.backdropUrl,
                contentDescription = null,
                placeholder = ColorPainter(BESTVTheme.colors.mobileCardSurface),
                error = ColorPainter(BESTVTheme.colors.mobileCardSurface),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                onSuccess = {
                    onBackdropColorChanged(work.backdropUrl, BESTVTheme.colors.mobileCardSurface)
                },
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0f to BESTVTheme.colors.black.copy(alpha = 0.55f),
                                0.3f to BESTVTheme.colors.black.copy(alpha = 0.12f),
                                0.65f to backgroundColor.copy(alpha = 0.08f),
                                0.78f to backgroundColor.copy(alpha = 0.28f),
                                0.88f to backgroundColor.copy(alpha = 0.56f),
                                0.96f to backgroundColor.copy(alpha = 0.84f),
                                1f to backgroundColor,
                            ),
                        ),
                    ),
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(horizontal = BESTVTheme.scale.s080, vertical = BESTVTheme.scale.s060),
            ) {
                Text(
                    text = work.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = BESTVTheme.colors.white,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        onTitleBottomChanged(coordinates.boundsInRoot().bottom)
                    },
                )
                Text(
                    text = workMetadataLine(work),
                    style = MaterialTheme.typography.bodyMedium,
                    color = BESTVTheme.colors.secondaryInfoText,
                    modifier = Modifier.padding(top = BESTVTheme.scale.s040),
                )
            }
        }
        Column(
            modifier = Modifier.padding(
                horizontal = BESTVTheme.scale.s080,
                vertical = BESTVTheme.scale.s060,
            ),
        ) {
            Text(
                text = work.overview,
                style = MaterialTheme.typography.bodyLarge,
                color = BESTVTheme.colors.biographyText,
            )
            WorkCrewDetails(
                crew = crew,
                modifier = Modifier.padding(top = BESTVTheme.scale.s080),
            )
            providers?.let { MobileWatchProvidersRow(it) }
        }
    }
}

private fun workMetadataLine(work: WorkViewModel): String = listOfNotNull(
    work.releaseDate.takeIf(String::isNotBlank),
    work.voteAverage.takeIf { it > 0f }?.let { "★ ${(it * 10).toInt() / 10f}" },
    work.source.takeIf(String::isNotBlank),
).joinToString(" · ")
