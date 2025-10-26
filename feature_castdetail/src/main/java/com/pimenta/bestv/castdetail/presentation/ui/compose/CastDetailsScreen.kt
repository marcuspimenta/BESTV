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

package com.pimenta.bestv.castdetail.presentation.ui.compose

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.CircularProgressIndicator
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.pimenta.bestv.castdetail.presentation.model.CastDetailsEffect
import com.pimenta.bestv.castdetail.presentation.model.CastDetailsEvent
import com.pimenta.bestv.castdetail.presentation.model.CastDetailsState
import com.pimenta.bestv.castdetail.presentation.viewmodel.CastDetailsViewModel
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.ui.compose.WorksRow
import kotlinx.coroutines.flow.collectLatest

/**
 * Main screen for displaying cast member details.
 *
 * This composable integrates with the MVI architecture:
 * - Collects state from ViewModel using collectAsStateWithLifecycle
 * - Handles loading, content, and error states
 * - Displays cast header, movies row, and TV shows row
 * - Manages side effects (navigation, errors)
 *
 * @param viewModel The ViewModel managing cast details state
 * @param onWorkClick Callback invoked when a work is clicked
 * @param onError Callback invoked when an error occurs
 * @param modifier Optional modifier for customization
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun CastDetailsScreen(
    viewModel: CastDetailsViewModel,
    openIntent: (Intent) -> Unit,
    showError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Collect side effects
    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is CastDetailsEffect.ShowError -> showError()
                is CastDetailsEffect.OpenIntent -> openIntent(effect.intent)
            }
        }
    }

    // Load data on first composition
    LaunchedEffect(Unit) {
        viewModel.handleEvent(CastDetailsEvent.LoadData)
    }

    CastDetailsContent(
        state = state,
        onWorkClick = { work ->
            viewModel.handleEvent(CastDetailsEvent.WorkClicked(work))
        },
        modifier = modifier
    )
}

/**
 * Content component for cast details screen.
 *
 * Handles different UI states:
 * - Loading: Shows centered progress indicator
 * - Error: Handled via side effect
 * - Content: Displays scrollable cast details with movies and TV shows
 *
 * @param state Current UI state
 * @param onWorkClick Callback invoked when a work is clicked
 * @param modifier Optional modifier for customization
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun CastDetailsContent(
    state: CastDetailsState,
    onWorkClick: (WorkViewModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E)
                    )
                )
            )
    ) {
        when {
            state.isLoading -> {
                // Loading state
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            state.castDetails != null -> {
                // Content state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Cast header
                    CastDetailsHeader(
                        cast = state.castDetails
                    )

                    // Movies section
                    if (state.movies.isNotEmpty()) {
                        WorksRow(
                            title = "Movies",
                            works = state.movies,
                            onWorkClick = onWorkClick
                        )
                    }

                    // TV Shows section
                    if (state.tvShows.isNotEmpty()) {
                        WorksRow(
                            title = "TV Shows",
                            works = state.tvShows,
                            onWorkClick = onWorkClick
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun CastDetailsScreenPreview() {
    MaterialTheme {
        CastDetailsContent(
            state = CastDetailsState(
                cast = CastViewModel(
                    id = 1,
                    name = "Christian Bale",
                    character = null,
                    birthday = "January 30, 1974",
                    deathDay = null,
                    biography = "Christian Charles Philip Bale is an English actor.",
                    thumbnailUrl = null,
                    source = "TMDB"
                ),
                isLoading = false,
                castDetails = CastViewModel(
                    id = 1,
                    name = "Christian Bale",
                    character = null,
                    birthday = "January 30, 1974",
                    deathDay = null,
                    biography = "Christian Charles Philip Bale is an English actor. Known for his versatility and physical transformations for his roles.",
                    thumbnailUrl = null,
                    source = "TMDB"
                ),
                movies = listOf(
                    WorkViewModel(
                        id = 1,
                        title = "The Dark Knight",
                        originalTitle = "The Dark Knight",
                        posterUrl = null,
                        type = WorkType.MOVIE,
                        source = "TMDB"
                    ),
                    WorkViewModel(
                        id = 2,
                        title = "Batman Begins",
                        originalTitle = "Batman Begins",
                        posterUrl = null,
                        type = WorkType.MOVIE,
                        source = "TMDB"
                    )
                ),
                tvShows = emptyList()
            ),
            onWorkClick = {}
        )
    }
}
