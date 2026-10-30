/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workdetail.presentation.ui.compose.mobile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.presentation.ui.compose.LazyContentTypes
import com.pimenta.bestv.presentation.ui.compose.mobile.MobileError
import com.pimenta.bestv.presentation.ui.compose.mobile.MobileLoading
import com.pimenta.bestv.presentation.ui.compose.mobile.MobileWorkRow
import com.pimenta.bestv.workdetail.R
import com.pimenta.bestv.workdetail.presentation.model.ReviewViewModel
import com.pimenta.bestv.workdetail.presentation.model.VideoViewModel
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.CastClicked
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.DismissError
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.LoadData
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.LoadMoreRecommendations
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.LoadMoreReviews
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.LoadMoreSimilar
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.VideoClicked
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.WorkClicked
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.ActionButton
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.Content.Casts
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.Content.Header
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.Content.RecommendedWorks
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.Content.Reviews
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.Content.SimilarWorks
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.Content.Videos
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.State.Error
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.State.Loaded
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.State.Loading
import com.pimenta.bestv.workdetail.presentation.ui.compose.WorkMetadataDetails

@Composable
internal fun MobileWorkDetailsContent(
    state: WorkDetailsState,
    backgroundColor: Color,
    cardBackgroundColor: Color,
    listState: androidx.compose.foundation.lazy.LazyListState,
    contentPadding: PaddingValues,
    onEvent: (WorkDetailsEvent) -> Unit,
    onListViewportTopChanged: (Float) -> Unit,
    onHeaderTitleBottomChanged: (Float) -> Unit,
    onBackdropColorChanged: (String, Color) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
    ) {
        when (val current = state.state) {
            is Loading ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    MobileLoading()
                }

            is Error ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    MobileError(
                        onRetry = { onEvent(LoadData) },
                    )
                }

            is Loaded ->
                MobileWorkDetailsList(
                    work = state.work,
                    backgroundColor = backgroundColor,
                    cardBackgroundColor = cardBackgroundColor,
                    content = current,
                    listState = listState,
                    onEvent = onEvent,
                    onListViewportTopChanged = onListViewportTopChanged,
                    onHeaderTitleBottomChanged = onHeaderTitleBottomChanged,
                    onBackdropColorChanged = onBackdropColorChanged,
                )
        }
    }
}

