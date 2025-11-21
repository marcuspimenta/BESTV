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

package com.pimenta.bestv.workbrowse.presentation.ui.compose

import android.content.Intent
import android.widget.VideoView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.ModalNavigationDrawer
import androidx.tv.material3.NavigationDrawerItem
import androidx.tv.material3.Text
import androidx.tv.material3.rememberDrawerState
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.ui.compose.ErrorScreen
import com.pimenta.bestv.presentation.ui.compose.SlideInFromBottom
import com.pimenta.bestv.presentation.ui.compose.BackgroundScreen
import com.pimenta.bestv.presentation.ui.compose.WorksRow
import com.pimenta.bestv.presentation.ui.compose.fadeAtTopEdge
import com.pimenta.bestv.workbrowse.presentation.model.ContentSection
import com.pimenta.bestv.workbrowse.presentation.model.ContentSection.Genre
import com.pimenta.bestv.workbrowse.presentation.model.ContentSection.TopContent
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseEffect
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseEvent
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section
import com.pimenta.bestv.workbrowse.presentation.model.WorkBrowseState.Section.About
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
fun WorkBrowseScreen(
    viewModel: WorkBrowseViewModel,
    closeScreen: () -> Unit,
    openIntent: (Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is WorkBrowseEffect.CloseScreen -> closeScreen()
                is WorkBrowseEffect.Navigate -> openIntent(effect.intent)
            }
        }
    }

    WorkBrowseContent(
        state = state,
        onBackClicked = { viewModel.handleEvent(WorkBrowseEvent.BackClicked) },
        onSplashAnimationFinished = { viewModel.handleEvent(WorkBrowseEvent.SplashAnimationFinished) },
        onSectionClicked = { viewModel.handleEvent(WorkBrowseEvent.SectionClicked(it)) },
        onWorkSelected = { viewModel.handleEvent(WorkBrowseEvent.WorkSelected(it)) },
        onWorkClicked = { viewModel.handleEvent(WorkBrowseEvent.WorkClicked(it)) },
        onRetryClicked = { viewModel.handleEvent(WorkBrowseEvent.RetryLoad) },
        modifier = modifier
    )
}

@Composable
private fun WorkBrowseContent(
    state: WorkBrowseState,
    onBackClicked: () -> Unit,
    onSplashAnimationFinished: () -> Unit,
    onSectionClicked: (Int) -> Unit,
    onWorkSelected: (WorkViewModel) -> Unit,
    onWorkClicked: (WorkViewModel) -> Unit,
    onRetryClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (val contentState = state.state) {
            is Loading -> {
                LoadingSplashScreen(
                    onSplashAnimationFinished = onSplashAnimationFinished,
                    modifier = Modifier.fillMaxSize()
                )
            }

            is Error -> {
                ErrorScreen(
                    onRetryClick = onRetryClicked,
                    modifier = Modifier.fillMaxSize()
                )
            }

            is Loaded -> {
                BrowseSections(
                    workSelected = contentState.workSelected,
                    selectedSectionIndex = contentState.selectedSectionIndex,
                    sections = contentState.sections,
                    onBackClicked = onBackClicked,
                    onSectionClicked = onSectionClicked,
                    onWorkSelected = onWorkSelected,
                    onWorkClicked = onWorkClicked,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun LoadingSplashScreen(
    onSplashAnimationFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            VideoView(context).apply {
                setOnCompletionListener { onSplashAnimationFinished() }
                setVideoURI(SPLASH_ANIMATION_FILE.toUri())
                start()
            }
        }
    )
}

@Composable
private fun BrowseSections(
    workSelected: WorkViewModel?,
    selectedSectionIndex: Int,
    sections: List<Section>,
    onBackClicked: () -> Unit,
    onWorkSelected: (WorkViewModel) -> Unit,
    onSectionClicked: (Int) -> Unit,
    onWorkClicked: (WorkViewModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val focusRequester = FocusRequester()

    BackHandler {
        if (drawerState.currentValue == DrawerValue.Open) {
            drawerState.setValue(DrawerValue.Closed)
        } else {
            onBackClicked()
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        BackgroundScreen(
            backdropUrl = workSelected?.backdropUrl
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
                        }
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.Center),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                        tint = Color.White
                                    )
                                },
                                content = {
                                    Text(
                                        text = stringResource(section.titleRes),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.White
                                    )
                                }
                            )
                        }
                    }
                }
            },
            scrimBrush = Brush.horizontalGradient(listOf(Color.Black, Color.Transparent)),
            modifier = modifier
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onFocusChanged { focusState ->
                        if (focusState.hasFocus && drawerState.currentValue == DrawerValue.Open) {
                            scope.launch {
                                drawerState.setValue(DrawerValue.Closed)
                            }
                        }
                    }
            ) {
                Section(
                    drawerValue = drawerState.currentValue,
                    focusRequester = focusRequester,
                    section = sections[selectedSectionIndex],
                    workSelected = workSelected,
                    onWorkSelected = onWorkSelected,
                    onWorkClicked = onWorkClicked,
                    modifier = Modifier.focusRequester(focusRequester)
                )

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(80.dp)
                        .background(Color.Black.copy(alpha = 0.80f))
                        .align(Alignment.TopStart)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.80f),
                                )
                            )
                        )
                        .align(BottomStart)
                )
            }
        }
    }
}

