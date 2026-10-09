/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.presentation.ui.compose.mobile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.model.PaginationState
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.presentation.ui.compose.LazyRowPagination

@Composable
fun MobileWorkRow(
    title: String,
    works: List<WorkViewModel>,
    onWorkClick: (WorkViewModel) -> Unit,
    modifier: Modifier = Modifier,
    pagination: PaginationState? = null,
    onLoadMore: () -> Unit = {},
) {
    if (works.isEmpty()) return

    val listState = rememberLazyListState()
    Column(modifier = modifier.fillMaxWidth()) {
        MobileSectionTitle(title)
        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = BESTVTheme.scale.s080),
            horizontalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s080),
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            items(works, key = { it.id }) { work ->
                MobileWorkCard(work, onClick = { onWorkClick(work) })
            }
            if (pagination?.isLoadingMore == true) {
                item(key = "loading") {
                    MobileLoading(Modifier.padding(BESTVTheme.scale.s240))
                }
            }
        }
        if (pagination != null) {
            LazyRowPagination(
                listState = listState,
                isLoadingMore = pagination.isLoadingMore,
                threshold = 3,
                onLoadMore = { if (pagination.canLoadMore) onLoadMore() },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MobileWorkRowPreview() {
    val previewWork = WorkViewModel(
        id = 1,
        originalLanguage = "en",
        overview = "A preview description.",
        source = "Movie",
        backdropUrl = "",
        posterUrl = "",
        title = "Preview Movie",
        originalTitle = "Preview Movie",
        releaseDate = "2025",
        type = WorkType.MOVIE,
        voteAverage = 8.0f,
    )
    BESTVTheme {
        MobileWorkRow(
            title = "Popular",
            works = listOf(previewWork, previewWork.copy(id = 2, title = "Next Movie")),
            onWorkClick = {},
        )
    }
}
