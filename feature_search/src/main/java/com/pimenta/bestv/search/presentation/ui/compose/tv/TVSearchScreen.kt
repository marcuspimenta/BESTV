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

package com.pimenta.bestv.search.presentation.ui.compose.tv

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.presentation.ui.compose.tv.TVBackgroundScreen
import com.pimenta.bestv.presentation.ui.compose.tv.TVErrorScreen
import com.pimenta.bestv.presentation.ui.compose.tv.TVLoading
import com.pimenta.bestv.presentation.ui.compose.tv.TVWorksRow
import com.pimenta.bestv.search.R
import com.pimenta.bestv.search.presentation.model.SearchEffect.Navigate
import com.pimenta.bestv.search.presentation.model.SearchEvent.ClearSearch
import com.pimenta.bestv.search.presentation.model.SearchEvent.LoadMoreMovies
import com.pimenta.bestv.search.presentation.model.SearchEvent.LoadMoreTvShows
import com.pimenta.bestv.search.presentation.model.SearchEvent.SearchQueryChanged
import com.pimenta.bestv.search.presentation.model.SearchEvent.SearchQuerySubmitted
import com.pimenta.bestv.search.presentation.model.SearchEvent.WorkClicked
import com.pimenta.bestv.search.presentation.model.SearchEvent.WorkItemSelected
import com.pimenta.bestv.search.presentation.model.SearchState
import com.pimenta.bestv.search.presentation.model.SearchState.Content
import com.pimenta.bestv.search.presentation.model.SearchState.Content.Movies
import com.pimenta.bestv.search.presentation.model.SearchState.Content.TvShows
import com.pimenta.bestv.search.presentation.model.SearchState.State.Empty
import com.pimenta.bestv.search.presentation.model.SearchState.State.Error
import com.pimenta.bestv.search.presentation.model.SearchState.State.Loaded
import com.pimenta.bestv.search.presentation.viewmodel.SearchViewModel
import kotlinx.coroutines.flow.collectLatest
import com.pimenta.bestv.presentation.R as PresentationR

@Composable
fun TVSearchScreen(
    viewModel: SearchViewModel,
    openIntent: (Intent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is Navigate -> openIntent(effect.intent)
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        TVBackgroundScreen(backdropUrl = (state.state as? Loaded)?.selectedWork?.backdropUrl)

        Column(modifier = modifier.fillMaxSize()) {
            TVSearchBar(
                query = state.query,
                placeholder = stringResource(R.string.search_placeholder),
                onQueryChange = { viewModel.handleEvent(SearchQueryChanged(it)) },
                onQuerySubmit = { viewModel.handleEvent(SearchQuerySubmitted(it)) },
                onClear = { viewModel.handleEvent(ClearSearch) },
            )

            TVSearchContent(
                state = state,
                onWorkClick = { viewModel.handleEvent(WorkClicked(it)) },
                onWorkSelected = { viewModel.handleEvent(WorkItemSelected(it)) },
                onLoadMoreMovies = { viewModel.handleEvent(LoadMoreMovies) },
                onLoadMoreTvShows = { viewModel.handleEvent(LoadMoreTvShows) },
                onRetryClicked = { viewModel.handleEvent(SearchQuerySubmitted(state.query)) },
                modifier =
                    Modifier
                        .weight(1f),
            )
        }

        if (state.isSearching) {
            TVLoading(
                modifier =
                    Modifier
                        .align(Alignment.Center),
            )
        }
    }
}

@Composable
private fun TVSearchContent(
    state: SearchState,
    onWorkClick: (WorkViewModel) -> Unit,
    onWorkSelected: (WorkViewModel?) -> Unit,
    onLoadMoreMovies: () -> Unit,
    onLoadMoreTvShows: () -> Unit,
    onRetryClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        when (val currentState = state.state) {
            is Empty -> {
                TVNoResultsView(
                    modifier =
                        Modifier
                            .align(Alignment.TopStart),
                )
            }

            is Error -> {
                TVErrorScreen(
                    onRetryClick = onRetryClicked,
                    modifier =
                        Modifier
                            .align(Alignment.Center),
                )
            }

            is Loaded -> {
                if (currentState.hasResults) {
                    TVSearchResultsContent(
                        contents = currentState.contents,
                        onWorkClick = onWorkClick,
                        onWorkSelected = onWorkSelected,
                        onLoadMoreMovies = onLoadMoreMovies,
                        onLoadMoreTvShows = onLoadMoreTvShows,
                        modifier =
                            Modifier
                                .fillMaxSize(),
                    )
                } else {
                    TVNoResultsView(
                        modifier =
                            Modifier
                                .align(Alignment.TopStart),
                    )
                }
            }
        }
    }
}

@Composable
private fun TVSearchResultsContent(
    contents: List<Content>,
    onWorkClick: (WorkViewModel) -> Unit,
    onWorkSelected: (WorkViewModel?) -> Unit,
    onLoadMoreMovies: () -> Unit,
    onLoadMoreTvShows: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.padding(top = BESTVTheme.scale.s080),
    ) {
        itemsIndexed(
            items = contents,
            key = { index, content -> "${content.query}_$index" },
        ) { index, content ->
            when (content) {
                is Movies ->
                    TVWorksRow(
                        title = stringResource(PresentationR.string.movies_title),
                        works = content.movies,
                        onWorkClick = onWorkClick,
                        onWorkFocused = onWorkSelected,
                        isLoadingMore = content.page.isLoadingMore,
                        onLoadMore = onLoadMoreMovies,
                    )

                is TvShows ->
                    TVWorksRow(
                        title = stringResource(PresentationR.string.tv_shows_title),
                        works = content.tvShows,
                        onWorkClick = onWorkClick,
                        onWorkFocused = onWorkSelected,
                        isLoadingMore = content.page.isLoadingMore,
                        onLoadMore = onLoadMoreTvShows,
                    )
            }
        }
    }
}

@Composable
private fun TVNoResultsView(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.no_results),
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = BESTVTheme.colors.white,
        modifier =
            modifier.padding(
                start = BESTVTheme.scale.s240,
                top = BESTVTheme.scale.s180,
            ),
    )
}
