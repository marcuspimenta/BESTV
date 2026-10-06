/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workdetail.presentation.ui.compose

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.workdetail.presentation.ui.compose.mobile.MobileWorkDetailsScreen
import com.pimenta.bestv.workdetail.presentation.ui.compose.tv.TVWorkDetailsScreen
import com.pimenta.bestv.workdetail.presentation.viewmodel.WorkDetailsViewModel

@Composable
fun WorkDetailsWrapperScreen(
    viewModel: WorkDetailsViewModel,
    openIntent: (Intent) -> Unit,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    BESTVTheme {
        if (state.isMobileDevice) {
            MobileWorkDetailsScreen(
                viewModel = viewModel,
                openIntent = openIntent,
                onBack = onBack,
            )
        } else {
            TVWorkDetailsScreen(
                viewModel = viewModel,
                openIntent = openIntent,
            )
        }
    }
}
