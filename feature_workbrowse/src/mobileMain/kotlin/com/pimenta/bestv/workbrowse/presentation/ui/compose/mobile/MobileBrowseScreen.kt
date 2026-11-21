/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workbrowse.presentation.ui.compose.mobile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseEffect.CloseScreen
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseEffect.Navigate
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseEvent
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section.Search
import com.pimenta.bestv.workbrowse.presentation.viewmodel.WorkBrowseViewModel
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.pimenta.bestv.common.resources.Res as CommonRes
import com.pimenta.bestv.workbrowse.resources.Res as workbrowseR
import com.pimenta.bestv.common.resources.app_name
import com.pimenta.bestv.common.resources.bestv_mark
import com.pimenta.bestv.common.resources.bestv_wordmark
import com.pimenta.bestv.common.resources.search as commonSearchDrawable
import com.pimenta.bestv.workbrowse.resources.search as workBrowseSearchString

@Composable
fun MobileBrowseScreen(
    viewModel: WorkBrowseViewModel,
    navigate: (NavKey) -> Unit,
    closeScreen: () -> Unit,
) {
    val browseState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                CloseScreen -> closeScreen()
                is Navigate -> navigate(effect.destination)
            }
        }
    }

    MobileBrowseLayout(
        state = browseState,
        onEvent = viewModel::handleEvent,
    )
}

@Composable
private fun MobileBrowseLayout(
    state: WorkBrowseState,
    onEvent: (WorkBrowseEvent) -> Unit,
) {
    val loadedState = state.state as? WorkBrowseState.State.Loaded
    val sections = loadedState?.sections.orEmpty()
    val selectedSection = loadedState?.let { sections.getOrNull(it.selectedSectionIndex) }

    Scaffold(
        containerColor = BESTVTheme.colors.black,
        topBar = {
            if (selectedSection != null) {
                MobileBrowseTopBar(onSearchClick = { onEvent(WorkBrowseEvent.SectionClicked(0)) })
            }
        },
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
        ) {
            MobileBrowseContent(
                state = state,
                onWorkClick = { onEvent(WorkBrowseEvent.WorkClicked(it)) },
                onRetry = { onEvent(WorkBrowseEvent.RetryLoad) },
                onSplashAnimationFinished = { onEvent(WorkBrowseEvent.SplashAnimationFinished) },
                modifier = Modifier.fillMaxSize(),
            )

            if (sections.any { it !is Search }) {
                MobileBrowseBottomBar(
                    sections = sections,
                    selectedSection = selectedSection,
                    onSectionClick = { section ->
                        val sectionIndex = sections.indexOf(section)
                        if (sectionIndex >= 0) {
                            onEvent(WorkBrowseEvent.SectionClicked(sectionIndex))
                        }
                    },
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MobileBrowseTopBar(onSearchClick: () -> Unit) {
    TopAppBar(
        title = { MobileBrowseLogo() },
        actions = { MobileBrowseSearchButton(onClick = onSearchClick) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BESTVTheme.colors.black,
            titleContentColor = BESTVTheme.colors.white,
        ),
        windowInsets = TopAppBarDefaults.windowInsets,
    )
}

@Composable
private fun MobileBrowseLogo() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s020),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(CommonRes.drawable.bestv_mark),
            contentDescription = null,
            modifier = Modifier.size(BESTVTheme.scale.s120),
        )
        Image(
            painter = painterResource(CommonRes.drawable.bestv_wordmark),
            contentDescription = stringResource(CommonRes.string.app_name),
            modifier = Modifier.size(
                width = BESTVTheme.scale.s300,
                height = BESTVTheme.scale.s120,
            ),
        )
    }
}

@Composable
private fun MobileBrowseSearchButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(CommonRes.drawable.commonSearchDrawable),
            contentDescription = stringResource(workbrowseR.string.workBrowseSearchString),
            tint = BESTVTheme.colors.white,
            modifier = Modifier.size(BESTVTheme.scale.s100),
        )
    }
}
