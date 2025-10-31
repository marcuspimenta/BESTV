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

package com.pimenta.bestv.presentation.ui.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text

/**
 * A dismissible error banner component for Android TV.
 * Displays an error message at the bottom of the screen with a dismiss button.
 *
 * @param errorMessage The error message to display. If null, the banner is hidden.
 * @param onDismiss Callback invoked when the dismiss button is clicked.
 * @param modifier Modifier to be applied to the banner container.
 */
@Composable
fun ErrorBanner(
    errorMessage: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    AnimatedVisibility(
        visible = errorMessage != null,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier
    ) {
        errorMessage?.let { message ->
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

            Surface(
                shape = MaterialTheme.shapes.medium,
                colors = SurfaceDefaults.colors(
                    containerColor = Color(0xFFD32F2F).copy(alpha = 0.95f),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp, vertical = 32.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.focusRequester(focusRequester)
                    ) {
                        Text(text = "Dismiss")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ErrorBannerPreview() {
    MaterialTheme {
        ErrorBanner(
            errorMessage = "Could not save/unsave the work.",
            onDismiss = {}
        )
    }
}

@Preview
@Composable
private fun ErrorBannerHiddenPreview() {
    MaterialTheme {
        ErrorBanner(
            errorMessage = null,
            onDismiss = {}
        )
    }
}
