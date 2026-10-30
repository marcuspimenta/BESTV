/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workdetail.presentation.ui.compose.mobile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.AsyncImage
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.presentation.ui.compose.LazyContentTypes
import com.pimenta.bestv.presentation.ui.compose.mobile.MobileSectionTitle
import com.pimenta.bestv.workdetail.R
import androidx.compose.ui.tooling.preview.Preview

@Composable
internal fun MobileCastRow(
    casts: List<CastViewModel>,
    cardColor: Color = BESTVTheme.colors.reviewCardSurface,
    onClick: (CastViewModel) -> Unit,
) {
    MobileSectionTitle(stringResource(R.string.cast))
    LazyRow(
        contentPadding = PaddingValues(
            horizontal = BESTVTheme.scale.s080,
        ),
        horizontalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s080),
    ) {
        items(casts, key = { it.id }, contentType = { LazyContentTypes.CAST }) { cast ->
            Card(
                onClick = { onClick(cast) },
                modifier = Modifier.width(BESTVTheme.scale.mobileCastCardWidth),
                colors = CardDefaults.cardColors(containerColor = cardColor),
            ) {
                Column {
                    AsyncImage(
                        model = cast.thumbnailUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(BESTVTheme.scale.s750),
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

@Preview(showBackground = true)
@Composable
private fun MobileCastRowPreview() {
    BESTVTheme {
        MobileCastRow(
            casts = listOf(
                CastViewModel(1, "Alex Morgan", "Lead", "", "", "", "", ""),
                CastViewModel(2, "Sam Taylor", "Friend", "", "", "", "", ""),
            ),
            onClick = {},
        )
    }
}
