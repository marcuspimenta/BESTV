/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workbrowse.presentation.ui.compose.mobile

import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.model.PaginationState
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.presentation.ui.compose.mobile.MobileError
import com.pimenta.bestv.presentation.ui.compose.mobile.MobileWorkRow
import com.pimenta.bestv.workbrowse.presentation.model.ContentSection
import com.pimenta.bestv.workbrowse.presentation.model.ContentSection.Genre
import com.pimenta.bestv.workbrowse.presentation.model.ContentSection.TopContent
import com.pimenta.bestv.workbrowse.presentation.model.TopWorkTypeViewModel
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section.Favorites
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section.Movies
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section.TvShows

@Composable
fun MobileBrowseContent(
    state: WorkBrowseState,
    onWorkClick: (WorkViewModel) -> Unit,
    onRetry: () -> Unit,
    onSplashAnimationFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (val currentState = state.state) {
        is WorkBrowseState.State.Loading ->
            MobileLoadingSplashScreen(
                isAnimationFinished = currentState.isSplashAnimationFinished,
                onAnimationFinished = onSplashAnimationFinished,
                modifier = modifier.fillMaxSize(),
            )

        is WorkBrowseState.State.Error ->
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { MobileError(onRetry = onRetry) }

        is WorkBrowseState.State.Loaded -> {
            var hasPlayedEntrance by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { hasPlayedEntrance = true }
            AnimatedContent(
                targetState = currentState.selectedSectionIndex,
                modifier = modifier.fillMaxSize(),
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally(
                            animationSpec = tween(durationMillis = 300),
                            initialOffsetX = { it },
                        ) togetherWith
                            slideOutHorizontally(
                                animationSpec = tween(durationMillis = 300),
                                targetOffsetX = { -it },
                            )
                    } else {
                        slideInHorizontally(
                            animationSpec = tween(durationMillis = 300),
                            initialOffsetX = { -it },
                        ) togetherWith
                            slideOutHorizontally(
                                animationSpec = tween(durationMillis = 300),
                                targetOffsetX = { it },
                            )
                    }
                },
                label = "browseSectionTransition",
            ) { sectionIndex ->
                val selected = currentState.sections.getOrNull(sectionIndex)
                val sectionContent = when (selected) {
                    is Movies -> selected.content
                    is TvShows -> selected.content
                    is Favorites -> selected.content
                    else -> emptyList()
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(bottom = BESTVTheme.scale.s600),
                ) {
                    items(sectionContent, key = { it.hashCode() }) { content ->
                        AnimatedVisibility(
                            visible = hasPlayedEntrance,
                            enter = slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = tween(durationMillis = 450),
                            ) + fadeIn(animationSpec = tween(durationMillis = 350)),
                        ) {
                            MobileContentRow(content, onWorkClick)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MobileLoadingSplashScreen(
    isAnimationFinished: Boolean,
    onAnimationFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                VideoView(context).apply {
                    setOnCompletionListener { onAnimationFinished() }
                    setVideoURI(SPLASH_ANIMATION_FILE.toUri())
                    start()
                }
            }
        )

        if (isAnimationFinished) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = BESTVTheme.scale.s600)
                    .size(BESTVTheme.scale.s160),
                color = BESTVTheme.colors.white,
                strokeWidth = BESTVTheme.scale.s015,
            )
        }
    }
}

private const val SPLASH_ANIMATION_FILE =
    "android.resource://com.pimenta.bestv/raw/splash_animation"

@Composable
private fun MobileContentRow(
    content: ContentSection,
    onWorkClick: (WorkViewModel) -> Unit,
) {
    val title = when (content) {
        is Genre -> content.genreViewModel.name.orEmpty()
        is TopContent -> stringResource(content.type.resource)
    }
    MobileWorkRow(
        title = title,
        works = content.works,
        onWorkClick = onWorkClick,
    )
}

@Preview(showBackground = true)
@Composable
private fun MobileBrowseContentPreview() {
    val previewWork = WorkViewModel(
        id = 1,
        originalLanguage = "en",
        overview = "A preview description for the mobile browse screen.",
        source = "Movie",
        backdropUrl = "",
        posterUrl = "",
        title = "Preview Movie",
        originalTitle = "Preview Movie",
        releaseDate = "2025",
        type = WorkType.MOVIE,
        voteAverage = 8.1f,
    )
    val section = TopContent(
        type = TopWorkTypeViewModel.NOW_PLAYING_MOVIES,
        works = listOf(previewWork, previewWork.copy(id = 2, title = "Another Movie")),
        page = PaginationState(),
    )
    BESTVTheme {
        MobileBrowseContent(
            state = WorkBrowseState(
                state = WorkBrowseState.State.Loaded(
                    workSelected = null,
                    selectedSectionIndex = 0,
                    sections = listOf(Movies(content = listOf(section))),
                ),
            ),
            onWorkClick = {},
            onRetry = {},
            onSplashAnimationFinished = {},
            modifier = Modifier
                .fillMaxSize(),
        )
    }
}
