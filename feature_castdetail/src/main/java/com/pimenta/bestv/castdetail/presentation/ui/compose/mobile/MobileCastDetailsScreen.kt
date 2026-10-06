/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.castdetail.presentation.ui.compose.mobile

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
fun MobileCastDetailsScreen(
    viewModel: CastDetailsViewModel,
    openIntent: (Intent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val loaded = state as? CastDetailsState.Loaded
    var viewportTop by remember { mutableFloatStateOf(Float.NaN) }
    var castTitleBottom by remember { mutableFloatStateOf(Float.NaN) }
    val showCollapsedTitle =
        viewportTop.isFinite() &&
            castTitleBottom.isFinite() &&
            castTitleBottom <= viewportTop

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is Navigate -> openIntent(effect.intent)
            }
        }
    }

    LaunchedEffect(Unit) { viewModel.handleEvent(CastDetailsEvent.LoadData) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BESTVTheme.colors.black,
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier
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
                        text = loaded?.cast?.name.orEmpty(),
                        style = MaterialTheme.typography.titleLarge,
                        color = BESTVTheme.colors.white,
                        maxLines = 1,
                    )
                }
            }
        },
    ) { contentPadding ->
        MobileCastDetailsContent(
            state = state,
            modifier =
                Modifier
                    .fillMaxSize().padding(contentPadding),
            onEvent = viewModel::handleEvent,
            onViewportTopChanged = { viewportTop = it },
            onTitleBottomChanged = { castTitleBottom = it },
        )
    }
}

@Composable
private fun MobileCastDetailsContent(
    state: CastDetailsState,
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
            ) { MobileError(onRetry = { onEvent(CastDetailsEvent.LoadData) }) }

        is CastDetailsState.Loaded ->
            MobileCastDetailsLoadedContent(
                castDetails = state,
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
    modifier: Modifier,
    onEvent: (CastDetailsEvent) -> Unit,
    onViewportTopChanged: (Float) -> Unit,
    onTitleBottomChanged: (Float) -> Unit,
) {
    Column(
        modifier =
            modifier
                .onGloballyPositioned { coordinates ->
                    onViewportTopChanged(coordinates.boundsInRoot().top)
                }.verticalScroll(rememberScrollState()),
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
        modifier =
            Modifier
                .padding(BESTVTheme.scale.s080),
        verticalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s080),
    ) {
        AsyncImage(
            model = cast.thumbnailUrl,
            contentDescription = cast.name,
            contentScale = ContentScale.Crop,
            modifier =
                Modifier
                    .width(BESTVTheme.scale.s700)
                    .height(BESTVTheme.scale.s1000)
                    .clip(RoundedCornerShape(BESTVTheme.scale.s060)),
        )
        Column {
            Text(
                cast.name,
                style = MaterialTheme.typography.headlineMedium,
                color = BESTVTheme.colors.white,
                modifier =
                    Modifier
                        .onGloballyPositioned { coordinates ->
                        onTitleBottomChanged(coordinates.boundsInRoot().bottom)
                    },
            )
            if (cast.birthday.isNotBlank()) {
                Text(
                    stringResource(com.pimenta.bestv.castdetail.R.string.born, cast.birthday),
                    color = BESTVTheme.colors.secondaryInfoText,
                    modifier =
                        Modifier
                            .padding(top = BESTVTheme.scale.s060),
                )
            }
            if (cast.biography.isNotBlank()) {
                Text(
                    cast.biography,
                    color = BESTVTheme.colors.biographyText,
                    modifier =
                        Modifier
                            .padding(top = BESTVTheme.scale.s080),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MobileCastHeaderPreview() {
    BESTVTheme {
        MobileCastHeader(
            cast =
                CastViewModel(
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
            state =
                CastDetailsState.Loaded(
                    cast =
                        CastViewModel(
                            id = 1,
                            name = "Alex Morgan",
                            character = "Lead actor",
                            birthday = "1988-04-10",
                            source = "TMDB",
                            deathDay = "",
                            biography = "An award-winning actor known for acclaimed dramatic roles.",
                            thumbnailUrl = "",
                        ),
                    movies =
                        listOf(
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
            modifier =
                Modifier
                    .fillMaxSize(),
            onEvent = {},
            onViewportTopChanged = {},
            onTitleBottomChanged = {},
        )
    }
}
