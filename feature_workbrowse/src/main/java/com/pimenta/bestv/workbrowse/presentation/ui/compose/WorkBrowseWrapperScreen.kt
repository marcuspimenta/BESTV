/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workbrowse.presentation.ui.compose

import android.content.Intent
import androidx.compose.runtime.Composable
import com.pimenta.bestv.presentation.theme.BESTVTheme
import com.pimenta.bestv.workbrowse.presentation.ui.compose.mobile.MobileBrowseScreen
import com.pimenta.bestv.workbrowse.presentation.ui.compose.tv.TVWorkBrowseScreen
import com.pimenta.bestv.workbrowse.presentation.viewmodel.WorkBrowseViewModel

@Composable
fun WorkBrowseWrapperScreen(
    viewModel: WorkBrowseViewModel,
    openIntent: (Intent) -> Unit,
    closeScreen: () -> Unit,
) {
    val isMobileDevice = viewModel.state.value.isMobileDevice
    BESTVTheme {
        if (isMobileDevice) {
            MobileBrowseScreen(
                viewModel = viewModel,
                openIntent = openIntent,
                closeScreen = closeScreen,
            )
        } else {
            TVWorkBrowseScreen(
                viewModel = viewModel,
                closeScreen = closeScreen,
                openIntent = openIntent,
            )
        }
    }
}
