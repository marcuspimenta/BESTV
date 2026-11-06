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

package com.pimenta.bestv.workbrowse.presentation.ui.compose.tv

import android.widget.VideoView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.ModalNavigationDrawer
import androidx.tv.material3.NavigationDrawerItem
import androidx.tv.material3.Text
import androidx.tv.material3.rememberDrawerState
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.presentation.ui.compose.tv.TVBackgroundScreen
import com.pimenta.bestv.presentation.ui.compose.tv.TVErrorScreen
import com.pimenta.bestv.presentation.ui.compose.tv.TVSlideInFromBottom
import com.pimenta.bestv.presentation.ui.compose.tv.TVWorksRow
import com.pimenta.bestv.presentation.ui.compose.tv.fadeAtTopEdge
import com.pimenta.bestv.workbrowse.presentation.model.ContentSection
import com.pimenta.bestv.workbrowse.presentation.model.ContentSection.Genre
import com.pimenta.bestv.workbrowse.presentation.model.ContentSection.TopContent
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseEffect.CloseScreen
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseEffect.Navigate
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseEvent
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState
import com.pimenta.bestv.workbrowse.presentation.model.lazyContentType
import com.pimenta.bestv.workbrowse.presentation.model.lazyListKey
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section.Favorites
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section.Movies
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section.Search
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section.TvShows
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.State.Error
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.State.Loaded
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.State.Loading
import com.pimenta.bestv.workbrowse.presentation.viewmodel.WorkBrowseViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val SPLASH_ANIMATION_FILE = "android.resource://com.pimenta.bestv/raw/splash_animation"

@Composable
fun TVWorkBrowseScreen(
    viewModel: WorkBrowseViewModel,
    closeScreen: () -> Unit,
    navigate: (NavKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is CloseScreen -> closeScreen()
                is Navigate -> navigate(effect.destination)
            }
        }
    }

    TVWorkBrowseContent(
        state = state,
        onBackClicked = { viewModel.handleEvent(WorkBrowseEvent.BackClicked) },
        onSplashAnimationFinished = { viewModel.handleEvent(WorkBrowseEvent.SplashAnimationFinished) },
        onSectionClicked = { viewModel.handleEvent(WorkBrowseEvent.SectionClicked(it)) },
        onWorkSelected = { viewModel.handleEvent(WorkBrowseEvent.WorkSelected(it)) },
        onWorkClicked = { viewModel.handleEvent(WorkBrowseEvent.WorkClicked(it)) },
        onRetryClicked = { viewModel.handleEvent(WorkBrowseEvent.RetryLoad) },
        modifier = modifier,
    )
}

