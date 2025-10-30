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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.MaterialTheme
import kotlinx.coroutines.launch
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.ui.compose.ErrorScreen
import com.pimenta.bestv.presentation.ui.compose.WorkBackground
import com.pimenta.bestv.presentation.ui.compose.WorksRow
import com.pimenta.bestv.workdetail.presentation.model.VideoViewModel
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEffect
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEffect.OpenIntent
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.ActionButtonClicked
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.CastClicked
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.LoadData
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.LoadMoreRecommendations
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.LoadMoreReviews
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.LoadMoreSimilar
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.VideoClicked
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.WorkClicked
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.ActionButton
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.ActionButton.SaveWork
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
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        WorkBackground(
            backdropUrl = state.work.backdropUrl
        )

        when (val currentState = state.state) {
            is Loading -> {
                CircularProgressIndicator(
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
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 48.dp)
                ) {
                    items(
                        items = currentState.contents,
                        key = { it.hashCode() }
                    ) { content ->
                        when (content) {
                            is Header -> WorkDetailsHeader(
                                work = state.work,
                                actions = content.actions,
                                actionClicked = actionClicked
                            )
                            is Casts -> CastRow(
                                casts = content.casts,
                                onCastClick = onCastClick,
                            )
                            is RecommendedWorks -> WorksRow(
                                title = "Recommended",
                                works = content.recommended,
                                onWorkClick = onWorkClick,
                                isLoadingMore = content.page.isLoadingMore,
                                onLoadMore = onLoadMoreRecommendations,
                            )
                            is Reviews -> ReviewRow(
                                reviews = content.reviews,
                                isLoadingMore = content.page.isLoadingMore,
                                onLoadMore = onLoadMoreReviews,
                            )
                            is SimilarWorks -> WorksRow(
                                title = "Similar",
                                works = content.similar,
                                onWorkClick = onWorkClick,
                                isLoadingMore = content.page.isLoadingMore,
                                onLoadMore = onLoadMoreSimilar,
                            )
                            is Videos -> VideoRow(
                                videos = content.videos,
                                onVideoClick = onVideoClick,
                            )
                        }
                    }
                }

                currentState.indexOfContentToScroll?.let {
                    LaunchedEffect(it) {
                        //listState.animateScrollToItem(it)
                    }
                }
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
                    title = "The Dark Knight",
                    originalTitle = "The Dark Knight",
                    type = WorkType.MOVIE,
                    posterUrl = null,
                    backdropUrl = null,
                    source = "TMDB"
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
            onRetryClicked = {}
        )
    }
}
