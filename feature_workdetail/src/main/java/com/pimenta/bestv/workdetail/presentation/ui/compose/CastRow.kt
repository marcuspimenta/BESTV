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

package com.pimenta.bestv.workdetail.presentation.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.presentation.ui.compose.CastCard
import com.pimenta.bestv.presentation.ui.compose.StartAlignedLazyRow
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.workdetail.R

@Composable
fun CastRow(
    casts: List<CastViewModel>,
    onCastClick: (CastViewModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(vertical = BESTVTheme.scale.s100)
    ) {
        // Section title
        Text(
            text = stringResource(R.string.cast),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = BESTVTheme.colors.white,
            modifier = Modifier.padding(horizontal = BESTVTheme.scale.s240)
        )

        // Cast list with start-aligned focus behavior
        StartAlignedLazyRow(
            contentPadding = PaddingValues(horizontal = BESTVTheme.scale.s240),
            horizontalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s120),
            modifier = Modifier.padding(top = BESTVTheme.scale.s090)
        ) {
            items(
                items = casts,
                key = { it.id }
            ) { cast ->
                CastCard(
                    cast = cast,
                    onClick = { onCastClick(cast) },
                )
            }
        }
    }
}

@Preview
@Composable
private fun CastRowPreview() {
    MaterialTheme {
        CastRow(
            casts = listOf(
                CastViewModel(
                    id = 1,
                    name = "Christian Bale",
                    character = "Bruce Wayne / Batman",
                    thumbnailUrl = "",
                    source = "TMDB",
                    birthday = "",
                    deathDay = "",
                    biography = ""
                ),
                CastViewModel(
                    id = 2,
                    name = "Heath Ledger",
                    character = "Joker",
                    thumbnailUrl = "",
                    source = "TMDB",
                    birthday = "",
                    deathDay = "",
                    biography = ""
                )
            ),
            onCastClick = {}
        )
    }
}