@Composable
internal fun TVWorkBrowseContent(
    state: WorkBrowseState,
    onBackClicked: () -> Unit,
    onSplashAnimationFinished: () -> Unit,
    onSectionClicked: (Int) -> Unit,
    onWorkSelected: (WorkViewModel) -> Unit,
    onWorkClicked: (WorkViewModel) -> Unit,
    onRetryClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (val contentState = state.state) {
            is Loading -> {
                TVLoadingSplashScreen(
                    onSplashAnimationFinished = onSplashAnimationFinished,
                    modifier = Modifier
                        .fillMaxSize(),
                )
            }

            is Error -> {
                TVErrorScreen(
                    onRetryClick = onRetryClicked,
                    modifier = Modifier
                        .fillMaxSize(),
                )
            }

            is Loaded -> {
                TVBrowseSections(
                    workSelected = contentState.workSelected,
                    selectedSectionIndex = contentState.selectedSectionIndex,
                    sections = contentState.sections,
                    onBackClicked = onBackClicked,
                    onSectionClicked = onSectionClicked,
                    onWorkSelected = onWorkSelected,
                    onWorkClicked = onWorkClicked,
                    modifier = Modifier
                        .fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun TVLoadingSplashScreen(
    onSplashAnimationFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            VideoView(context).apply {
                setOnCompletionListener { onSplashAnimationFinished() }
                setVideoURI(SPLASH_ANIMATION_FILE.toUri())
                start()
            }
        },
    )
}

@Composable
private fun TVBrowseSections(
    workSelected: WorkViewModel?,
    selectedSectionIndex: Int,
    sections: List<Section>,
    onBackClicked: () -> Unit,
    onWorkSelected: (WorkViewModel) -> Unit,
    onSectionClicked: (Int) -> Unit,
    onWorkClicked: (WorkViewModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val focusRequester = remember { FocusRequester() }
    val sectionStateHolder = rememberSaveableStateHolder()
    val initiallyFocusedSections = remember { mutableSetOf<String>() }
    val drawerItemFocusRequesters = remember(sections.size) {
        List(sections.size) { FocusRequester() }
    }
    val selectedSection = sections[selectedSectionIndex]
    var drawerWasOpen by remember { mutableStateOf(false) }

    BackHandler {
        if (drawerState.currentValue == DrawerValue.Open) {
            drawerState.setValue(DrawerValue.Closed)
        } else {
            onBackClicked()
        }
    }

    LaunchedEffect(drawerState.currentValue) {
        if (drawerState.currentValue == DrawerValue.Open) {
            drawerWasOpen = true
            drawerItemFocusRequesters.getOrNull(selectedSectionIndex)?.requestFocus()
        } else if (drawerWasOpen) {
            drawerWasOpen = false
            focusRequester.requestFocus()
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        TVBackgroundScreen(
            backdropUrl = workSelected?.backdropUrl,
        )

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .onPreviewKeyEvent { keyEvent ->
                            if (keyEvent.type == KeyEventType.KeyDown &&
                                keyEvent.key == Key.DirectionRight &&
                                drawerState.currentValue == DrawerValue.Open
                            ) {
                                scope.launch { drawerState.setValue(DrawerValue.Closed) }
                                true
                            } else {
                                false
                            }
                        },
                ) {
                    Column(
                        modifier = Modifier
                            .focusGroup()
                            .focusRestorer()
                            .padding(BESTVTheme.scale.s060)
                            .align(Alignment.Center),
                        verticalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s060),
                    ) {
                        sections.forEachIndexed { index, section ->
                            NavigationDrawerItem(
                                selected = selectedSectionIndex == index,
                                onClick = {
                                    onSectionClicked(index)
                                    scope.launch {
                                        drawerState.setValue(DrawerValue.Closed)
                                    }
                                },
                                leadingContent = {
                                    Icon(
                                        painter = painterResource(section.iconRes),
                                        contentDescription = null,
                                        tint = BESTVTheme.colors.white,
                                    )
                                },
                                modifier = Modifier
                                    .focusRequester(drawerItemFocusRequesters[index]),
                                content = {
                                    Text(
                                        text = stringResource(section.titleRes),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = BESTVTheme.colors.white,
                                    )
                                },
                            )
                        }
                    }
                }
            },
            scrimBrush = Brush.horizontalGradient(listOf(BESTVTheme.colors.black, BESTVTheme.colors.transparent)),
            modifier = modifier,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .focusGroup()
                    .focusRestorer()
                    .onFocusChanged { focusState ->
                        if (focusState.hasFocus && drawerState.currentValue == DrawerValue.Open) {
                            scope.launch {
                                drawerState.setValue(DrawerValue.Closed)
                            }
                        }
                    },
            ) {
                sectionStateHolder.SaveableStateProvider(selectedSection.saveableKey()) {
                    TVSection(
                        section = selectedSection,
                        focusRequester = focusRequester,
                        initiallyFocusedSections = initiallyFocusedSections,
                        workSelected = workSelected,
                        onWorkSelected = onWorkSelected,
                        onWorkClicked = onWorkClicked,
                        modifier = Modifier,
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(BESTVTheme.scale.s400)
                        .background(BESTVTheme.colors.drawerScrim)
                        .align(Alignment.TopStart),
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(BESTVTheme.scale.s250)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    BESTVTheme.colors.transparent,
                                    BESTVTheme.colors.drawerScrim,
                                ),
                            ),
                        ).align(BottomStart),
                )
            }
        }
    }
}

