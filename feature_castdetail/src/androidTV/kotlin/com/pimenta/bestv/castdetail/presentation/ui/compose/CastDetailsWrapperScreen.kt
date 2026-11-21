/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.castdetail.presentation.ui.compose

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import com.pimenta.bestv.castdetail.presentation.ui.compose.tv.TVCastDetailsScreen
import com.pimenta.bestv.castdetail.presentation.viewmodel.CastDetailsViewModel

@Composable
fun CastDetailsWrapperScreen(
    viewModel: CastDetailsViewModel,
    navigate: (NavKey) -> Unit,
    onBack: () -> Unit,
) {
    TVCastDetailsScreen(
        viewModel = viewModel,
        navigate = navigate,
    )
}
