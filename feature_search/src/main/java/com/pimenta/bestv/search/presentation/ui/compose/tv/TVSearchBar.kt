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

package com.pimenta.bestv.search.presentation.ui.compose.tv

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.search.R

@Composable
fun TVSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onQuerySubmit: (String) -> Unit,
    onClear: () -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }

    // Request focus when the search bar is first displayed
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier =
                modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(
                    horizontal = BESTVTheme.scale.s240,
                    vertical = BESTVTheme.scale.s120,
                ).focusRequester(focusRequester),
        placeholder = {
            Text(
                text = placeholder,
                color = BESTVTheme.colors.searchPlaceholder,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.search_action),
                tint = BESTVTheme.colors.secondaryText,
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.clear_search),
                        tint = BESTVTheme.colors.secondaryText,
                    )
                }
            }
        },
        singleLine = true,
        keyboardOptions =
            KeyboardOptions(
                imeAction = ImeAction.Search,
            ),
        keyboardActions =
            KeyboardActions(
                onSearch = { onQuerySubmit(query) },
            ),
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedTextColor = BESTVTheme.colors.white,
                unfocusedTextColor = BESTVTheme.colors.white,
                cursorColor = BESTVTheme.colors.white,
                focusedBorderColor = BESTVTheme.colors.searchFocusedBorder,
                unfocusedBorderColor = BESTVTheme.colors.searchUnfocusedBorder,
                focusedContainerColor = BESTVTheme.colors.searchFieldSurface,
                unfocusedContainerColor = BESTVTheme.colors.searchFieldSurface,
            ),
    )
}
