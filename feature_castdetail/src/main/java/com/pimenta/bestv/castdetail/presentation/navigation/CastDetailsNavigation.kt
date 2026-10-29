/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.castdetail.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.pimenta.bestv.route.navigation.NavigationEntryProvider
import com.pimenta.bestv.castdetail.presentation.ui.compose.CastDetailsWrapperScreen
import com.pimenta.bestv.castdetail.presentation.viewmodel.CastDetailsViewModel
import com.pimenta.bestv.route.CastDetailsDestination
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

class CastDetailsNavigationEntryProvider : NavigationEntryProvider {
    override fun install(
        scope: EntryProviderScope<NavKey>,
        navigate: (NavKey) -> Unit,
        onBack: () -> Unit,
    ) {
        scope.castDetailsEntryBuilder(navigate, onBack)
    }
}

fun EntryProviderScope<NavKey>.castDetailsEntryBuilder(
    navigate: (NavKey) -> Unit,
    onBack: () -> Unit,
) {
    entry<CastDetailsDestination> { destination ->
        val viewModel = koinViewModel<CastDetailsViewModel>(parameters = { parametersOf(destination.castId) })
        CastDetailsWrapperScreen(
            viewModel = viewModel,
            navigate = navigate,
            onBack = onBack,
        )
    }
}
