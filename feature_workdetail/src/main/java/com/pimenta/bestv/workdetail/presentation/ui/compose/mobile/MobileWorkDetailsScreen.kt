/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workdetail.presentation.ui.compose.mobile

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.presentation.ui.compose.LazyRowPagination
import com.pimenta.bestv.presentation.ui.compose.mobile.MobileError
import com.pimenta.bestv.presentation.ui.compose.mobile.MobileLoading
import com.pimenta.bestv.presentation.ui.compose.mobile.MobileSectionTitle
import com.pimenta.bestv.presentation.ui.compose.mobile.MobileWorkRow
import com.pimenta.bestv.workdetail.R
import com.pimenta.bestv.workdetail.presentation.model.ReviewViewModel
import com.pimenta.bestv.workdetail.presentation.model.VideoViewModel
import com.pimenta.bestv.workdetail.presentation.model.WatchProvidersViewModel
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEffect.Navigate
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.ActionButtonClicked
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.CastClicked
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.ClearScrollIndex
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
import com.pimenta.bestv.workdetail.presentation.viewmodel.WorkDetailsViewModel
import kotlinx.coroutines.flow.collectLatest
import com.pimenta.bestv.presentation.R as PresentationR

@Composable
fun MobileWorkDetailsScreen(
    viewModel: WorkDetailsViewModel,
    openIntent: (Intent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    var listViewportTop by remember { mutableFloatStateOf(Float.NaN) }
    var headerTitleBottom by remember { mutableFloatStateOf(Float.NaN) }
    val showCollapsedTitle = listViewportTop.isFinite() &&
        headerTitleBottom.isFinite() &&
        headerTitleBottom <= listViewportTop

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            if (effect is Navigate) openIntent(effect.intent)
        }
    }
    LaunchedEffect(Unit) { viewModel.handleEvent(LoadData) }
    val loaded = state.state as? Loaded
    LaunchedEffect(loaded?.indexOfContentToScroll) {
        loaded?.indexOfContentToScroll?.let { index ->
            listState.animateScrollToItem(
                index.coerceAtMost((loaded.contents.size - 1).coerceAtLeast(0)),
            )
            viewModel.handleEvent(ClearScrollIndex)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BESTVTheme.colors.black,
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(PresentationR.string.back),
                    )
                }
                AnimatedVisibility(
                    visible = showCollapsedTitle,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                ) {
                    Text(
                        text = state.work.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = BESTVTheme.colors.white,
                        maxLines = 1,
                    )
                }
            }
        },
    ) { contentPadding ->
        MobileWorkDetailsContent(
            state = state,
            listState = listState,
            contentPadding = contentPadding,
            onEvent = viewModel::handleEvent,
            onListViewportTopChanged = { listViewportTop = it },
            onHeaderTitleBottomChanged = { headerTitleBottom = it },
        )
    }
}

@Composable
private fun MobileWorkDetailsContent(
    state: WorkDetailsState,
    listState: androidx.compose.foundation.lazy.LazyListState,
    contentPadding: PaddingValues,
    onEvent: (WorkDetailsEvent) -> Unit,
    onListViewportTopChanged: (Float) -> Unit,
    onHeaderTitleBottomChanged: (Float) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
    ) {
        when (val current = state.state) {
            is Loading ->
                Box(
                    Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    MobileLoading()
                }

            is Error ->
                Box(
                    Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    MobileError(onRetry = { onEvent(LoadData) })
                }

            is Loaded ->
                MobileWorkDetailsList(
                    work = state.work,
                    content = current,
                    listState = listState,
                    onEvent = onEvent,
                    onListViewportTopChanged = onListViewportTopChanged,
                    onHeaderTitleBottomChanged = onHeaderTitleBottomChanged,
                )
        }
    }
}

