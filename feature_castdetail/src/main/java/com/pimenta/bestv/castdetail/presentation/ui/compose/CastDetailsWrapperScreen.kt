/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.castdetail.presentation.ui.compose

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pimenta.bestv.castdetail.presentation.ui.compose.mobile.MobileCastDetailsScreen
import com.pimenta.bestv.castdetail.presentation.ui.compose.tv.TVCastDetailsScreen
import com.pimenta.bestv.castdetail.presentation.viewmodel.CastDetailsViewModel
import com.pimenta.bestv.presentation.theme.BESTVTheme

@Composable
fun CastDetailsWrapperScreen(
    viewModel: CastDetailsViewModel,
    openIntent: (Intent) -> Unit,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    BESTVTheme {
        if (state.isMobileDevice) {
            MobileCastDetailsScreen(
                viewModel = viewModel,
                openIntent = openIntent,
                onBack = onBack,
            )
        } else {
            TVCastDetailsScreen(
                viewModel = viewModel,
                openIntent = openIntent,
            )
        }
    }
}
