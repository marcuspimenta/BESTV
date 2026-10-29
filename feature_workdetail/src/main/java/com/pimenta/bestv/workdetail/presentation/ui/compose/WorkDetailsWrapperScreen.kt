/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workdetail.presentation.ui.compose

import android.content.Intent
import androidx.compose.runtime.Composable
import com.pimenta.bestv.workdetail.presentation.ui.compose.mobile.MobileWorkDetailsScreen
import com.pimenta.bestv.workdetail.presentation.ui.compose.tv.TVWorkDetailsScreen
import com.pimenta.bestv.workdetail.presentation.viewmodel.WorkDetailsViewModel

@Composable
fun WorkDetailsWrapperScreen(
    viewModel: WorkDetailsViewModel,
    navigate: (androidx.navigation3.runtime.NavKey) -> Unit,
    openExternalIntent: (Intent) -> Unit,
    onBack: () -> Unit,
) {
    val isMobileDevice = viewModel.state.value.isMobileDevice
    if (isMobileDevice) {
        MobileWorkDetailsScreen(
            viewModel = viewModel,
            navigate = navigate,
            openExternalIntent = openExternalIntent,
            onBack = onBack,
        )
    } else {
        TVWorkDetailsScreen(
            viewModel = viewModel,
            navigate = navigate,
            openExternalIntent = openExternalIntent,
        )
    }
}
