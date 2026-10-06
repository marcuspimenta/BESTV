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

package com.pimenta.bestv.presentation.ui.compose.tv

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.material3.Card
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.StandardCardContainer
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.presentation.theme.BESTVTheme

@Composable
fun TVCastCard(
    cast: CastViewModel,
    modifier: Modifier = Modifier,
    includeCastName: Boolean = true,
    onClick: () -> Unit = {},
) {
    StandardCardContainer(
        modifier = modifier.wrapContentSize(),
        imageCard = { interactionSource ->
            Card(
                onClick = onClick,
                interactionSource = interactionSource,
                modifier =
                    Modifier
                        .width(BESTVTheme.scale.s700)
                        .height(BESTVTheme.scale.s850),
            ) {
                AsyncImage(
                    model = cast.thumbnailUrl,
                    contentDescription = cast.name,
                    modifier =
                        Modifier
                            .fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
        },
        title = {
            if (includeCastName) {
                Text(
                    text = cast.name,
                    style = MaterialTheme.typography.labelLarge,
                    color = BESTVTheme.colors.white,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier =
                        Modifier
                            .padding(top = BESTVTheme.scale.s060),
                )
            }
        },
    )
}

@Preview
@Composable
private fun TVCastCardPreview() {
    MaterialTheme {
        TVCastCard(
            cast =
                CastViewModel(
                    id = 1,
                    name = "Christian Bale",
                    character = "Bruce Wayne",
                    birthday = "January 30, 1974",
                    deathDay = "12 Nov 20",
                    biography = "Christian Charles Philip Bale is an English actor.",
                    thumbnailUrl = "",
                    source = "TMDB",
                ),
            onClick = {},
        )
    }
}
