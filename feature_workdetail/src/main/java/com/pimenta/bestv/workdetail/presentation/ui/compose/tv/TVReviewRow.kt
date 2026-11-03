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

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.StandardCardContainer
import androidx.tv.material3.Text
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.presentation.ui.compose.LazyContentTypes
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
    initialFocusRequester: FocusRequester? = null,
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
            itemsIndexed(
                items = reviews,
                key = { _, review -> review.lazyListKey() },
                contentType = { _, _ -> LazyContentTypes.REVIEW },
            ) { index, review ->
                TVReviewCard(
                    review = review,
                    initialFocusRequester = initialFocusRequester.takeIf { index == 0 },
                )
            }
        }
    }
}

@Composable
private fun TVReviewCard(
    review: ReviewViewModel,
    modifier: Modifier = Modifier,
    initialFocusRequester: FocusRequester? = null,
) {
    val content = review.content.orEmpty()
    var expanded by remember(review.id) { mutableStateOf(false) }
    var hasOverflow by remember(review.id) { mutableStateOf(false) }
    val expansionLabel = stringResource(
        if (expanded) PresentationR.string.show_less else PresentationR.string.read_more,
    )

    StandardCardContainer(
        modifier = modifier.width(BESTVTheme.scale.s2000),
        imageCard = { interactionSource ->
            Card(
                onClick = { if (hasOverflow) expanded = !expanded },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = BESTVTheme.scale.s1000)
                    .animateContentSize()
                    .then(initialFocusRequester?.let { Modifier.focusRequester(it) } ?: Modifier)
                    .focusProperties { canFocus = hasOverflow }
                    .semantics { stateDescription = expansionLabel },
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

                    Text(
                        text = content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = BESTVTheme.colors.reviewText,
                        maxLines = if (expanded) 10 else 4,
                        overflow = TextOverflow.Ellipsis,
                        onTextLayout = { result ->
                            if (!expanded) hasOverflow = result.hasVisualOverflow
                        },
                    )

                    if (hasOverflow) {
                        Text(
                            text = expansionLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = BESTVTheme.colors.white,
                        )
                    }
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
