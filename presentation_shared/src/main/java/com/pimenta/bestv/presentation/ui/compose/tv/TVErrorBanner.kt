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

package com.pimenta.bestv.presentation.ui.compose.tv

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import com.pimenta.bestv.presentation.R
import com.pimenta.bestv.presentation.theme.BESTVTheme

/**
 * A dismissible error banner component for Android TV.
 * Displays an error message at the bottom of the screen with a dismiss button.
 *
 * @param errorMessage The error message to display. If null, the banner is hidden.
 * @param onDismiss Callback invoked when the dismiss button is clicked.
 * @param modifier Modifier to be applied to the banner container.
 */
@Composable
fun TVErrorBanner(
    errorMessage: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }

    AnimatedVisibility(
        visible = errorMessage != null,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier,
    ) {
        errorMessage?.let { message ->
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

            Surface(
                shape = MaterialTheme.shapes.medium,
                colors = SurfaceDefaults.colors(
                    containerColor = BESTVTheme.colors.errorBannerSurface,
                    contentColor = BESTVTheme.colors.white,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = BESTVTheme.scale.s240,
                        vertical = BESTVTheme.scale.s160,
                    ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(BESTVTheme.scale.s120),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .weight(1f),
                    )

                    Spacer(
                        modifier = Modifier
                            .width(BESTVTheme.scale.s080),
                    )

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .focusRequester(focusRequester),
                    ) {
                        Text(text = stringResource(R.string.dismiss))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun TVErrorBannerPreview() {
    MaterialTheme {
        TVErrorBanner(
            errorMessage = "Could not save/unsave the work.",
            onDismiss = {},
        )
    }
}

@Preview
@Composable
private fun TVErrorBannerHiddenPreview() {
    MaterialTheme {
        TVErrorBanner(
            errorMessage = null,
            onDismiss = {},
        )
    }
}
