/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workdetail.presentation.ui.compose.mobile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.presentation.ui.compose.LazyRowPagination
import com.pimenta.bestv.presentation.ui.compose.LazyContentTypes
import com.pimenta.bestv.presentation.ui.compose.mobile.MobileSectionTitle
import com.pimenta.bestv.workdetail.R
import com.pimenta.bestv.workdetail.presentation.model.ReviewViewModel
import androidx.compose.ui.tooling.preview.Preview

@Composable
internal fun MobileReviewRow(
    reviews: List<ReviewViewModel>,
    page: com.pimenta.bestv.presentation.model.PaginationState,
    onLoadMore: () -> Unit,
    cardColor: Color = BESTVTheme.colors.reviewCardSurface,
) {
    MobileSectionTitle(stringResource(R.string.reviews))
    val listState = rememberLazyListState()
    LazyRow(
        state = listState,
        contentPadding = PaddingValues(horizontal = BESTVTheme.scale.s080),
        horizontalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s080),
    ) {
        items(reviews, key = { it.lazyListKey() }, contentType = { LazyContentTypes.REVIEW }) { review ->
            Card(
                modifier = Modifier
                    .width(BESTVTheme.scale.s1250),
                colors = CardDefaults.cardColors(containerColor = cardColor),
            ) {
                Column(
                    modifier = Modifier
                        .padding(BESTVTheme.scale.s080),
                ) {
                    Text(review.author.orEmpty(), style = MaterialTheme.typography.titleMedium)
                    Text(
                        review.content.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = BESTVTheme.colors.reviewText,
                        minLines = 8,
                        maxLines = 8,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
    LazyRowPagination(
        listState = listState,
        isLoadingMore = page.isLoadingMore,
        threshold = 3,
        onLoadMore = { if (page.canLoadMore) onLoadMore() },
    )
}

@Preview(showBackground = true)
@Composable
private fun MobileReviewRowPreview() {
    BESTVTheme {
        MobileReviewRow(
            reviews = listOf(
                ReviewViewModel(
                    id = "1",
                    author = "A. Reviewer",
                    content =
                    "A thoughtful review preview with enough text to demonstrate the " +
                        "fixed eight line card content, truncation, and horizontal scrolling.",
                ),
                ReviewViewModel(id = "2", author = "B. Reviewer", content = "A second review."),
            ),
            page = com.pimenta.bestv.presentation.model
                .PaginationState(),
            onLoadMore = {},
        )
    }
}
