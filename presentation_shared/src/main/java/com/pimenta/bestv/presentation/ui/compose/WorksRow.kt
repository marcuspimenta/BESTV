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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel

@Composable
fun WorksRow(
    title: String,
    works: List<WorkViewModel>,
    onWorkClick: (WorkViewModel) -> Unit,
    modifier: Modifier = Modifier,
    titleStyle: TextStyle = MaterialTheme.typography.headlineSmall,
    titleStartPadding: Dp = 48.dp,
    worksStartPadding: Dp = 48.dp,
    onWorkFocused: (WorkViewModel) -> Unit = {},
    isLoadingMore: Boolean = false,
    includeWorkTitle: Boolean = true,
    onLoadMore: () -> Unit = {}
) {
    if (works.isEmpty()) return

    val listState = rememberLazyListState()

    // Monitor scroll position and trigger pagination when near the end
    LazyRowPagination(
        listState = listState,
        isLoadingMore = isLoadingMore,
        threshold = 3,
        onLoadMore = onLoadMore
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
    ) {
        // Section title
        Text(
            text = title,
            style = titleStyle,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(start = titleStartPadding)
        )

        // Horizontal scrolling row with start-aligned focus behavior
        StartAlignedLazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = worksStartPadding),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(top = 18.dp)
        ) {
            items(
                items = works,
                key = { work -> work.id }
            ) { work ->
                WorkCard(
                    work = work,
                    includeWorkTitle = includeWorkTitle,
                    onClick = { onWorkClick(work) },
                    onFocusChanged = { focused ->
                        if (focused) {
                            onWorkFocused(work)
                        }
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun WorksRowPreview() {
    MaterialTheme {
        WorksRow(
            title = "Movies",
            works = listOf(
                WorkViewModel(
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
                    isFavorite = false
                ),
                WorkViewModel(
                    id = 2,
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
                    isFavorite = false
                ),
            ),
            onWorkClick = {}
        )
    }
}
