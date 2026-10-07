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

package com.pimenta.bestv.castdetail.presentation.ui.compose.tv

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.pimenta.bestv.castdetail.R
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.presentation.ui.compose.tv.TVCastCard
import com.pimenta.bestv.presentation.ui.compose.tv.TVExpandableText

@Composable
fun TVCastDetailsHeader(
    cast: CastViewModel,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(BESTVTheme.scale.s240),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top,
    ) {
        TVCastCard(
            cast = cast,
            includeCastName = false,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = BESTVTheme.scale.s160),
        ) {
            Text(
                text = cast.name,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = BESTVTheme.colors.white,
            )

            Text(
                text = stringResource(R.string.born, cast.birthday),
                style = MaterialTheme.typography.bodyLarge,
                color = BESTVTheme.colors.secondaryInfoText,
                modifier = Modifier
                    .padding(top = BESTVTheme.scale.s060),
            )

            if (cast.biography.isNotBlank()) {
                TVExpandableText(
                    text = cast.biography,
                    color = BESTVTheme.colors.biographyText,
                    modifier = Modifier
                        .padding(top = BESTVTheme.scale.s060),
                )
            }
        }
    }
}

@Preview
@Composable
private fun TVCastDetailsHeaderPreview() {
    MaterialTheme {
        TVCastDetailsHeader(
            cast = CastViewModel(
                id = 1,
                name = "Christian Bale",
                character = "",
                birthday = "January 30, 1974",
                deathDay = "",
                biography =
                "Christian Charles Philip Bale is an English actor. Known for his " +
                    "versatility and physical transformations for his roles, he has been a " +
                    "leading man in films of several genres.",
                thumbnailUrl = "",
                source = "TMDB",
            ),
        )
    }
}
