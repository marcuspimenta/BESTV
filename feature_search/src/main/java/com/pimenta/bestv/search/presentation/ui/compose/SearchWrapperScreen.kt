/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.search.presentation.ui.compose

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.search.presentation.ui.compose.mobile.MobileSearchScreen
import com.pimenta.bestv.search.presentation.ui.compose.tv.TVSearchScreen
import com.pimenta.bestv.search.presentation.viewmodel.SearchViewModel

@Composable
fun SearchWrapperScreen(
    viewModel: SearchViewModel,
    openIntent: (Intent) -> Unit,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    BESTVTheme {
        if (state.isMobileDevice) {
            MobileSearchScreen(
                viewModel = viewModel,
                openIntent = openIntent,
                onBack = onBack,
            )
        } else {
            TVSearchScreen(
                viewModel = viewModel,
                openIntent = openIntent,
            )
        }
    }
}
