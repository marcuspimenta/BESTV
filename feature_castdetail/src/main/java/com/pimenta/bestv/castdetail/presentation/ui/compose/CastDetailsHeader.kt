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

package com.pimenta.bestv.castdetail.presentation.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.presentation.ui.compose.CastCard

@Composable
fun CastDetailsHeader(
    cast: CastViewModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        // Profile photo
        CastCard(cast = cast)

        Spacer(modifier = Modifier.width(32.dp))

        // Cast details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Name
            Text(
                text = cast.name ?: "",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Birthday
            cast.birthday?.let { birthday ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Born: $birthday",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            // Death day
            cast.deathDay?.let { deathDay ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Died: $deathDay",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            // Biography
            cast.biography?.let { biography ->
                if (biography.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = biography,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.9f),
                        maxLines = 8,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Source
            cast.source?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Preview
@Composable
private fun CastDetailsHeaderPreview() {
    MaterialTheme {
        CastDetailsHeader(
            cast = CastViewModel(
                id = 1,
                name = "Christian Bale",
                character = null,
                birthday = "January 30, 1974",
                deathDay = null,
                biography = "Christian Charles Philip Bale is an English actor. Known for his " +
                        "versatility and physical transformations for his roles, he has been a " +
                        "leading man in films of several genres.",
                thumbnailUrl = null,
                source = "TMDB"
            )
        )
    }
}