@Composable
private fun MobileWorkDetailsList(
    work: WorkViewModel,
    content: Loaded,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onEvent: (WorkDetailsEvent) -> Unit,
    onListViewportTopChanged: (Float) -> Unit,
    onHeaderTitleBottomChanged: (Float) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                onListViewportTopChanged(coordinates.boundsInRoot().top)
            },
    ) {
        content.contents.forEach { section ->
            when (section) {
                is Header -> {
                    item(key = section.id) {
                        MobileWorkHeader(work, onHeaderTitleBottomChanged)
                    }
                    stickyHeader(key = "work-actions") {
                        MobileWorkActions(section.actions) { onEvent(ActionButtonClicked(it)) }
                    }
                    section.watchProviders?.let { providers ->
                        item(key = "watch-providers") { MobileProviders(providers) }
                    }
                }

                is Videos ->
                    item(key = section.id) {
                        MobileVideos(section.videos) { onEvent(VideoClicked(it)) }
                    }

                is Casts ->
                    item(key = section.id) {
                        MobileCasts(section.casts) { onEvent(CastClicked(it)) }
                    }

                is RecommendedWorks ->
                    item(key = section.id) {
                        MobileWorkRow(
                            title = stringResource(R.string.recommended),
                            works = section.recommended,
                            onWorkClick = { onEvent(WorkClicked(it)) },
                            pagination = section.page,
                            onLoadMore = { onEvent(LoadMoreRecommendations) },
                        )
                    }

                is SimilarWorks ->
                    item(key = section.id) {
                        MobileWorkRow(
                            title = stringResource(R.string.similar),
                            works = section.similar,
                            onWorkClick = { onEvent(WorkClicked(it)) },
                            pagination = section.page,
                            onLoadMore = { onEvent(LoadMoreSimilar) },
                        )
                    }

                is Reviews ->
                    item(key = section.id) {
                        MobileReviews(section.reviews, section.page) { onEvent(LoadMoreReviews) }
                    }
            }
        }

        content.error?.let { error ->
            item(key = "error") {
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
    }
}

@Composable
private fun MobileWorkActions(
    actions: List<ActionButton>,
    onAction: (ActionButton) -> Unit,
) {
    if (actions.isEmpty()) return
    LazyRow(
        contentPadding = PaddingValues(
            horizontal = BESTVTheme.scale.s080,
        ),
        horizontalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s060),
        modifier = Modifier
            .background(BESTVTheme.colors.black)
            .padding(BESTVTheme.scale.s040),
    ) {
        items(actions, key = { it.id }) { action ->
            Button(onClick = { onAction(action) }) { Text(action.title) }
        }
    }
}

@Composable
private fun MobileWorkHeader(
    work: WorkViewModel,
    onTitleBottomChanged: (Float) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s080),
    ) {
        AsyncImage(
            model = work.backdropUrl,
            contentDescription = work.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(BESTVTheme.scale.s1000),
        )
        Column(
            modifier = Modifier
                .padding(
                    start = BESTVTheme.scale.s080,
                    end = BESTVTheme.scale.s080,
                    bottom = BESTVTheme.scale.s080,
                ),
        ) {
            Text(
                text = work.title,
                style = MaterialTheme.typography.headlineMedium,
                color = BESTVTheme.colors.white,
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        onTitleBottomChanged(coordinates.boundsInRoot().bottom)
                    },
            )
            Text(
                text = listOf(
                    work.releaseDate,
                    "★ ${work.voteAverage}",
                    work.source,
                ).filter(String::isNotBlank)
                    .joinToString(" · "),
                style = MaterialTheme.typography.bodyMedium,
                color = BESTVTheme.colors.secondaryText,
                modifier = Modifier
                    .padding(top = BESTVTheme.scale.s040),
            )
            Text(
                text = work.overview,
                style = MaterialTheme.typography.bodyMedium,
                color = BESTVTheme.colors.biographyText,
                modifier = Modifier
                    .padding(top = BESTVTheme.scale.s080),
            )
        }
    }
}

@Composable
private fun MobileProviders(providers: WatchProvidersViewModel) {
    if (!providers.hasAnyProvider) return

    MobileSectionTitle(stringResource(R.string.where_to_watch))
    LazyRow(
        contentPadding = PaddingValues(
            horizontal = BESTVTheme.scale.s080,
        ),
        horizontalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s060),
    ) {
        items(providers.providers, key = { it.id }) { provider ->
            AsyncImage(
                model = provider.logoUrl,
                contentDescription = provider.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(BESTVTheme.scale.s200)
                    .clip(RoundedCornerShape(BESTVTheme.scale.s040)),
            )
        }
    }
}