@Composable
private fun MobileWorkDetailsList(
    work: WorkViewModel,
    backgroundColor: Color,
    cardBackgroundColor: Color,
    content: Loaded,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onEvent: (WorkDetailsEvent) -> Unit,
    onListViewportTopChanged: (Float) -> Unit,
    onHeaderTitleBottomChanged: (Float) -> Unit,
    onBackdropColorChanged: (String, Color) -> Unit,
) {
    val hasMetadata = content.contents
        .filterIsInstance<Header>()
        .firstOrNull()
        ?.metadata
        ?.hasContent == true

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                onListViewportTopChanged(coordinates.boundsInRoot().top)
            },
    ) {
        content.contents.forEachIndexed { index, section ->
            when (section) {
                is Header ->
                    item(key = section.id, contentType = LazyContentTypes.HEADER) {
                        MobileWorkDetailsHeader(
                            work = work,
                            backgroundColor = backgroundColor,
                            crew = section.crew,
                            providers = section.watchProviders,
                            onTitleBottomChanged = onHeaderTitleBottomChanged,
                            onBackdropColorChanged = onBackdropColorChanged,
                        )
                    }

                is Videos ->
                    item(key = section.id, contentType = LazyContentTypes.VIDEOS) {
                        MobileVideoRow(
                            videos = section.videos,
                            cardColor = cardBackgroundColor,
                            onClick = { onEvent(VideoClicked(it)) },
                        )
                    }

                is Casts ->
                    item(key = section.id, contentType = LazyContentTypes.CASTS) {
                        MobileCastRow(
                            casts = section.casts,
                            cardColor = cardBackgroundColor,
                            onClick = { onEvent(CastClicked(it)) },
                        )
                    }

                is RecommendedWorks ->
                    item(key = section.id, contentType = LazyContentTypes.RECOMMENDED) {
                        MobileWorkRow(
                            title = stringResource(R.string.recommended),
                            works = section.recommended,
                            onWorkClick = { onEvent(WorkClicked(it)) },
                            pagination = section.page,
                            onLoadMore = { onEvent(LoadMoreRecommendations) },
                            cardColor = cardBackgroundColor,
                        )
                    }

                is SimilarWorks ->
                    item(key = section.id, contentType = LazyContentTypes.SIMILAR) {
                        MobileWorkRow(
                            title = stringResource(R.string.similar),
                            works = section.similar,
                            onWorkClick = { onEvent(WorkClicked(it)) },
                            pagination = section.page,
                            onLoadMore = { onEvent(LoadMoreSimilar) },
                            cardColor = cardBackgroundColor,
                        )
                    }

                is Reviews ->
                    item(key = section.id, contentType = LazyContentTypes.REVIEWS) {
                        MobileReviewRow(
                            reviews = section.reviews,
                            page = section.page,
                            onLoadMore = { onEvent(LoadMoreReviews) },
                            cardColor = cardBackgroundColor,
                        )
                    }
            }

            if (index < content.contents.lastIndex || hasMetadata) {
                item(key = "divider-${section.id}", contentType = LazyContentTypes.DIVIDER) {
                    HorizontalDivider(
                        color = BESTVTheme.colors.mobileNavigationSelectedSurface,
                        thickness = BESTVTheme.scale.s005,
                        modifier = Modifier.padding(
                            start = BESTVTheme.scale.s080,
                            end = BESTVTheme.scale.s080,
                            top = BESTVTheme.scale.s120,
                        ),
                    )
                }
            }
        }

        content.error?.let { error ->
            item(key = "error", contentType = LazyContentTypes.ERROR) {
                Card(
                    modifier = Modifier
                        .padding(BESTVTheme.scale.s080),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = error.message,
                            modifier = Modifier
                                .weight(1f)
                                .padding(BESTVTheme.scale.s080),
                        )
                        IconButton(onClick = { onEvent(DismissError) }) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    }
                }
            }
        }

        content.contents.filterIsInstance<Header>().firstOrNull()?.metadata?.takeIf { it.hasContent }?.let { metadata ->
            item(key = "work-metadata", contentType = LazyContentTypes.METADATA) {
                WorkMetadataDetails(
                    metadata = metadata,
                    modifier = Modifier.padding(
                        horizontal = BESTVTheme.scale.s080,
                        vertical = BESTVTheme.scale.s120,
                    ),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MobileWorkDetailsContentPreview() {
    BESTVTheme {
        MobileWorkDetailsContent(
            state = WorkDetailsState(
                work = mobilePreviewWork,
                state = Loaded(
                    indexOfContentToScroll = null,
                    contents = listOf(
                        Header(
                            actions = listOf(
                                ActionButton.SaveWork(isFavorite = false),
                                ActionButton.ScrollToVideos,
                            ),
                            watchProviders = null,
                            metadata = null,
                            crew = emptyList(),
                        ),
                        Videos(videos = listOf(VideoViewModel(id = "trailer", name = "Trailer"))),
                        Casts(
                            casts = listOf(
                                CastViewModel(1, "Alex Morgan", "Lead", "", "", "", "", ""),
                            ),
                        ),
                        RecommendedWorks(
                            recommended = listOf(mobilePreviewWork),
                            page = com.pimenta.bestv.presentation.model
                                .PaginationState(),
                        ),
                        Reviews(
                            reviews = listOf(
                                ReviewViewModel(
                                    id = "review",
                                    author = "A. Reviewer",
                                    content = "A sample review shown in a mobile work detail preview.",
                                ),
                            ),
                            page = com.pimenta.bestv.presentation.model
                                .PaginationState(),
                        ),
                    ),
                    error = null,
                ),
                isMobileDevice = true,
            ),
            backgroundColor = BESTVTheme.colors.black,
            cardBackgroundColor = BESTVTheme.colors.reviewCardSurface,
            listState = rememberLazyListState(),
            contentPadding = PaddingValues(),
            onEvent = {},
            onListViewportTopChanged = {},
            onHeaderTitleBottomChanged = {},
            onBackdropColorChanged = { _, _ -> },
        )
    }
}
