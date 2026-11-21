/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workdetail.presentation.ui.compose

import androidx.compose.runtime.Composable
import com.pimenta.bestv.workdetail.presentation.ui.compose.tv.TVWorkDetailsScreen
import com.pimenta.bestv.workdetail.presentation.viewmodel.WorkDetailsViewModel

@Composable
fun WorkDetailsWrapperScreen(
    viewModel: WorkDetailsViewModel,
    navigate: (androidx.navigation3.runtime.NavKey) -> Unit,
    openExternalUrl: (String) -> Unit,
    onBack: () -> Unit,
) {
    TVWorkDetailsScreen(
        viewModel = viewModel,
        navigate = navigate,
        openExternalUrl = openExternalUrl,
    )
}
