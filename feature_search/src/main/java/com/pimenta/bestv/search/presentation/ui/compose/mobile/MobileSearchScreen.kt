/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.search.presentation.ui.compose.mobile

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.presentation.ui.compose.mobile.MobileError
import com.pimenta.bestv.presentation.ui.compose.mobile.MobileLoading
import com.pimenta.bestv.presentation.ui.compose.mobile.MobileWorkRow
import com.pimenta.bestv.search.R
import com.pimenta.bestv.search.presentation.model.SearchEffect.Navigate
import com.pimenta.bestv.search.presentation.model.SearchEvent.ClearSearch
import com.pimenta.bestv.search.presentation.model.SearchEvent.LoadMoreMovies
import com.pimenta.bestv.search.presentation.model.SearchEvent.LoadMoreTvShows
import com.pimenta.bestv.search.presentation.model.SearchEvent.SearchQueryChanged
import com.pimenta.bestv.search.presentation.model.SearchEvent.SearchQuerySubmitted
import com.pimenta.bestv.search.presentation.model.SearchEvent.WorkClicked
import com.pimenta.bestv.search.presentation.model.SearchState
import com.pimenta.bestv.search.presentation.model.SearchState.Content.Movies
import com.pimenta.bestv.search.presentation.model.SearchState.Content.TvShows
import com.pimenta.bestv.search.presentation.model.SearchState.State.Empty
import com.pimenta.bestv.search.presentation.model.SearchState.State.Error
import com.pimenta.bestv.search.presentation.viewmodel.SearchViewModel
import kotlinx.coroutines.flow.collectLatest
import com.pimenta.bestv.presentation.R as PresentationR

@Composable
fun MobileSearchScreen(
    viewModel: SearchViewModel,
    openIntent: (Intent) -> Unit,
    onBack: () -> Unit,
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

    Scaffold(
        modifier = modifier.fillMaxSize().statusBarsPadding(),
        containerColor = BESTVTheme.colors.black,
        topBar = {
            MobileSearchTopBar(
                query = state.query,
                onBack = onBack,
                onQueryChanged = { viewModel.handleEvent(SearchQueryChanged(it)) },
                onClear = { viewModel.handleEvent(ClearSearch) },
                onSearch = { viewModel.handleEvent(SearchQuerySubmitted(state.query)) },
            )
        },
    ) { contentPadding ->
        MobileSearchContent(
            state = state,
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            onRetry = { viewModel.handleEvent(SearchQuerySubmitted(state.query)) },
            onWorkClick = { viewModel.handleEvent(WorkClicked(it)) },
            onLoadMoreMovies = { viewModel.handleEvent(LoadMoreMovies) },
            onLoadMoreTvShows = { viewModel.handleEvent(LoadMoreTvShows) },
        )
    }
}

@Composable
private fun MobileSearchTopBar(
    query: String,
    onBack: () -> Unit,
    onQueryChanged: (String) -> Unit,
    onClear: () -> Unit,
    onSearch: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = BESTVTheme.scale.s080,
                    vertical = BESTVTheme.scale.s040,
                ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(BESTVTheme.scale.s240),
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(PresentationR.string.back),
                tint = BESTVTheme.colors.white,
                modifier = Modifier.size(BESTVTheme.scale.s120),
            )
        }
        TextField(
            value = query,
            onValueChange = onQueryChanged,
            modifier = Modifier.weight(1f).height(BESTVTheme.scale.s360),
            placeholder = {
                Text(
                    text = stringResource(R.string.search_placeholder),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = onClear) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = stringResource(R.string.clear_search),
                        )
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() }),
            colors =
                TextFieldDefaults.colors(
                    focusedTextColor = BESTVTheme.colors.white,
                    unfocusedTextColor = BESTVTheme.colors.white,
                    cursorColor = BESTVTheme.colors.white,
                    focusedContainerColor = BESTVTheme.colors.transparent,
                    unfocusedContainerColor = BESTVTheme.colors.transparent,
                    focusedIndicatorColor = BESTVTheme.colors.transparent,
                    unfocusedIndicatorColor = BESTVTheme.colors.transparent,
                ),
        )
    }
}

@Composable
private fun MobileSearchContent(
    state: SearchState,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
    onWorkClick: (com.pimenta.bestv.model.presentation.model.WorkViewModel) -> Unit,
    onLoadMoreMovies: () -> Unit,
    onLoadMoreTvShows: () -> Unit,
) {
    Box(modifier = modifier) {
        when (val result = state.state) {
            is Empty ->
                Text(
                    text = stringResource(R.string.search_placeholder),
                    color = BESTVTheme.colors.secondaryText,
                    modifier = Modifier.align(Alignment.Center),
                )

            is Error ->
                MobileError(
                    onRetry = onRetry,
                    modifier = Modifier.align(Alignment.Center),
                )

            is SearchState.State.Loaded -> {
                if (!result.hasResults) {
                    Text(
                        text = stringResource(R.string.no_results),
                        color = BESTVTheme.colors.secondaryText,
                        modifier =
                            Modifier.padding(
                                start = BESTVTheme.scale.s080,
                                top = BESTVTheme.scale.s120,
                            ),
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s080),
                    ) {
                        items(result.contents, key = { it.query + it::class.simpleName }) { content ->
                            when (content) {
                                is Movies ->
                                    MobileWorkRow(
                                        title = stringResource(PresentationR.string.movies_title),
                                        works = content.movies,
                                        onWorkClick = onWorkClick,
                                        pagination = content.page,
                                        onLoadMore = onLoadMoreMovies,
                                    )

                                is TvShows ->
                                    MobileWorkRow(
                                        title = stringResource(PresentationR.string.tv_shows_title),
                                        works = content.tvShows,
                                        onWorkClick = onWorkClick,
                                        pagination = content.page,
                                        onLoadMore = onLoadMoreTvShows,
                                    )
                            }
                        }
                    }
                }
            }
        }

        if (state.isSearching) {
            MobileLoading(
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MobileSearchTopBarPreview() {
    BESTVTheme {
        MobileSearchTopBar(
            query = "A search query",
            onBack = {},
            onQueryChanged = {},
            onClear = {},
            onSearch = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MobileSearchEmptyPreview() {
    BESTVTheme {
        MobileSearchContent(
            state = SearchState(),
            modifier = Modifier.fillMaxWidth(),
            onRetry = {},
            onWorkClick = {},
            onLoadMoreMovies = {},
            onLoadMoreTvShows = {},
        )
    }
}
