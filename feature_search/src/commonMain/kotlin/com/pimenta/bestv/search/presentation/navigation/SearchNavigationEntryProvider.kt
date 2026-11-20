package com.pimenta.bestv.search.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.pimenta.bestv.presentation.route.SearchDestination
import com.pimenta.bestv.presentation.route.navigation.NavigationEntryProvider
import com.pimenta.bestv.search.presentation.viewmodel.SearchViewModel
import org.koin.compose.viewmodel.koinViewModel

fun searchNavigationEntryProvider(
    content: @Composable (
        SearchViewModel,
        (NavKey) -> Unit,
        () -> Unit,
    ) -> Unit,
): NavigationEntryProvider = object : NavigationEntryProvider {
    override fun install(
        scope: EntryProviderScope<NavKey>,
        navigate: (NavKey) -> Unit,
        onBack: () -> Unit,
        @Suppress("UNUSED_PARAMETER") onOpenExternalUrl: (String) -> Unit,
    ) {
        scope.entry<SearchDestination> {
            content(koinViewModel(), navigate, onBack)
        }
    }
}
