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
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section.Search
import com.pimenta.bestv.workbrowse.presentation.viewmodel.WorkBrowseViewModel
import kotlinx.coroutines.flow.collectLatest
import com.pimenta.bestv.presentation.R as presentationR
import com.pimenta.bestv.workbrowse.R as workbrowseR

@Composable
fun MobileBrowseScreen(
    viewModel: WorkBrowseViewModel,
    openIntent: (Intent) -> Unit,
    closeScreen: () -> Unit,
) {
    val browseState by viewModel.state.collectAsStateWithLifecycle()

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

    val loadedState = browseState.state as? WorkBrowseState.State.Loaded
    val sections = loadedState?.sections.orEmpty()
    val selectedSection = loadedState?.let { sections.getOrNull(it.selectedSectionIndex) }

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
                    IconButton(
                        onClick = {
                            viewModel.handleEvent(WorkBrowseEvent.SectionClicked(0))
                        }
                    ) {
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

            if (sections.any { it !is Search }) {
                MobileBrowseBottomBar(
                    sections = sections,
                    selectedSection = selectedSection,
                    onSectionClick = { section ->
                        val sectionIndex = sections.indexOf(section)
                        if (sectionIndex >= 0) {
                            viewModel.handleEvent(WorkBrowseEvent.SectionClicked(sectionIndex))
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
