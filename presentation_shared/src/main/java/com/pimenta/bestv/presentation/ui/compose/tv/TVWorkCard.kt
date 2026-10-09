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
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.material3.Card
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.StandardCardContainer
import coil3.compose.AsyncImage
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.theme.BESTVTheme

@Composable
fun TVWorkCard(
    work: WorkViewModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onFocusChanged: (Boolean) -> Unit = {},
) {
    StandardCardContainer(
        modifier = modifier
            .width(BESTVTheme.scale.s600)
            .height(BESTVTheme.scale.s850),
        imageCard = { interactionSource ->
            Card(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxSize()
                    .onFocusChanged { focusState ->
                        onFocusChanged(focusState.isFocused)
                    },
                interactionSource = interactionSource,
            ) {
                AsyncImage(
                    model = work.posterUrl,
                    contentDescription = work.title,
                    contentScale = ContentScale.Crop,
                    placeholder = ColorPainter(BESTVTheme.colors.reviewCardSurface),
                    error = ColorPainter(BESTVTheme.colors.reviewCardSurface),
                )
            }
        },
        title = {},
    )
}

@Preview
@Composable
private fun TVWorkCardPreview() {
    MaterialTheme {
        TVWorkCard(
            work = WorkViewModel(
                id = 1,
                title = "The Dark Knight The Dark Knight The Dark Knight The Dark Knight",
                originalTitle = "The Dark Knight",
                posterUrl = "",
                type = WorkType.MOVIE,
                source = "TMDB",
                originalLanguage = "",
                overview = "",
                backdropUrl = "",
                releaseDate = "",
                voteAverage = 0f,
                isFavorite = false,
            ),
            onClick = {},
        )
    }
}
