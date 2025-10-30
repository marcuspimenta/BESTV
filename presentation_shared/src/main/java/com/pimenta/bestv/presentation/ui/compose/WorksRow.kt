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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
    isLoadingMore: Boolean = false,
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
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 48.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Horizontal scrolling row
        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = works,
                key = { work -> work.id }
            ) { work ->
                WorkCard(
                    work = work,
                    onClick = { onWorkClick(work) }
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
                    title = "The Dark Knight",
                    originalTitle = "The Dark Knight",
                    posterUrl = null,
                    type = WorkType.MOVIE,
                    source = "TMDB"
                ),
                WorkViewModel(
                    id = 2,
                    title = "Batman Begins",
                    originalTitle = "Batman Begins",
                    posterUrl = null,
                    type = WorkType.MOVIE,
                    source = "TMDB"
                ),
                WorkViewModel(
                    id = 3,
                    title = "The Prestige",
                    originalTitle = "The Prestige",
                    posterUrl = null,
                    type = WorkType.MOVIE,
                    source = "TMDB"
                )
            ),
            onWorkClick = {}
        )
    }
}
