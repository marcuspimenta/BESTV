/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.castdetail.presentation.ui.compose.mobile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import coil3.compose.AsyncImage
import com.pimenta.bestv.castdetail.presentation.model.CastDetailsEffect.Navigate
import com.pimenta.bestv.castdetail.presentation.model.CastDetailsEvent
import com.pimenta.bestv.castdetail.presentation.model.CastDetailsState
import com.pimenta.bestv.castdetail.presentation.viewmodel.CastDetailsViewModel
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.presentation.ui.compose.mobile.MobileError
import com.pimenta.bestv.presentation.ui.compose.mobile.MobileLoading
import com.pimenta.bestv.presentation.ui.compose.mobile.MobileWorkRow
import kotlinx.coroutines.flow.collectLatest
import com.pimenta.bestv.presentation.R as PresentationR

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MobileCastDetailsScreen(
    viewModel: CastDetailsViewModel,
    navigate: (NavKey) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val loaded = state as? CastDetailsState.Loaded
    val castId = loaded?.cast?.id
    val scrollState = rememberScrollState()
    var viewportTop by remember(castId) { mutableFloatStateOf(Float.NaN) }
    var castTitleBottom by remember(castId) { mutableFloatStateOf(Float.NaN) }
    var collapseThreshold by rememberSaveable(castId) {
        mutableIntStateOf(Int.MAX_VALUE)
    }
    val showCollapsedTitle by remember(scrollState, collapseThreshold) {
        derivedStateOf {
            collapseThreshold != Int.MAX_VALUE && scrollState.value >= collapseThreshold
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is Navigate -> navigate(effect.destination)
            }
        }
    }

    LaunchedEffect(Unit) { viewModel.handleEvent(CastDetailsEvent.LoadData) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BESTVTheme.colors.black,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(PresentationR.string.back),
                        )
                    }
                },
                title = {
                    AnimatedVisibility(
                        visible = showCollapsedTitle,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        Text(
                            text = loaded?.cast?.name.orEmpty(),
                            style = MaterialTheme.typography.titleLarge,
                            color = BESTVTheme.colors.white,
                            maxLines = 1,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BESTVTheme.colors.black),
                windowInsets = TopAppBarDefaults.windowInsets,
            )
        },
    ) { contentPadding ->
        MobileCastDetailsContent(
            state = state,
            scrollState = scrollState,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            onEvent = viewModel::handleEvent,
            onViewportTopChanged = {
                if (!viewportTop.isFinite()) {
                    viewportTop = it
                    if (castTitleBottom.isFinite() && collapseThreshold == Int.MAX_VALUE) {
                        // Restore the unscrolled position; measured bounds already include the saved offset.
                        val measuredThreshold = castTitleBottom - it + scrollState.value
                        collapseThreshold = measuredThreshold.toInt().coerceAtLeast(0)
                    }
                }
            },
            onTitleBottomChanged = {
                if (!castTitleBottom.isFinite()) {
                    castTitleBottom = it
                    if (viewportTop.isFinite() && collapseThreshold == Int.MAX_VALUE) {
                        // Restore the unscrolled position; measured bounds already include the saved offset.
                        val measuredThreshold = it - viewportTop + scrollState.value
                        collapseThreshold = measuredThreshold.toInt().coerceAtLeast(0)
                    }
                }
            },
        )
    }
}

@Composable
private fun MobileCastDetailsContent(
    state: CastDetailsState,
    scrollState: androidx.compose.foundation.ScrollState,
    modifier: Modifier = Modifier,
    onEvent: (CastDetailsEvent) -> Unit,
    onViewportTopChanged: (Float) -> Unit,
    onTitleBottomChanged: (Float) -> Unit,
) {
    when (state) {
        is CastDetailsState.Loading ->
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center,
            ) { MobileLoading() }

        is CastDetailsState.Error ->
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center,
            ) {
                MobileError(
                    onRetry = { onEvent(CastDetailsEvent.LoadData) },
                )
            }

        is CastDetailsState.Loaded ->
            MobileCastDetailsLoadedContent(
                castDetails = state,
                scrollState = scrollState,
                modifier = modifier,
                onEvent = onEvent,
                onViewportTopChanged = onViewportTopChanged,
                onTitleBottomChanged = onTitleBottomChanged,
            )
    }
}