@Composable
private fun MobileVideos(
    videos: List<VideoViewModel>,
    onClick: (VideoViewModel) -> Unit,
) {
    MobileSectionTitle(stringResource(R.string.videos))
    LazyRow(
        contentPadding = PaddingValues(
            horizontal = BESTVTheme.scale.s080,
        ),
        horizontalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s080),
    ) {
        items(videos, key = { it.id ?: it.hashCode() }) { video ->
            Card(
                onClick = { onClick(video) },
                modifier = Modifier
                    .width(BESTVTheme.scale.s1000),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(BESTVTheme.scale.s600),
                ) {
                    AsyncImage(
                        model = video.thumbnailUrl,
                        contentDescription = stringResource(R.string.play_video),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize(),
                    )
                    Surface(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(BESTVTheme.scale.s320),
                        shape = CircleShape,
                        color = BESTVTheme.colors.imageScrim,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = stringResource(R.string.play_video),
                            tint = BESTVTheme.colors.white,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(BESTVTheme.scale.s060),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MobileCasts(
    casts: List<CastViewModel>,
    onClick: (CastViewModel) -> Unit,
) {
    MobileSectionTitle(stringResource(R.string.cast))
    LazyRow(
        contentPadding = PaddingValues(
            horizontal = BESTVTheme.scale.s080,
        ),
        horizontalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s080),
    ) {
        items(casts, key = { it.id }) { cast ->
            Card(
                onClick = { onClick(cast) },
                modifier = Modifier
                    .width(BESTVTheme.scale.s500),
            ) {
                AsyncImage(
                    model = cast.thumbnailUrl,
                    contentDescription = cast.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(BESTVTheme.scale.s750),
                )
            }
        }
    }
}

@Composable
private fun MobileReviews(
    reviews: List<ReviewViewModel>,
    page: com.pimenta.bestv.presentation.model.PaginationState,
    onLoadMore: () -> Unit,
) {
    MobileSectionTitle(stringResource(R.string.reviews))
    val listState = rememberLazyListState()
    LazyRow(
        state = listState,
        contentPadding = PaddingValues(
            horizontal = BESTVTheme.scale.s080,
        ),
        horizontalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s080),
    ) {
        items(reviews, key = { it.id ?: it.hashCode() }) { review ->
            Card(
                modifier = Modifier
                    .width(BESTVTheme.scale.s1250),
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

private val mobilePreviewWork =
    WorkViewModel(
        id = 1,
        originalLanguage = "en",
        overview = "An adventurous story about an unexpected journey across a changing world.",
        source = "Movie",
        backdropUrl = "",
        posterUrl = "",
        title = "Preview Work",
        originalTitle = "Preview Work",
        releaseDate = "2025",
        type = WorkType.MOVIE,
        voteAverage = 8.2f,
    )

@Preview(showBackground = true)
@Composable
private fun MobileWorkHeaderPreview() {
    BESTVTheme {
        MobileWorkHeader(mobilePreviewWork, onTitleBottomChanged = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun MobileWorkActionsPreview() {
    BESTVTheme {
        MobileWorkActions(
            actions = listOf(
                ActionButton.SaveWork(isFavorite = false),
                ActionButton.ScrollToVideos,
                ActionButton.ScrollToCasts,
            ),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MobileProvidersPreview() {
    BESTVTheme {
        MobileProviders(
            WatchProvidersViewModel(
                tmdbLink = null,
                providers = listOf(
                    com.pimenta.bestv.workdetail.presentation.model.WatchProviderViewModel(
                        1,
                        "Stream",
                        null,
                    ),
                    com.pimenta.bestv.workdetail.presentation.model.WatchProviderViewModel(
                        2,
                        "Cinema",
                        null,
                    ),
                ),
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MobileVideosPreview() {
    BESTVTheme {
        MobileVideos(
            videos = listOf(
                VideoViewModel(id = "1", name = "Trailer"),
                VideoViewModel(id = "2", name = "Behind the scenes"),
            ),
            onClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MobileCastsPreview() {
    BESTVTheme {
        MobileCasts(
            casts = listOf(
                CastViewModel(1, "Alex Morgan", "Lead", "", "", "", "", ""),
                CastViewModel(2, "Sam Taylor", "Friend", "", "", "", "", ""),
            ),
            onClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MobileReviewsPreview() {
    BESTVTheme {
        MobileReviews(
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

@Preview(showBackground = true)
@Composable
private fun MobileWorkDetailsContentPreview() {
    BESTVTheme {
        MobileWorkDetailsContent(
            state = WorkDetailsState(
                work = mobilePreviewWork,
                state = Loaded(
                    contents = listOf(
                        Header(
                            actions = listOf(
                                ActionButton.SaveWork(isFavorite = false),
                                ActionButton.ScrollToVideos,
                            ),
                            watchProviders = null,
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
                ),
            ),
            listState = rememberLazyListState(),
            contentPadding = PaddingValues(),
            onEvent = {},
            onListViewportTopChanged = {},
            onHeaderTitleBottomChanged = {},
        )
    }
}