@Composable
private fun TVSection(
    section: Section,
    focusRequester: FocusRequester,
    initiallyFocusedSections: MutableSet<String>,
    workSelected: WorkViewModel?,
    onWorkSelected: (WorkViewModel) -> Unit,
    onWorkClicked: (WorkViewModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (section) {
        is Search -> Unit
        is Favorites ->
            TVSectionWorks(
                sectionKey = section.saveableKey(),
                focusRequester = focusRequester,
                initiallyFocusedSections = initiallyFocusedSections,
                workSelected = workSelected,
                content = section.content,
                onWorkSelected = onWorkSelected,
                onWorkClicked = onWorkClicked,
                modifier = modifier,
            )

        is Movies ->
            TVSectionWorks(
                sectionKey = section.saveableKey(),
                focusRequester = focusRequester,
                initiallyFocusedSections = initiallyFocusedSections,
                workSelected = workSelected,
                content = section.content,
                onWorkSelected = onWorkSelected,
                onWorkClicked = onWorkClicked,
                modifier = modifier,
            )

        is TvShows ->
            TVSectionWorks(
                sectionKey = section.saveableKey(),
                focusRequester = focusRequester,
                initiallyFocusedSections = initiallyFocusedSections,
                workSelected = workSelected,
                content = section.content,
                onWorkSelected = onWorkSelected,
                onWorkClicked = onWorkClicked,
                modifier = modifier,
            )
    }
}

@Composable
private fun TVSectionWorks(
    sectionKey: String,
    focusRequester: FocusRequester,
    initiallyFocusedSections: MutableSet<String>,
    workSelected: WorkViewModel?,
    content: List<ContentSection>,
    onWorkSelected: (WorkViewModel) -> Unit,
    onWorkClicked: (WorkViewModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val shouldRequestInitialFocus = sectionKey !in initiallyFocusedSections
    val hasWorks = content.any { it.works.isNotEmpty() }

    TVSlideInFromBottom(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .fillMaxHeight(0.40f),
            ) {
                TVWorkSelectedHeader(
                    workSelected = workSelected,
                    modifier = Modifier
                        .align(BottomStart),
                )
            }

            TVSectionWorkList(
                content = content,
                listState = listState,
                onWorkSelected = onWorkSelected,
                onWorkClicked = onWorkClicked,
                initialFocusRequester = focusRequester,
                requestInitialFocus = shouldRequestInitialFocus,
                focusTargetWorkId = workSelected?.id,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
        }

        LaunchedEffect(sectionKey, hasWorks) {
            if (hasWorks) {
                initiallyFocusedSections.add(sectionKey)
            }
        }
    }
}

@Composable
private fun TVWorkSelectedHeader(
    workSelected: WorkViewModel?,
    modifier: Modifier = Modifier,
) {
    workSelected?.let {
        Crossfade(
            targetState = it,
            label = "work_selected",
            animationSpec = tween(durationMillis = 500),
            modifier = modifier.padding(start = BESTVTheme.scale.s500),
        ) { work ->
            Column {
                Text(
                    text = work.title,
                    style = MaterialTheme.typography.displaySmall,
                    color = BESTVTheme.colors.white,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = "${work.releaseDate} · ${work.source}",
                    style = MaterialTheme.typography.labelSmall,
                    color = BESTVTheme.colors.secondaryText,
                    modifier = Modifier
                        .padding(top = BESTVTheme.scale.s040),
                )

                Text(
                    text = work.overview,
                    style = MaterialTheme.typography.bodyMedium,
                    color = BESTVTheme.colors.secondaryText,
                    minLines = 3,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(top = BESTVTheme.scale.s040)
                        .fillMaxWidth(0.6f),
                )
            }
        }
    }
}

@Composable
private fun TVSectionWorkList(
    content: List<ContentSection>,
    listState: LazyListState,
    onWorkSelected: (WorkViewModel) -> Unit,
    onWorkClicked: (WorkViewModel) -> Unit,
    initialFocusRequester: FocusRequester,
    requestInitialFocus: Boolean,
    focusTargetWorkId: Int?,
    modifier: Modifier = Modifier,
) {
    val firstContentIndex = content.indexOfFirst { it.works.isNotEmpty() }

    LazyColumn(
        state = listState,
        modifier = modifier.focusRestorer(),
    ) {
        itemsIndexed(
            items = content,
            key = { _, item -> item.lazyListKey() },
            contentType = { _, item -> item.lazyContentType() },
        ) { index, contentItem ->
            TVWorksRow(
                title = when (contentItem) {
                    is Genre -> contentItem.genreViewModel.name.orEmpty()
                    is TopContent -> stringResource(contentItem.type.resource)
                },
                titleStyle = MaterialTheme.typography.labelLarge,
                works = contentItem.works,
                onWorkClick = onWorkClicked,
                onWorkFocused = onWorkSelected,
                paginationEnabled = false,
                titleStartPadding = BESTVTheme.scale.s500,
                worksStartPadding = BESTVTheme.scale.s500,
                initialFocusRequester = initialFocusRequester.takeIf { index == firstContentIndex },
                initialFocusWorkId = focusTargetWorkId.takeIf { id -> contentItem.works.any { it.id == id } },
                requestInitialFocus = requestInitialFocus && index == firstContentIndex,
                modifier = Modifier
                    .fadeAtTopEdge(
                        listState = listState,
                        itemIndex = index,
                        fadeThreshold = BESTVTheme.scale.s500,
                    ),
            )
        }
    }
}

private fun Section.saveableKey(): String = when (this) {
    is Search -> "search"
    is Favorites -> "favorites"
    is Movies -> "movies"
    is TvShows -> "tv-shows"
}
