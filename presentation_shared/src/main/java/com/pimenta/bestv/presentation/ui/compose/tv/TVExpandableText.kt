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

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.pimenta.bestv.presentation.R
import com.pimenta.bestv.presentation.theme.BESTVTheme

private const val DEFAULT_MAX_LINES_COLLAPSED = 4

@Composable
fun TVExpandableText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = BESTVTheme.colors.secondaryText,
    maxLinesCollapsed: Int = DEFAULT_MAX_LINES_COLLAPSED,
) {
    var isExpanded by remember { mutableStateOf(false) }
    var hasTextOverflow by remember { mutableStateOf(false) }

    Column(modifier = modifier.animateContentSize()) {
        Text(
            text = text,
            style = style,
            color = color,
            maxLines = if (isExpanded) Int.MAX_VALUE else maxLinesCollapsed,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { textLayoutResult ->
                if (!isExpanded) {
                    hasTextOverflow = textLayoutResult.hasVisualOverflow
                }
            },
        )

        if (hasTextOverflow || isExpanded) {
            Surface(
                onClick = { isExpanded = !isExpanded },
                colors = ClickableSurfaceDefaults.colors(
                    containerColor = BESTVTheme.colors.transparent,
                    focusedContainerColor = BESTVTheme.colors.focusSurface,
                ),
                modifier = Modifier
                    .padding(
                        start = BESTVTheme.scale.s040,
                        top = BESTVTheme.scale.s040,
                    ),
            ) {
                Text(
                    text = stringResource(if (isExpanded) R.string.show_less else R.string.read_more),
                    style = MaterialTheme.typography.labelLarge,
                    color = BESTVTheme.colors.white,
                    modifier = Modifier
                        .padding(
                            horizontal = BESTVTheme.scale.s040,
                            vertical = BESTVTheme.scale.s020,
                        ),
                )
            }
        }
    }
}

@Preview
@Composable
private fun TVExpandableTextPreview() {
    MaterialTheme {
        TVExpandableText(
            text = "This is a long text that should be expandable. ".repeat(10),
        )
    }
}
