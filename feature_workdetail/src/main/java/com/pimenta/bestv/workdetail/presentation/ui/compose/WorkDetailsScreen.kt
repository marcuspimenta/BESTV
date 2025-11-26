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

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.MaterialTheme
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.ui.compose.ErrorBanner
import com.pimenta.bestv.presentation.ui.compose.ErrorScreen
import com.pimenta.bestv.presentation.ui.compose.Loading
import com.pimenta.bestv.presentation.ui.compose.BackgroundScreen
import com.pimenta.bestv.presentation.ui.compose.WorksRow
import com.pimenta.bestv.workdetail.presentation.model.VideoViewModel
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEffect.OpenIntent
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
import com.pimenta.bestv.workdetail.presentation.model.ErrorType
import com.pimenta.bestv.workdetail.presentation.viewmodel.WorkDetailsViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun WorkDetailsScreen(
    viewModel: WorkDetailsViewModel,
    openIntent: (Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is OpenIntent -> openIntent(effect.intent)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.handleEvent(LoadData)
    }

    WorkDetailsContent(
        state = state,
        actionClicked = { viewModel.handleEvent(ActionButtonClicked(it)) },
        onVideoClick = { viewModel.handleEvent(VideoClicked(it)) },
        onCastClick = { viewModel.handleEvent(CastClicked(it)) },
        onWorkClick = { viewModel.handleEvent(WorkClicked(it)) },
        onLoadMoreReviews = { viewModel.handleEvent(LoadMoreReviews) },
        onLoadMoreRecommendations = { viewModel.handleEvent(LoadMoreRecommendations) },
        onLoadMoreSimilar = { viewModel.handleEvent(LoadMoreSimilar) },
        onRetryClicked = { viewModel.handleEvent(LoadData) },
        onDismissError = { viewModel.handleEvent(DismissError) },
        onClearScrollIndex = { viewModel.handleEvent(ClearScrollIndex) },
        modifier = modifier
    )
}

