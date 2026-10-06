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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.workdetail.R
import com.pimenta.bestv.workdetail.presentation.model.WatchProviderViewModel
import com.pimenta.bestv.workdetail.presentation.model.WatchProvidersViewModel

@Composable
fun TVWatchProvidersRow(
    watchProviders: WatchProvidersViewModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = stringResource(R.string.where_to_watch),
            style = MaterialTheme.typography.bodySmall,
            color = BESTVTheme.colors.secondaryText,
            modifier =
                Modifier
                    .padding(bottom = BESTVTheme.scale.s040),
        )

        TVProviderSection(
            providers = watchProviders.providers,
        )
    }
}

@Composable
private fun TVProviderSection(
    providers: List<WatchProviderViewModel>,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s060),
        modifier = modifier,
    ) {
        items(
            items = providers,
            key = { it.id },
        ) { provider ->
            TVProviderItem(provider = provider)
        }
    }
}

@Composable
private fun TVProviderItem(
    provider: WatchProviderViewModel,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = provider.logoUrl,
        contentDescription = provider.name,
        contentScale = ContentScale.Crop,
        modifier =
            modifier
                .size(BESTVTheme.scale.s240)
                .clip(RoundedCornerShape(BESTVTheme.scale.s040)),
    )
}
