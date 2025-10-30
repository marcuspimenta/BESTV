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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.font.FontWeight
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.presentation.ui.compose.CastCard

@Composable
fun CastRow(
    casts: List<CastViewModel>,
    onCastClick: (CastViewModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(vertical = 20.dp)
    ) {
        // Section title
        Text(
            text = "Cast",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 48.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Cast list
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 48.dp)
        ) {
            items(
                items = casts,
                key = { it.id }
            ) { cast ->
                CastCard(
                    cast = cast,
                    onClick = { onCastClick(cast) },
                    size = 150.dp,
                    includeCastName = true
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
                    thumbnailUrl = null,
                    source = "TMDB"
                ),
                CastViewModel(
                    id = 2,
                    name = "Heath Ledger",
                    character = "Joker",
                    thumbnailUrl = null,
                    source = "TMDB"
                ),
                CastViewModel(
                    id = 3,
                    name = "Aaron Eckhart",
                    character = "Harvey Dent / Two-Face",
                    thumbnailUrl = null,
                    source = "TMDB"
                )
            ),
            onCastClick = {}
        )
    }
}
