/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workdetail.presentation.ui.compose.mobile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import coil3.compose.AsyncImage
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.presentation.ui.compose.LazyContentTypes
import com.pimenta.bestv.presentation.ui.compose.mobile.MobileSectionTitle
import com.pimenta.bestv.workdetail.R
import com.pimenta.bestv.workdetail.presentation.model.WatchProvidersViewModel
import androidx.compose.ui.tooling.preview.Preview

@Composable
internal fun MobileWatchProvidersRow(providers: WatchProvidersViewModel) {
    if (!providers.hasAnyProvider) return

    MobileSectionTitle(
        title = stringResource(R.string.where_to_watch),
        style = MaterialTheme.typography.titleSmall,
        startPadding = BESTVTheme.scale.s000,
        topPadding = BESTVTheme.scale.s080,
    )
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s060),
    ) {
        items(providers.providers, key = { it.id }, contentType = { LazyContentTypes.PROVIDER }) { provider ->
            AsyncImage(
                model = provider.logoUrl,
                contentDescription = provider.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(BESTVTheme.scale.s200)
                    .clip(RoundedCornerShape(BESTVTheme.scale.s040)),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MobileWatchProvidersRowPreview() {
    BESTVTheme {
        MobileWatchProvidersRow(
            WatchProvidersViewModel(
                tmdbLink = null,
                providers = listOf(
                    com.pimenta.bestv.workdetail.presentation.model.WatchProviderViewModel(
                        1,
                        "Stream",
                        null,
                    ),
                    com.pimenta.bestv.workdetail.presentation.model.WatchProviderViewModel(
                        2,
                        "Cinema",
                        null,
                    ),
                ),
            ),
        )
    }
}
