/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.search.presentation.ui.compose

import androidx.compose.runtime.Composable
import com.pimenta.bestv.presentation.platform.DeviceCapabilities
import androidx.navigation3.runtime.NavKey
import com.pimenta.bestv.search.presentation.ui.compose.mobile.MobileSearchScreen
import com.pimenta.bestv.search.presentation.ui.compose.tv.TVSearchScreen
import com.pimenta.bestv.search.presentation.viewmodel.SearchViewModel
import org.koin.compose.koinInject

@Composable
fun SearchWrapperScreen(
    viewModel: SearchViewModel,
    navigate: (NavKey) -> Unit,
    onBack: () -> Unit,
) {
    val isMobileDevice = koinInject<DeviceCapabilities>().isMobileDevice
    if (isMobileDevice) {
        MobileSearchScreen(
            viewModel = viewModel,
            navigate = navigate,
            onBack = onBack,
        )
    } else {
        TVSearchScreen(
            viewModel = viewModel,
            navigate = navigate,
        )
    }
}
