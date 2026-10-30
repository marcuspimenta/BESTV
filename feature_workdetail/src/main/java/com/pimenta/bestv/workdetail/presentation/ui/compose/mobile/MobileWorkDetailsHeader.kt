/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workdetail.presentation.ui.compose.mobile

import android.graphics.Bitmap
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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextOverflow
import coil3.BitmapImage
import coil3.Image
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.workdetail.presentation.model.WatchProvidersViewModel
import com.pimenta.bestv.workdetail.presentation.model.CrewViewModel
import com.pimenta.bestv.workdetail.presentation.ui.compose.WorkCrewDetails
import androidx.compose.ui.tooling.preview.Preview

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
    val context = LocalPlatformContext.current
    val backdropRequest = remember(context, work.backdropUrl) {
        ImageRequest
            .Builder(context)
            .data(work.backdropUrl)
            .allowHardware(false)
            .build()
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(heroHeight),
        ) {
            AsyncImage(
                model = backdropRequest,
                contentDescription = null,
                placeholder = ColorPainter(BESTVTheme.colors.mobileCardSurface),
                error = ColorPainter(BESTVTheme.colors.mobileCardSurface),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                onSuccess = { result ->
                    val dominantColor = result.result.image.extractDominantColor()
                    onBackdropColorChanged(work.backdropUrl, dominantColor)
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
    work.voteAverage.takeIf { it > 0f }?.let { "★ ${"%.1f".format(it)}" },
    work.source.takeIf(String::isNotBlank),
).joinToString(" · ")

private fun Image.extractDominantColor(): Color {
    val imageWidth = width
    val imageHeight = height
    if (imageWidth <= 0 || imageHeight <= 0) return Color.Black

    val bitmap = try {
        if (this is BitmapImage) {
            this.bitmap
        } else {
            toBitmap(width = imageWidth, height = imageHeight, config = Bitmap.Config.ARGB_8888)
        }
    } catch (_: IllegalArgumentException) {
        return Color.Black
    } catch (_: IllegalStateException) {
        return Color.Black
    }
    if (bitmap.config == Bitmap.Config.HARDWARE) return Color.Black

    val colorCounts = IntArray(1 shl 15)
    val fallbackColorCounts = IntArray(1 shl 15)
    val sampleStep = maxOf(maxOf(bitmap.width, bitmap.height) / 64, 1)
    var dominantBucket = -1
    var dominantCount = 0
    var fallbackBucket = -1
    var fallbackCount = 0

    for (y in 0 until bitmap.height step sampleStep) {
        for (x in 0 until bitmap.width step sampleStep) {
            val pixel = bitmap.getPixel(x, y)
            if ((pixel ushr 24) < 0x80) continue

            val red = (pixel shr 16) and 0xFF
            val green = (pixel shr 8) and 0xFF
            val blue = pixel and 0xFF
            val bucket = ((red shr 3) shl 10) or ((green shr 3) shl 5) or (blue shr 3)
            val fallbackCountForBucket = ++fallbackColorCounts[bucket]
            if (fallbackCountForBucket > fallbackCount) {
                fallbackBucket = bucket
                fallbackCount = fallbackCountForBucket
            }

            if (maxOf(red, green, blue) >= 48) {
                val count = ++colorCounts[bucket]
                if (count > dominantCount) {
                    dominantBucket = bucket
                    dominantCount = count
                }
            }
        }
    }

    if (dominantBucket < 0) dominantBucket = fallbackBucket
    if (dominantBucket < 0) return Color.Black

    return Color(
        red = ((((dominantBucket shr 10) and 0x1F) shl 3) + 4) / 255f,
        green = ((((dominantBucket shr 5) and 0x1F) shl 3) + 4) / 255f,
        blue = (((dominantBucket and 0x1F) shl 3) + 4) / 255f,
    )
}

@Preview(showBackground = true)
@Composable
private fun MobileWorkDetailsHeaderPreview() {
    BESTVTheme {
        MobileWorkDetailsHeader(
            mobilePreviewWork,
            backgroundColor = BESTVTheme.colors.black,
            crew = listOf(
                CrewViewModel(id = 1, name = "Example Director", role = "Director"),
                CrewViewModel(id = 2, name = "Example Writer", role = "Writer"),
            ),
            providers = null,
            onTitleBottomChanged = {},
            onBackdropColorChanged = { _, _ -> },
        )
    }
}