@Composable
private fun WorkDetailsContent(
    state: WorkDetailsState,
    actionClicked: (ActionButton) -> Unit,
    onVideoClick: (VideoViewModel) -> Unit,
    onCastClick: (CastViewModel) -> Unit,
    onWorkClick: (WorkViewModel) -> Unit,
    onLoadMoreReviews: () -> Unit,
    onLoadMoreRecommendations: () -> Unit,
    onLoadMoreSimilar: () -> Unit,
    onRetryClicked: () -> Unit,
    onDismissError: () -> Unit,
    onClearScrollIndex: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        BackgroundScreen(
            backdropUrl = state.work.backdropUrl
        )

        when (val currentState = state.state) {
            is Loading -> {
                Loading(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is Error -> {
                ErrorScreen(
                    onRetryClick = onRetryClicked,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is Loaded -> {
                LoadedWorkDetails(
                    work = state.work,
                    loadedState = currentState,
                    actionClicked = actionClicked,
                    onVideoClick = onVideoClick,
                    onCastClick = onCastClick,
                    onWorkClick = onWorkClick,
                    onLoadMoreReviews = onLoadMoreReviews,
                    onLoadMoreRecommendations = onLoadMoreRecommendations,
                    onLoadMoreSimilar = onLoadMoreSimilar,
                    onDismissError = onDismissError,
                    onClearScrollIndex = onClearScrollIndex,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun LoadedWorkDetails(
    work: WorkViewModel,
    loadedState: Loaded,
    actionClicked: (ActionButton) -> Unit,
    onVideoClick: (VideoViewModel) -> Unit,
    onCastClick: (CastViewModel) -> Unit,
    onWorkClick: (WorkViewModel) -> Unit,
    onLoadMoreReviews: () -> Unit,
    onLoadMoreRecommendations: () -> Unit,
    onLoadMoreSimilar: () -> Unit,
    onDismissError: () -> Unit,
    onClearScrollIndex: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val focusRequesters = remember(loadedState.contents.size) {
        loadedState.contents.map { FocusRequester() }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = listState,
        ) {
            itemsIndexed(
                items = loadedState.contents,
                key = { _, content -> content.id }
            ) { index, content ->
                when (content) {
                    is Header -> WorkDetailsHeader(
                        work = work,
                        actions = content.actions,
                        actionClicked = actionClicked,
                        modifier = Modifier.focusRequester(focusRequesters[index])
                    )

                    is Casts -> CastRow(
                        casts = content.casts,
                        onCastClick = onCastClick,
                        modifier = Modifier.focusRequester(focusRequesters[index])
                    )

                    is RecommendedWorks -> WorksRow(
                        title = "Recommended",
                        works = content.recommended,
                        onWorkClick = onWorkClick,
                        isLoadingMore = content.page.isLoadingMore,
                        onLoadMore = onLoadMoreRecommendations,
                        modifier = Modifier.focusRequester(focusRequesters[index])
                    )

                    is Reviews -> ReviewRow(
                        reviews = content.reviews,
                        isLoadingMore = content.page.isLoadingMore,
                        onLoadMore = onLoadMoreReviews,
                        modifier = Modifier.focusRequester(focusRequesters[index])
                    )

                    is SimilarWorks -> WorksRow(
                        title = "Similar",
                        works = content.similar,
                        onWorkClick = onWorkClick,
                        isLoadingMore = content.page.isLoadingMore,
                        onLoadMore = onLoadMoreSimilar,
                        modifier = Modifier.focusRequester(focusRequesters[index])
                    )

                    is Videos -> VideoRow(
                        videos = content.videos,
                        onVideoClick = onVideoClick,
                        modifier = Modifier.focusRequester(focusRequesters[index])
                    )
                }
            }
        }

        ErrorBanner(
            errorMessage = loadedState.error?.message,
            onDismiss = onDismissError,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        LaunchedEffect(Unit) {
            focusRequesters.getOrNull(0)?.requestFocus()
        }

        loadedState.indexOfContentToScroll?.let { targetIndex ->
            LaunchedEffect(targetIndex) {
                listState.animateScrollToItem(targetIndex)
                focusRequesters.getOrNull(targetIndex)?.requestFocus()
                onClearScrollIndex()
            }
        }
    }
}

@Preview
@Composable
private fun WorkDetailsScreenPreview() {
    MaterialTheme {
        WorkDetailsContent(
            state = WorkDetailsState(
                work = WorkViewModel(
                    id = 1,
                    overview = "Overview",
                    title = "The Dark Knight",
                    originalTitle = "The Dark Knight",
                    type = WorkType.MOVIE,
                    posterUrl = "",
                    source = "TMDB",
                    originalLanguage = "",
                    backdropUrl = "",
                    releaseDate = "",
                    voteAverage = 0f,
                    isFavorite = false
                ),
                state = Loaded(
                    contents = emptyList()
                )
            ),
            actionClicked = {},
            onVideoClick = {},
            onCastClick = {},
            onWorkClick = {},
            onLoadMoreReviews = {},
            onLoadMoreRecommendations = {},
            onLoadMoreSimilar = {},
            onRetryClicked = {},
            onDismissError = {},
            onClearScrollIndex = {}
        )
    }
}

@Preview
@Composable
private fun WorkDetailsScreenWithErrorPreview() {
    MaterialTheme {
        WorkDetailsContent(
            state = WorkDetailsState(
                work = WorkViewModel(
                    id = 1,
                    overview = "Overview",
                    title = "The Dark Knight",
                    originalTitle = "The Dark Knight",
                    type = WorkType.MOVIE,
                    posterUrl = "",
                    source = "TMDB",
                    originalLanguage = "",
                    backdropUrl = "",
                    releaseDate = "",
                    voteAverage = 0f,
                    isFavorite = false
                ),
                state = Loaded(
                    contents = emptyList(),
                    error = ErrorType.FavoriteError
                )
            ),
            actionClicked = {},
            onVideoClick = {},
            onCastClick = {},
            onWorkClick = {},
            onLoadMoreReviews = {},
            onLoadMoreRecommendations = {},
            onLoadMoreSimilar = {},
            onRetryClicked = {},
            onDismissError = {},
            onClearScrollIndex = {}
        )
    }
}
