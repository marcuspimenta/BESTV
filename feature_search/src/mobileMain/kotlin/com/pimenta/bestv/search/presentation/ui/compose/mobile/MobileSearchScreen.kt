/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.search.presentation.ui.compose.mobile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.pimenta.bestv.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.presentation.ui.compose.mobile.MobileError
import com.pimenta.bestv.presentation.ui.compose.mobile.MobileLoading
import com.pimenta.bestv.presentation.ui.compose.mobile.MobileWorkRow
import com.pimenta.bestv.search.resources.Res
import com.pimenta.bestv.search.presentation.model.SearchEffect.Navigate
import com.pimenta.bestv.search.presentation.model.SearchEvent.ClearSearch
import com.pimenta.bestv.search.presentation.model.SearchEvent.LoadMoreMovies
import com.pimenta.bestv.search.presentation.model.SearchEvent.LoadMoreTvShows
import com.pimenta.bestv.search.presentation.model.SearchEvent.SearchQueryChanged
import com.pimenta.bestv.search.presentation.model.SearchEvent.SearchQuerySubmitted
import com.pimenta.bestv.search.presentation.model.SearchEvent.WorkClicked
import com.pimenta.bestv.search.presentation.model.SearchState
import com.pimenta.bestv.search.presentation.model.lazyContentType
import com.pimenta.bestv.search.presentation.model.lazyListKey
import com.pimenta.bestv.search.presentation.model.SearchState.Content.Movies
import com.pimenta.bestv.search.presentation.model.SearchState.Content.TvShows
import com.pimenta.bestv.search.presentation.model.SearchState.State.Empty
import com.pimenta.bestv.search.presentation.model.SearchState.State.Error
import com.pimenta.bestv.search.presentation.viewmodel.SearchViewModel
import kotlinx.coroutines.flow.collectLatest
import com.pimenta.bestv.common.resources.Res as CommonRes
import com.pimenta.bestv.common.resources.back
import com.pimenta.bestv.common.resources.movies_title
import com.pimenta.bestv.common.resources.tv_shows_title
import com.pimenta.bestv.search.resources.clear_search
import com.pimenta.bestv.search.resources.no_results
import com.pimenta.bestv.search.resources.search_placeholder

@Composable
fun MobileSearchScreen(
    viewModel: SearchViewModel,
    navigate: (NavKey) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is Navigate -> navigate(effect.destination)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
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
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .imePadding(),
            onRetry = { viewModel.handleEvent(SearchQuerySubmitted(state.query)) },
            onWorkClick = { viewModel.handleEvent(WorkClicked(it)) },
            onLoadMoreMovies = { viewModel.handleEvent(LoadMoreMovies) },
            onLoadMoreTvShows = { viewModel.handleEvent(LoadMoreTvShows) },
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MobileSearchTopBar(
    query: String,
    onBack: () -> Unit,
    onQueryChanged: (String) -> Unit,
    onClear: () -> Unit,
    onSearch: () -> Unit,
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(CommonRes.string.back),
                    tint = BESTVTheme.colors.white,
                )
            }
        },
        title = {
            MobileSearchField(
                query = query,
                onQueryChanged = onQueryChanged,
                onClear = onClear,
                onSearch = onSearch,
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = BESTVTheme.colors.black),
        windowInsets = TopAppBarDefaults.windowInsets,
    )
}

@Composable
private fun MobileSearchField(
    query: String,
    onQueryChanged: (String) -> Unit,
    onClear: () -> Unit,
    onSearch: () -> Unit,
) {
    TextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = Modifier
            .fillMaxWidth()
            .height(BESTVTheme.scale.s280)
            .clip(RoundedCornerShape(BESTVTheme.scale.mobileSearchFieldCornerRadius)),
        placeholder = {
            Text(
                text = stringResource(Res.string.search_placeholder),
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
                        contentDescription = stringResource(Res.string.clear_search),
                    )
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        colors = TextFieldDefaults.colors(
            focusedTextColor = BESTVTheme.colors.white,
            unfocusedTextColor = BESTVTheme.colors.white,
            cursorColor = BESTVTheme.colors.white,
            focusedContainerColor = BESTVTheme.colors.mobileCardSurface,
            unfocusedContainerColor = BESTVTheme.colors.mobileCardSurface,
            focusedIndicatorColor = BESTVTheme.colors.transparent,
            unfocusedIndicatorColor = BESTVTheme.colors.transparent,
        ),
    )
}

@Composable
private fun MobileSearchContent(
    state: SearchState,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
    onWorkClick: (WorkViewModel) -> Unit,
    onLoadMoreMovies: () -> Unit,
    onLoadMoreTvShows: () -> Unit,
) {
    Box(modifier = modifier) {
        when (val result = state.state) {
            is Empty ->
                MobileSearchEmptyMessage(
                    message = stringResource(Res.string.search_placeholder),
                    modifier = Modifier.align(Alignment.Center),
                )

            is Error ->
                MobileError(
                    onRetry = onRetry,
                    modifier = Modifier
                        .align(Alignment.Center),
                )

            is SearchState.State.Loaded -> {
                if (!result.hasResults) {
                    MobileSearchEmptyMessage(
                        message = stringResource(Res.string.no_results),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Center),
                    )
                } else {
                    MobileSearchResults(
                        contents = result.contents,
                        onWorkClick = onWorkClick,
                        onLoadMoreMovies = onLoadMoreMovies,
                        onLoadMoreTvShows = onLoadMoreTvShows,
                    )
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

@Composable
private fun MobileSearchEmptyMessage(
    message: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
) {
    Column(
        modifier = modifier.padding(BESTVTheme.scale.s240),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s060),
    ) {
        Icon(
            Icons.Default.Search,
            contentDescription = null,
            tint = BESTVTheme.colors.secondaryText,
            modifier = Modifier.size(BESTVTheme.scale.s160),
        )
        Text(text = message, color = BESTVTheme.colors.secondaryText, style = style)
    }
}

@Composable
private fun MobileSearchResults(
    contents: List<SearchState.Content>,
    onWorkClick: (WorkViewModel) -> Unit,
    onLoadMoreMovies: () -> Unit,
    onLoadMoreTvShows: () -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s080),
    ) {
        items(
            items = contents,
            key = { content -> content.lazyListKey() },
            contentType = { content -> content.lazyContentType() },
        ) { content ->
            when (content) {
                is Movies ->
                    MobileWorkRow(
                        title = stringResource(CommonRes.string.movies_title),
                        works = content.movies,
                        onWorkClick = onWorkClick,
                        pagination = content.page,
                        onLoadMore = onLoadMoreMovies,
                    )

                is TvShows ->
                    MobileWorkRow(
                        title = stringResource(CommonRes.string.tv_shows_title),
                        works = content.tvShows,
                        onWorkClick = onWorkClick,
                        pagination = content.page,
                        onLoadMore = onLoadMoreTvShows,
                    )
            }
        }
    }
}
