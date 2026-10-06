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

package com.pimenta.bestv.workdetail.presentation.ui.compose.tv

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.presentation.ui.compose.tv.TVExpandableText
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.ActionButton
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.ActionButton.SaveWork
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.ActionButton.ScrollToCasts
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.ActionButton.ScrollToRecommendedWorks
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.ActionButton.ScrollToReviews
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.ActionButton.ScrollToSimilarWorks
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.ActionButton.ScrollToVideos
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.Content.Header

@Composable
fun TVWorkDetailsHeader(
    work: WorkViewModel,
    header: Header,
    actionClicked: (ActionButton) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(
                    start = BESTVTheme.scale.s240,
                    top = BESTVTheme.scale.s240,
                    end = BESTVTheme.scale.s240,
                    bottom = BESTVTheme.scale.s060,
                ),
    ) {
        Column {
            Text(
                text = work.title,
                style = MaterialTheme.typography.displaySmall,
                color = BESTVTheme.colors.white,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            TVActionButtonsRow(
                actions = header.actions,
                actionClicked = actionClicked,
                modifier = Modifier.padding(top = BESTVTheme.scale.s100),
            )

            Text(
                text = "${work.releaseDate} · ${work.voteAverage} · ${work.source}",
                style = MaterialTheme.typography.bodyMedium,
                color = BESTVTheme.colors.secondaryText,
                modifier = Modifier.padding(top = BESTVTheme.scale.s100),
            )

            TVExpandableText(
                text = work.overview,
                modifier =
                    Modifier
                        .padding(top = BESTVTheme.scale.s090)
                        .fillMaxWidth(0.6f),
            )

            header.watchProviders?.let {
                TVWatchProvidersRow(
                    watchProviders = it,
                    modifier = Modifier.padding(top = BESTVTheme.scale.s090),
                )
            }
        }
    }
}

@Composable
private fun TVActionButtonsRow(
    actions: List<ActionButton>,
    actionClicked: (ActionButton) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(BESTVTheme.scale.s060),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        items(
            items = actions,
            key = { it.id },
        ) { action ->
            Button(
                onClick = { actionClicked(action) },
            ) {
                Text(text = action.title)
            }
        }
    }
}

@Preview
@Composable
private fun TVWorkDetailsHeaderPreview() {
    MaterialTheme {
        TVWorkDetailsHeader(
            work =
                WorkViewModel(
                    id = 1,
                    overview = "Overview",
                    title = "The Dark Knight",
                    originalTitle = "The Dark Knight",
                    type = WorkType.MOVIE,
                    posterUrl = "",
                    source = "TMDB",
                    originalLanguage = "",
                    backdropUrl = "",
                    releaseDate = "",
                    voteAverage = 0f,
                    isFavorite = false,
                ),
            header =
                Header(
                    actions =
                        listOf(
                            SaveWork(true),
                            ScrollToVideos,
                            ScrollToCasts,
                            ScrollToRecommendedWorks,
                            ScrollToSimilarWorks,
                            ScrollToReviews,
                        ),
                    watchProviders = null,
                ),
            actionClicked = {},
        )
    }
}
