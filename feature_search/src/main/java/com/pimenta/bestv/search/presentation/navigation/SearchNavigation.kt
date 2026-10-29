/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.pimenta.bestv.search.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.pimenta.bestv.route.navigation.NavigationEntryProvider
import com.pimenta.bestv.route.SearchDestination
import com.pimenta.bestv.search.presentation.ui.compose.SearchWrapperScreen
import com.pimenta.bestv.search.presentation.viewmodel.SearchViewModel
import org.koin.compose.viewmodel.koinViewModel

class SearchNavigationEntryProvider : NavigationEntryProvider {
    override fun install(
        scope: EntryProviderScope<NavKey>,
        navigate: (NavKey) -> Unit,
        onBack: () -> Unit,
    ) {
        scope.searchEntryBuilder(navigate, onBack)
    }
}

fun EntryProviderScope<NavKey>.searchEntryBuilder(
    navigate: (NavKey) -> Unit,
    onBack: () -> Unit,
) {
    entry<SearchDestination> {
        val viewModel = koinViewModel<SearchViewModel>()
        SearchWrapperScreen(
            viewModel = viewModel,
            navigate = navigate,
            onBack = onBack,
        )
    }
}