@Composable
private fun Section(
    drawerValue: DrawerValue,
    focusRequester: FocusRequester,
    section: Section,
    workSelected: WorkViewModel?,
    onWorkSelected: (WorkViewModel) -> Unit,
    onWorkClicked: (WorkViewModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (section) {
        is Search -> Unit
        is Favorites -> SectionWorks(
            drawerValue = drawerValue,
            focusRequester = focusRequester,
            workSelected = workSelected,
            content = section.content,
            onWorkSelected = onWorkSelected,
            onWorkClicked = onWorkClicked,
            modifier = modifier
        )

        is Movies -> SectionWorks(
            drawerValue = drawerValue,
            focusRequester = focusRequester,
            workSelected = workSelected,
            content = section.content,
            onWorkSelected = onWorkSelected,
            onWorkClicked = onWorkClicked,
            modifier = modifier
        )

        is TvShows -> SectionWorks(
            drawerValue = drawerValue,
            focusRequester = focusRequester,
            workSelected = workSelected,
            content = section.content,
            onWorkSelected = onWorkSelected,
            onWorkClicked = onWorkClicked,
            modifier = modifier
        )

        is About -> Unit
    }
}

@Composable
private fun SectionWorks(
    drawerValue: DrawerValue,
    focusRequester: FocusRequester,
    workSelected: WorkViewModel?,
    content: List<ContentSection>,
    onWorkSelected: (WorkViewModel) -> Unit,
    onWorkClicked: (WorkViewModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    SlideInFromBottom(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .fillMaxHeight(0.45f)
            ) {
                WorkSelectedHeader(
                    workSelected = workSelected,
                    modifier = Modifier.align(BottomStart)
                )
            }

            SectionWorkList(
                content = content,
                listState = listState,
                onWorkSelected = onWorkSelected,
                onWorkClicked = onWorkClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }

        LaunchedEffect(drawerValue) {
            if (drawerValue == DrawerValue.Closed) {
                focusRequester.requestFocus()
            }
        }
    }
}

@Composable
private fun WorkSelectedHeader(
    workSelected: WorkViewModel?,
    modifier: Modifier = Modifier
) {
    workSelected?.let {
        Crossfade(
            targetState = it,
            label = "work_selected",
            animationSpec = tween(durationMillis = 500),
            modifier = modifier.padding(start = 100.dp)
        ) { work ->
            Column {
                Text(
                    text = work.title,
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = "${work.releaseDate} · ${work.source}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 8.dp)
                )

                Text(
                    text = work.overview,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(0.6f)
                )
            }
        }
    }
}

@Composable
private fun SectionWorkList(
    content: List<ContentSection>,
    listState: LazyListState,
    onWorkSelected: (WorkViewModel) -> Unit,
    onWorkClicked: (WorkViewModel) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = listState,
        modifier = modifier
    ) {
        itemsIndexed(
            items = content,
            key = { _, item -> item.hashCode() }
        ) { index, contentItem ->
            WorksRow(
                title = when (contentItem) {
                    is Genre -> contentItem.genreViewModel.name.orEmpty()
                    is TopContent -> stringResource(contentItem.type.resource)
                },
                titleStyle = MaterialTheme.typography.labelLarge,
                works = contentItem.works,
                includeWorkTitle = false,
                onWorkClick = onWorkClicked,
                onWorkFocused = onWorkSelected,
                isLoadingMore = contentItem.page.isLoadingMore,
                titleStartPadding = 100.dp,
                worksStartPadding = 100.dp,
                onLoadMore = {},
                modifier = Modifier.fadeAtTopEdge(
                    listState = listState,
                    itemIndex = index,
                    fadeThreshold = 100f
                )
            )
        }
    }
}