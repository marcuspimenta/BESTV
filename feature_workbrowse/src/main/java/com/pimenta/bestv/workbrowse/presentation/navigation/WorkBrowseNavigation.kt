/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.workbrowse.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.pimenta.bestv.route.navigation.NavigationEntryProvider
import com.pimenta.bestv.route.WorkBrowseDestination
import com.pimenta.bestv.workbrowse.presentation.ui.compose.WorkBrowseWrapperScreen
import com.pimenta.bestv.workbrowse.presentation.viewmodel.WorkBrowseViewModel
import org.koin.compose.viewmodel.koinViewModel

class WorkBrowseNavigationEntryProvider : NavigationEntryProvider {
    override fun install(
        scope: EntryProviderScope<NavKey>,
        navigate: (NavKey) -> Unit,
        onBack: () -> Unit,
    ) {
        scope.workBrowseEntryBuilder(navigate, onBack)
    }
}

fun EntryProviderScope<NavKey>.workBrowseEntryBuilder(
    navigate: (NavKey) -> Unit,
    onBack: () -> Unit,
) {
    entry<WorkBrowseDestination> {
        val viewModel = koinViewModel<WorkBrowseViewModel>()
        WorkBrowseWrapperScreen(
            viewModel = viewModel,
            navigate = navigate,
            closeScreen = onBack,
        )
    }
}
