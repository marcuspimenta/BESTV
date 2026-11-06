/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workdetail.presentation.ui.compose.mobile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.presentation.ui.compose.LazyContentTypes
import com.pimenta.bestv.presentation.ui.compose.mobile.MobileSectionTitle
import com.pimenta.bestv.workdetail.R

@Composable
internal fun MobileCastRow(
    casts: List<CastViewModel>,
    cardColor: Color = BESTVTheme.colors.reviewCardSurface,
    onClick: (CastViewModel) -> Unit,
) {
    MobileSectionTitle(stringResource(R.string.cast))
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val cardWidth = (maxWidth * MOBILE_CAST_CARD_WIDTH_FRACTION)
            .coerceAtMost(BESTVTheme.scale.mobileCastCardWidth)
        LazyRow(
            contentPadding = PaddingValues(horizontal = BESTVTheme.scale.s080),
            horizontalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s060),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(casts, key = { it.id }, contentType = { LazyContentTypes.CAST }) { cast ->
                Card(
                    onClick = { onClick(cast) },
                    modifier = Modifier.width(cardWidth),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                ) {
                    Column {
                        AsyncImage(
                            model = cast.thumbnailUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(2f / 3f),
                        )
                        Text(
                            text = cast.name,
                            style = MaterialTheme.typography.labelLarge,
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
}

private const val MOBILE_CAST_CARD_WIDTH_FRACTION = 0.26f

@Preview(showBackground = true)
@Composable
private fun MobileCastRowPreview() {
    BESTVTheme {
        MobileCastRow(
            casts = listOf(
                CastViewModel(1, "Alex Morgan", "Lead", "", "", "", "", ""),
                CastViewModel(2, "Sam Taylor", "Friend", "", "", "", "", ""),
                CastViewModel(3, "Jamie Lee", "Detective", "", "", "", "", ""),
                CastViewModel(4, "Morgan Blake", "Doctor", "", "", "", "", ""),
            ),
            onClick = {},
        )
    }
}
