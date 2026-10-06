/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workbrowse.presentation.ui.compose.mobile

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseEffect.CloseScreen
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseEffect.Navigate
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseEvent
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section.Favorites
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section.Movies
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section.Search
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section.TvShows
import com.pimenta.bestv.workbrowse.presentation.viewmodel.WorkBrowseViewModel
import kotlinx.coroutines.flow.collectLatest
import com.pimenta.bestv.presentation.R as presentationR
import com.pimenta.bestv.workbrowse.R as workbrowseR

@Composable
fun MobileBrowseScreen(
    viewModel: WorkBrowseViewModel,
    openIntent: (Intent) -> Unit,
    openSearch: () -> Unit,
    closeScreen: () -> Unit,
) {
    val browseState by viewModel.state.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableStateOf("movies") }

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                CloseScreen -> closeScreen()
                is Navigate -> openIntent(effect.intent)
            }
        }
    }

    LifecycleResumeEffect(Unit) {
        viewModel.handleEvent(WorkBrowseEvent.ScreenResumed)
        onPauseOrDispose { }
    }

    val destinations = remember(browseState) { mobileDestinations(browseState) }
    LaunchedEffect(destinations, selectedTab) {
        if (destinations.none { it.key == selectedTab }) {
            selectedTab = destinations.firstOrNull { it.key != "search" }?.key ?: "movies"
        }
    }

    Scaffold(
        containerColor = BESTVTheme.colors.black,
        topBar = {
            val loaded = browseState.state as? WorkBrowseState.State.Loaded
            val selected = loaded?.sections?.getOrNull(loaded.selectedSectionIndex)
            if (selected != null) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(
                                horizontal = BESTVTheme.scale.s080,
                                vertical = BESTVTheme.scale.s060,
                            ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s020),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(presentationR.drawable.bestv_mark),
                            contentDescription = stringResource(presentationR.string.app_name),
                            modifier =
                                Modifier
                                    .size(BESTVTheme.scale.s160),
                        )
                        Image(
                            painter = painterResource(presentationR.drawable.bestv_wordmark),
                            contentDescription = stringResource(presentationR.string.app_name),
                            modifier =
                                Modifier
                                    .size(width = BESTVTheme.scale.s400, height = BESTVTheme.scale.s160),
                        )
                    }
                    IconButton(onClick = openSearch) {
                        Icon(
                            painter = painterResource(presentationR.drawable.search),
                            contentDescription = stringResource(workbrowseR.string.search),
                            tint = BESTVTheme.colors.white,
                            modifier =
                                Modifier
                                    .size(BESTVTheme.scale.s100),
                        )
                    }
                }
            }
        },
    ) { contentPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
        ) {
            MobileBrowseContent(
                state = browseState,
                onWorkClick = { viewModel.handleEvent(WorkBrowseEvent.WorkClicked(it)) },
                onRetry = { viewModel.handleEvent(WorkBrowseEvent.RetryLoad) },
                onSplashAnimationFinished = {
                    viewModel.handleEvent(WorkBrowseEvent.SplashAnimationFinished)
                },
                modifier =
                    Modifier
                        .fillMaxSize(),
            )

            if (destinations.isNotEmpty()) {
                MobileBrowseBottomBar(
                    destinations = destinations,
                    selectedTab = selectedTab,
                    onDestinationClick = { destination ->
                        selectedTab = destination.key
                        destination.sectionIndex?.let { index ->
                            viewModel.handleEvent(WorkBrowseEvent.SectionClicked(index))
                        }
                    },
                    modifier =
                        Modifier
                            .align(Alignment.BottomCenter),
                )
            }
        }
    }
}

internal data class MobileDestination(
    val key: String,
    val titleRes: Int,
    val iconRes: Int,
    val sectionIndex: Int?,
)

private fun mobileDestinations(state: WorkBrowseState): List<MobileDestination> {
    val sections = (state.state as? WorkBrowseState.State.Loaded)?.sections.orEmpty()
    return sections.mapIndexedNotNull { index, section ->
        when (section) {
            is Search -> null
            is Movies -> MobileDestination("movies", section.titleRes, section.iconRes, index)
            is TvShows -> MobileDestination("tv", section.titleRes, section.iconRes, index)
            is Favorites -> MobileDestination("favorites", section.titleRes, section.iconRes, index)
            is Section.About -> null
        }
    }
}
