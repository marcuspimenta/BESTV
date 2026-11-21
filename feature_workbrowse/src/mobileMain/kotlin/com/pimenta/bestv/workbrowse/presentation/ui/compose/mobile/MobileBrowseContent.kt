/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workbrowse.presentation.ui.compose.mobile

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import com.pimenta.bestv.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.presentation.ui.compose.mobile.MobileError
import com.pimenta.bestv.presentation.ui.compose.mobile.MobileWorkRow
import com.pimenta.bestv.workbrowse.presentation.model.ContentSection
import com.pimenta.bestv.workbrowse.presentation.model.ContentSection.Genre
import com.pimenta.bestv.workbrowse.presentation.model.ContentSection.TopContent
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section.Favorites
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section.Movies
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section.TvShows
import com.pimenta.bestv.workbrowse.presentation.model.lazyContentType
import com.pimenta.bestv.workbrowse.presentation.model.lazyListKey

@Composable
fun MobileBrowseContent(
    state: WorkBrowseState,
    onWorkClick: (WorkViewModel) -> Unit,
    onRetry: () -> Unit,
    onSplashAnimationFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (val currentState = state.state) {
            is WorkBrowseState.State.Loading -> SplashAnimation(
                modifier = Modifier.fillMaxSize(),
                onCompleted = onSplashAnimationFinished,
            )

            is WorkBrowseState.State.Error -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { MobileError(onRetry = onRetry) }

            is WorkBrowseState.State.Loaded -> {
                var hasPlayedEntrance by rememberSaveable { mutableStateOf(false) }
                LaunchedEffect(Unit) { hasPlayedEntrance = true }
                AnimatedContent(
                    targetState = currentState.selectedSectionIndex,
                    modifier = Modifier.fillMaxSize(),
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
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = BESTVTheme.scale.s600),
                    ) {
                        items(
                            items = sectionContent,
                            key = { it.lazyListKey() },
                            contentType = { it.lazyContentType() },
                        ) { content ->
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
}

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
