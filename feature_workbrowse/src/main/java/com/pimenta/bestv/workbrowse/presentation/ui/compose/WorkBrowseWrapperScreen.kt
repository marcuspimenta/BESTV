/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workbrowse.presentation.ui.compose

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import com.pimenta.bestv.workbrowse.presentation.ui.compose.mobile.MobileBrowseScreen
import com.pimenta.bestv.workbrowse.presentation.ui.compose.tv.TVWorkBrowseScreen
import com.pimenta.bestv.workbrowse.presentation.viewmodel.WorkBrowseViewModel

@Composable
fun WorkBrowseWrapperScreen(
    viewModel: WorkBrowseViewModel,
    navigate: (NavKey) -> Unit,
    closeScreen: () -> Unit,
) {
    val isMobileDevice = viewModel.state.value.isMobileDevice
    if (isMobileDevice) {
        MobileBrowseScreen(
            viewModel = viewModel,
            navigate = navigate,
            closeScreen = closeScreen,
        )
    } else {
        TVWorkBrowseScreen(
            viewModel = viewModel,
            closeScreen = closeScreen,
            navigate = navigate,
        )
    }
}
