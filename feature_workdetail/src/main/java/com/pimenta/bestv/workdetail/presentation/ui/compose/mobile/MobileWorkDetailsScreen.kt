/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workdetail.presentation.ui.compose.mobile

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.workdetail.R
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEffect.Navigate
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.ActionButtonClicked
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEvent.LoadData
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.ActionButton.SaveWork
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.Content.Header
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.State.Loaded
import com.pimenta.bestv.workdetail.presentation.viewmodel.WorkDetailsViewModel
import kotlinx.coroutines.flow.collectLatest
import com.pimenta.bestv.presentation.R as PresentationR

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MobileWorkDetailsScreen(
    viewModel: WorkDetailsViewModel,
    navigate: (NavKey) -> Unit,
    openExternalIntent: (Intent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val workId = state.work.id
    var listViewportTop by remember(workId) { mutableFloatStateOf(Float.NaN) }
    var headerTitleBottom by remember(workId) { mutableFloatStateOf(Float.NaN) }
    // Keep the threshold with the back-stack entry; the header may be off-screen when it returns.
    var collapseThreshold by rememberSaveable(workId) {
        mutableIntStateOf(Int.MAX_VALUE)
    }
    val showCollapsedTitle by remember(listState, collapseThreshold) {
        derivedStateOf {
            collapseThreshold != Int.MAX_VALUE &&
                (
                    listState.firstVisibleItemIndex > 0 ||
                        listState.firstVisibleItemScrollOffset >= collapseThreshold
                    )
        }
    }
    var dominantBackdropColorArgb by rememberSaveable(state.work.backdropUrl) { mutableStateOf<Int?>(null) }
    val currentBackdropUrl by rememberUpdatedState(state.work.backdropUrl)
    val dominantBackdropColor = dominantBackdropColorArgb?.let { Color(it) }
    val screenBackgroundColor = dominantBackdropColor?.toAppBarTint() ?: BESTVTheme.colors.black
    val collapsedAppBarBackgroundColor = screenBackgroundColor.darkenSrgb(0.70f)
    val appBarElevation by animateDpAsState(
        targetValue = if (showCollapsedTitle) BESTVTheme.scale.s100 else BESTVTheme.scale.s000,
        label = "workDetailsAppBarElevation",
    )
    val appBarBackgroundColor by animateColorAsState(
        targetValue = if (showCollapsedTitle) collapsedAppBarBackgroundColor else BESTVTheme.colors.transparent,
        label = "workDetailsAppBarBackground",
    )
    val saveAction = (state.state as? Loaded)
        ?.contents
        ?.filterIsInstance<Header>()
        ?.firstOrNull()
        ?.actions
        ?.filterIsInstance<SaveWork>()
        ?.firstOrNull()

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is Navigate -> navigate(effect.destination)
                is com.pimenta.bestv.workdetail.presentation.model.WorkDetailsEffect.OpenExternalIntent -> {
                    openExternalIntent(effect.intent)
                }
            }
        }
    }
    LaunchedEffect(Unit) { viewModel.handleEvent(LoadData) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = screenBackgroundColor,
    ) { contentPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            MobileWorkDetailsContent(
                state = state,
                backgroundColor = screenBackgroundColor,
                cardBackgroundColor = collapsedAppBarBackgroundColor,
                listState = listState,
                contentPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding()),
                onEvent = viewModel::handleEvent,
                onListViewportTopChanged = {
                    if (!listViewportTop.isFinite()) {
                        listViewportTop = it
                        if (
                            headerTitleBottom.isFinite() &&
                            collapseThreshold == Int.MAX_VALUE &&
                            listState.firstVisibleItemIndex == 0
                        ) {
                            // Restored item bounds include the current offset; recover the zero-scroll threshold.
                            val measuredThreshold =
                                headerTitleBottom - it + listState.firstVisibleItemScrollOffset
                            collapseThreshold = measuredThreshold.toInt().coerceAtLeast(0)
                        }
                    }
                },
                onHeaderTitleBottomChanged = {
                    if (
                        !headerTitleBottom.isFinite() &&
                        collapseThreshold == Int.MAX_VALUE &&
                        listState.firstVisibleItemIndex == 0
                    ) {
                        headerTitleBottom = it
                        if (listViewportTop.isFinite()) {
                            // Restored item bounds include the current offset; recover the zero-scroll threshold.
                            val measuredThreshold =
                                it - listViewportTop + listState.firstVisibleItemScrollOffset
                            collapseThreshold = measuredThreshold.toInt().coerceAtLeast(0)
                        }
                    }
                },
                onBackdropColorChanged = { imageUrl, color ->
                    if (imageUrl == currentBackdropUrl) dominantBackdropColorArgb = color.toArgb()
                },
            )
            TopAppBar(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .shadow(elevation = appBarElevation),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(PresentationR.string.back),
                            tint = BESTVTheme.colors.white,
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
                            text = state.work.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = BESTVTheme.colors.white,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                },
                actions = {
                    saveAction?.let { action ->
                        IconButton(onClick = { viewModel.handleEvent(ActionButtonClicked(action)) }) {
                            FavoriteIcon(action)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = appBarBackgroundColor,
                    titleContentColor = BESTVTheme.colors.white,
                    navigationIconContentColor = BESTVTheme.colors.white,
                    actionIconContentColor = BESTVTheme.colors.white,
                ),
                windowInsets = TopAppBarDefaults.windowInsets,
            )
        }
    }
}

@Composable
private fun FavoriteIcon(action: SaveWork) {
    Icon(
        imageVector = if (action.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
        contentDescription = stringResource(
            if (action.isFavorite) R.string.remove_favorites else R.string.save_favorites,
        ),
        tint = BESTVTheme.colors.white,
    )
}

private fun Color.toAppBarTint(): Color {
    val maxChannel = maxOf(red, green, blue)
    if (maxChannel <= 0f) return Color.Black

    val scale = 0.38f / maxChannel
    return copy(
        red = (red * scale).coerceAtMost(0.38f),
        green = (green * scale).coerceAtMost(0.38f),
        blue = (blue * scale).coerceAtMost(0.38f),
        alpha = 1f,
    )
}

private fun Color.darkenSrgb(factor: Float): Color {
    val argb = toArgb()
    val alpha = (argb ushr 24) and 0xFF
    val red = (((argb shr 16) and 0xFF) * factor).toInt()
    val green = (((argb shr 8) and 0xFF) * factor).toInt()
    val blue = ((argb and 0xFF) * factor).toInt()

    return Color((alpha shl 24) or (red shl 16) or (green shl 8) or blue)
}
