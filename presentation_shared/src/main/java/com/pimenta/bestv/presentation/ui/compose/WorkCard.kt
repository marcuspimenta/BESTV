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

package com.pimenta.bestv.presentation.ui.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.StandardCardContainer
import androidx.tv.material3.Text
import coil.compose.SubcomposeAsyncImage
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.model.presentation.model.WorkType

@Composable
fun WorkCard(
    work: WorkViewModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onFocusChanged: (Boolean) -> Unit = {},
    includeWorkTitle: Boolean = true
) {
    StandardCardContainer(
        modifier = Modifier.width(250.dp),
        imageCard = { interactionSource ->
            Card(
                onClick = onClick,
                modifier = modifier
                    .fillMaxWidth()
                    .height(143.dp)
                    .onFocusChanged { focusState ->
                        onFocusChanged(focusState.isFocused)
                    },
                interactionSource = interactionSource
            ) {
                // Poster image
                SubcomposeAsyncImage(
                    model = work.backdropUrl,
                    contentDescription = work.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }
        },
        title = {
            if (includeWorkTitle) {
                Text(
                    text = work.title ?: work.originalTitle ?: "",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp)
                )
            }
        }
    )


}

@Preview
@Composable
private fun WorkCardPreview() {
    MaterialTheme {
        WorkCard(
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
                isFavorite = false
            ),
            onClick = {}
        )
    }
}
