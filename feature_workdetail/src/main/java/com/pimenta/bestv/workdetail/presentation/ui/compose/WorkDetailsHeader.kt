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

package com.pimenta.bestv.workdetail.presentation.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.pimenta.bestv.model.presentation.model.WorkType
import com.pimenta.bestv.model.presentation.model.WorkViewModel
import com.pimenta.bestv.presentation.ui.compose.WorkCard
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.ActionButton
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.ActionButton.SaveWork
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.ActionButton.ScrollToCasts
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.ActionButton.ScrollToRecommendedWorks
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.ActionButton.ScrollToReviews
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.ActionButton.ScrollToSimilarWorks
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.ActionButton.ScrollToVideos
import com.pimenta.bestv.workdetail.presentation.model.WorkDetailsState.Content

@Composable
fun WorkDetailsHeader(
    work: WorkViewModel,
    actions: List<ActionButton>,
    actionClicked: (ActionButton) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row (
        modifier = modifier
            .fillMaxWidth()
            .padding(48.dp),
    ){
        WorkCard(
            work = work,
            width = 220.dp,
            includeWorkTitle = false
        )

        Column(
            modifier = Modifier.padding(start = 28.dp),
        ) {
            // Title
            Text(
                text = work.title ?: "Untitled",
                style = MaterialTheme.typography.displayLarge,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(26.dp))

            ActionButtonsRow(
                actions = actions,
                actionClicked = actionClicked
            )

            Spacer(modifier = Modifier.height(26.dp))

            work.releaseDate?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            work.overview?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            work.source?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun ActionButtonsRow(
    actions: List<ActionButton>,
    actionClicked: (ActionButton) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(
            items = actions,
            key = { it.title }
        ) { action ->
            Button(
                onClick = { actionClicked(action) }
            ) {
                Text(text = action.title)
            }
        }
    }
}

@Preview
@Composable
private fun WorkDetailsHeaderPreview() {
    MaterialTheme {
        WorkDetailsHeader(
            work = WorkViewModel(
                id = 1,
                overview = "Overview",
                title = "The Dark Knight",
                originalTitle = "The Dark Knight",
                type = WorkType.MOVIE,
                posterUrl = null,
                source = "TMDB"
            ),
            actions = listOf(
                SaveWork(true),
                ScrollToVideos,
                ScrollToCasts,
                ScrollToRecommendedWorks,
                ScrollToSimilarWorks,
                ScrollToReviews
            ),
            actionClicked = {}
        )
    }
}