@Composable
private fun MobileCastDetailsLoadedContent(
    castDetails: CastDetailsState.Loaded,
    scrollState: androidx.compose.foundation.ScrollState,
    modifier: Modifier,
    onEvent: (CastDetailsEvent) -> Unit,
    onViewportTopChanged: (Float) -> Unit,
    onTitleBottomChanged: (Float) -> Unit,
) {
    Column(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                onViewportTopChanged(coordinates.boundsInRoot().top)
            }.verticalScroll(scrollState),
    ) {
        MobileCastHeader(castDetails.cast, onTitleBottomChanged)
        if (castDetails.movies.isNotEmpty()) {
            MobileWorkRow(
                title = stringResource(PresentationR.string.movies_title),
                works = castDetails.movies,
                onWorkClick = { onEvent(CastDetailsEvent.WorkClicked(it)) },
            )
        }
        if (castDetails.tvShows.isNotEmpty()) {
            MobileWorkRow(
                title = stringResource(PresentationR.string.tv_shows_title),
                works = castDetails.tvShows,
                onWorkClick = { onEvent(CastDetailsEvent.WorkClicked(it)) },
            )
        }
    }
}

@Composable
private fun MobileCastHeader(
    cast: CastViewModel,
    onTitleBottomChanged: (Float) -> Unit,
) {
    Column(
        modifier = Modifier.padding(BESTVTheme.scale.s080),
        verticalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s080),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s080)) {
            AsyncImage(
                model = cast.thumbnailUrl,
                contentDescription = null,
                placeholder = ColorPainter(BESTVTheme.colors.mobileCardSurface),
                error = ColorPainter(BESTVTheme.colors.mobileCardSurface),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(BESTVTheme.scale.mobileCastPortraitWidth)
                    .height(BESTVTheme.scale.mobileCastPortraitHeight)
                    .clip(RoundedCornerShape(BESTVTheme.scale.mobileCardCornerRadius)),
            )
            Column(
                modifier = Modifier.align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s060),
            ) {
                Text(
                    cast.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = BESTVTheme.colors.white,
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        onTitleBottomChanged(coordinates.boundsInRoot().bottom)
                    },
                )
                if (cast.character.isNotBlank()) {
                    Text(
                        cast.character,
                        style = MaterialTheme.typography.titleSmall,
                        color = BESTVTheme.colors.secondaryInfoText,
                    )
                }
                if (cast.birthday.isNotBlank()) {
                    Text(
                        stringResource(com.pimenta.bestv.castdetail.R.string.born, cast.birthday),
                        style = MaterialTheme.typography.bodySmall,
                        color = BESTVTheme.colors.secondaryText,
                    )
                }
            }
        }
        if (cast.biography.isNotBlank()) {
            Text(
                cast.biography,
                style = MaterialTheme.typography.bodyLarge,
                color = BESTVTheme.colors.biographyText,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MobileCastHeaderPreview() {
    BESTVTheme {
        MobileCastHeader(
            cast = CastViewModel(
                id = 1,
                name = "Alex Morgan",
                character = "Lead actor",
                birthday = "1988-04-10",
                source = "TMDB",
                deathDay = "",
                biography =
                "An award-winning actor known for acclaimed dramatic roles and " +
                    "independent films.",
                thumbnailUrl = "",
            ),
            onTitleBottomChanged = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MobileCastDetailsContentPreview() {
    BESTVTheme {
        MobileCastDetailsContent(
            state = CastDetailsState.Loaded(
                cast = CastViewModel(
                    id = 1,
                    name = "Alex Morgan",
                    character = "Lead actor",
                    birthday = "1988-04-10",
                    source = "TMDB",
                    deathDay = "",
                    biography = "An award-winning actor known for acclaimed dramatic roles.",
                    thumbnailUrl = "",
                ),
                movies = listOf(
                    WorkViewModel(
                        id = 1,
                        originalLanguage = "en",
                        overview = "A preview movie.",
                        source = "Movie",
                        backdropUrl = "",
                        posterUrl = "",
                        title = "Preview Movie",
                        originalTitle = "Preview Movie",
                        releaseDate = "2025",
                        type = WorkType.MOVIE,
                        voteAverage = 8.1f,
                    ),
                ),
                tvShows = emptyList(),
            ),
            scrollState = rememberScrollState(),
            modifier = Modifier
                .fillMaxSize(),
            onEvent = {},
            onViewportTopChanged = {},
            onTitleBottomChanged = {},
        )
    }
}
