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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.MaterialTheme
import com.pimenta.bestv.castdetail.presentation.model.CastDetailsEffect.OpenIntent
import com.pimenta.bestv.castdetail.presentation.model.CastDetailsEvent
import com.pimenta.bestv.castdetail.presentation.model.CastDetailsState
import com.pimenta.bestv.castdetail.presentation.viewmodel.CastDetailsViewModel
import com.pimenta.bestv.model.presentation.model.CastViewModel
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.ui.compose.ErrorScreen
import com.pimenta.bestv.presentation.ui.compose.Loading
import com.pimenta.bestv.presentation.ui.compose.WorksRow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CastDetailsScreen(
    viewModel: CastDetailsViewModel,
    openIntent: (Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is OpenIntent -> openIntent(effect.intent)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.handleEvent(CastDetailsEvent.LoadData)
    }

    CastDetailsContent(
        state = state,
        onWorkClick = { viewModel.handleEvent(CastDetailsEvent.WorkClicked(it)) },
        onRetryClicked = { viewModel.handleEvent(CastDetailsEvent.LoadData) },
        modifier = modifier
    )
}

@Composable
private fun CastDetailsContent(
    state: CastDetailsState,
    onWorkClick: (WorkViewModel) -> Unit,
    onRetryClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        when (state) {
            is CastDetailsState.Loading -> {
                Loading(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is CastDetailsState.Error -> {
                ErrorScreen(
                    onRetryClick = onRetryClicked,
                    modifier = modifier
                )
            }

            is CastDetailsState.Loaded -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 48.dp)
                ) {
                    CastDetailsHeader(
                        cast = state.cast
                    )

                    if (state.movies.isNotEmpty()) {
                        WorksRow(
                            title = "Movies",
                            works = state.movies,
                            onWorkClick = onWorkClick
                        )
                    }

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
            state = CastDetailsState.Loaded(
                cast = CastViewModel(
                    id = 1,
                    name = "Christian Bale",
                    character = "Bruce Wayne / Batman",
                    thumbnailUrl = "",
                    source = "TMDB",
                    birthday = "",
                    deathDay = "",
                    biography = ""
                ),
                movies = listOf(
                    WorkViewModel(
                        id = 1,
                        title = "The Dark Knight The Dark Knight The Dark Knight The Dark Knight",
                        originalTitle = "The Dark Knight",
                        posterUrl = "",
                        type = WorkType.MOVIE,
                        source = "TMDB",
                        originalLanguage = "",
                        overview = "",
                        backdropUrl = "",
                        releaseDate = "",
                        isFavorite = false
                    ),
                    WorkViewModel(
                        id = 2,
                        title = "The Dark Knight The Dark Knight The Dark Knight The Dark Knight",
                        originalTitle = "The Dark Knight",
                        posterUrl = "",
                        type = WorkType.MOVIE,
                        source = "TMDB",
                        originalLanguage = "",
                        overview = "",
                        backdropUrl = "",
                        releaseDate = "",
                        isFavorite = false
                    )
                ),
                tvShows = emptyList()
            ),
            onWorkClick = {},
            onRetryClicked = {},
        )
    }
}
