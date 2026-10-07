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

package com.pimenta.bestv.workdetail.presentation.ui.compose.tv

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.StandardCardContainer
import androidx.tv.material3.Text
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.presentation.ui.compose.LazyRowPagination
import com.pimenta.bestv.presentation.ui.compose.tv.TVStartAlignedLazyRow
import com.pimenta.bestv.workdetail.R
import com.pimenta.bestv.workdetail.presentation.model.ReviewViewModel
import com.pimenta.bestv.presentation.R as PresentationR

@Composable
fun TVReviewRow(
    reviews: List<ReviewViewModel>,
    modifier: Modifier = Modifier,
    isLoadingMore: Boolean = false,
    onLoadMore: () -> Unit = {},
) {
    val listState = rememberLazyListState()

    // Monitor scroll position and trigger pagination when near the end
    LazyRowPagination(
        listState = listState,
        isLoadingMore = isLoadingMore,
        threshold = 3,
        onLoadMore = onLoadMore,
    )

    Column(
        modifier = modifier.padding(vertical = BESTVTheme.scale.s100),
    ) {
        // Section title
        Text(
            text = stringResource(R.string.reviews),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = BESTVTheme.colors.white,
            modifier = Modifier
                .padding(
                    horizontal = BESTVTheme.scale.s240,
                    vertical = BESTVTheme.scale.s040,
                ),
        )

        // Reviews list with start-aligned focus behavior
        TVStartAlignedLazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = BESTVTheme.scale.s240),
            horizontalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s120),
            modifier = Modifier
                .padding(top = BESTVTheme.scale.s090),
        ) {
            items(
                items = reviews,
                key = { it.id ?: it.hashCode() },
            ) { review ->
                TVReviewCard(
                    review = review,
                )
            }
        }
    }
}

@Composable
private fun TVReviewCard(
    review: ReviewViewModel,
    modifier: Modifier = Modifier,
) {
    StandardCardContainer(
        modifier = modifier.width(BESTVTheme.scale.s2000),
        imageCard = { interactionSource ->
            Card(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(BESTVTheme.scale.s1000),
                interactionSource = interactionSource,
                colors = CardDefaults.colors(
                    containerColor = BESTVTheme.colors.reviewCardSurface,
                ),
            ) {
                Column(
                    modifier = Modifier
                        .padding(BESTVTheme.scale.s080),
                    verticalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s040),
                ) {
                    Text(
                        text = review.author ?: stringResource(PresentationR.string.unknown),
                        style = MaterialTheme.typography.titleMedium,
                        color = BESTVTheme.colors.white,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    // Review content (truncated)
                    Text(
                        text = review.content ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BESTVTheme.colors.reviewText,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        },
        title = {},
    )
}

@Preview
@Composable
private fun TVReviewRowPreview() {
    MaterialTheme {
        TVReviewRow(
            reviews = listOf(
                ReviewViewModel(
                    id = "1",
                    author = "John Doe",
                    content =
                    "This is an amazing movie with great acting and an incredible " +
                        "storyline that keeps you engaged from start to finish. The " +
                        "cinematography is stunning and the music perfectly complements " +
                        "every scene.",
                ),
                ReviewViewModel(
                    id = "2",
                    author = "Jane Smith",
                    content =
                    "One of the best movies I've ever seen. The plot twists are " +
                        "unexpected and the character development is superb.",
                ),
                ReviewViewModel(
                    id = "3",
                    author = "Bob Johnson",
                    content =
                    "A masterpiece of modern cinema. Every frame is carefully crafted " +
                        "and the performances are outstanding.",
                ),
            ),
            isLoadingMore = false,
        )
    }
}
