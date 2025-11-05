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

package com.pimenta.bestv.search.presentation.ui.compose

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.ui.compose.ErrorScreen
import com.pimenta.bestv.presentation.ui.compose.WorkBackground
import com.pimenta.bestv.presentation.ui.compose.WorksRow
import com.pimenta.bestv.search.presentation.model.SearchEffect.OpenWorkDetails
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

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    openIntent: (Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is OpenWorkDetails -> openIntent(effect.intent)
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        WorkBackground(backdropUrl = (state.state as? Loaded)?.selectedWork?.backdropUrl)

        Column(modifier = modifier.fillMaxSize()) {
            SearchBar(
                query = state.query,
                onQueryChange = { viewModel.handleEvent(SearchQueryChanged(it)) },
                onQuerySubmit = { viewModel.handleEvent(SearchQuerySubmitted(it)) },
                onClear = { viewModel.handleEvent(ClearSearch) }
            )

            SearchContent(
                state = state,
                onWorkClick = { viewModel.handleEvent(WorkClicked(it)) },
                onWorkSelected = { viewModel.handleEvent(WorkItemSelected(it)) },
                onLoadMoreMovies = { viewModel.handleEvent(LoadMoreMovies) },
                onLoadMoreTvShows = { viewModel.handleEvent(LoadMoreTvShows) },
                onRetryClicked = { viewModel.handleEvent(SearchQuerySubmitted(state.query)) },
                modifier = Modifier.weight(1f)
            )
        }

        if (state.isSearching) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun SearchContent(
    state: SearchState,
    onWorkClick: (WorkViewModel) -> Unit,
    onWorkSelected: (WorkViewModel?) -> Unit,
    onLoadMoreMovies: () -> Unit,
    onLoadMoreTvShows: () -> Unit,
    onRetryClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        when (val currentState = state.state) {
            is Empty -> {
                NoResultsView(
                    modifier = Modifier.align(Alignment.TopStart)
                )
            }

            is Error -> {
                ErrorScreen(
                    onRetryClick = onRetryClicked,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is Loaded -> {
                if (currentState.hasResults) {
                    SearchResultsContent(
                        contents = currentState.contents,
                        onWorkClick = onWorkClick,
                        onWorkSelected = onWorkSelected,
                        onLoadMoreMovies = onLoadMoreMovies,
                        onLoadMoreTvShows = onLoadMoreTvShows,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    NoResultsView(
                        modifier = Modifier.align(Alignment.TopStart)
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchResultsContent(
    contents: List<Content>,
    onWorkClick: (WorkViewModel) -> Unit,
    onWorkSelected: (WorkViewModel?) -> Unit,
    onLoadMoreMovies: () -> Unit,
    onLoadMoreTvShows: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(top = 16.dp)
    ) {
        itemsIndexed(
            items = contents,
            key = { index, content -> "${content.query}_${index}" }
        ) { index, content ->
            when (content) {
                is Movies -> WorksRow(
                    title = "Movies",
                    works = content.movies,
                    onWorkClick = onWorkClick,
                    onWorkFocused = onWorkSelected,
                    isLoadingMore = content.page.isLoadingMore,
                    onLoadMore = onLoadMoreMovies,
                )

                is TvShows -> WorksRow(
                    title = "TV Shows",
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
private fun NoResultsView(
    modifier: Modifier = Modifier
) {
    Text(
        text = "No results",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = modifier.padding(start = 48.dp, top = 36.dp)
    )
}
