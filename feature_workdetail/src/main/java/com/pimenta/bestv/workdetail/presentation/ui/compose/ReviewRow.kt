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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.pimenta.bestv.presentation.ui.compose.LazyRowPagination
import com.pimenta.bestv.presentation.ui.compose.StartAlignedLazyRow
import com.pimenta.bestv.workdetail.presentation.model.ReviewViewModel

@Composable
fun ReviewRow(
    reviews: List<ReviewViewModel>,
    modifier: Modifier = Modifier,
    isLoadingMore: Boolean = false,
    onLoadMore: () -> Unit = {}
) {
    val listState = rememberLazyListState()

    // Monitor scroll position and trigger pagination when near the end
    LazyRowPagination(
        listState = listState,
        isLoadingMore = isLoadingMore,
        threshold = 3,
        onLoadMore = onLoadMore
    )

    Column(
        modifier = modifier.padding(vertical = 20.dp)
    ) {
        // Section title
        Text(
            text = "Reviews",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 48.dp, vertical = 8.dp)
        )

        // Reviews list with start-aligned focus behavior
        StartAlignedLazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(top = 18.dp)
        ) {
            items(
                items = reviews,
                key = { it.id ?: it.hashCode() }
            ) { review ->
                ReviewCard(
                    review = review
                )
            }
        }
    }
}

@Composable
private fun ReviewCard(
    review: ReviewViewModel,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = {},
        modifier = modifier
            .width(400.dp)
            .height(200.dp),
        scale = CardDefaults.scale(focusedScale = 1.05f),
        border = CardDefaults.border(
            focusedBorder = Border(
                border = androidx.compose.foundation.BorderStroke(
                    width = 3.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        ),
        colors = CardDefaults.colors(
            containerColor = Color.DarkGray
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = review.author ?: "Unknown",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Review content (truncated)
            Text(
                text = review.content ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                maxLines = 6,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
private fun ReviewRowPreview() {
    MaterialTheme {
        ReviewRow(
            reviews = listOf(
                ReviewViewModel(
                    id = "1",
                    author = "John Doe",
                    content = "This is an amazing movie with great acting and an incredible " +
                            "storyline that keeps you engaged from start to finish. The " +
                            "cinematography is stunning and the music perfectly complements " +
                            "every scene."
                ),
                ReviewViewModel(
                    id = "2",
                    author = "Jane Smith",
                    content = "One of the best movies I've ever seen. The plot twists are " +
                            "unexpected and the character development is superb."
                ),
                ReviewViewModel(
                    id = "3",
                    author = "Bob Johnson",
                    content = "A masterpiece of modern cinema. Every frame is carefully crafted " +
                            "and the performances are outstanding."
                )
            ),
            isLoadingMore = false
        )
    }
}
