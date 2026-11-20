package com.pimenta.bestv.workbrowse.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.pimenta.bestv.presentation.route.WorkBrowseDestination
import com.pimenta.bestv.presentation.route.navigation.NavigationEntryProvider
import com.pimenta.bestv.workbrowse.presentation.viewmodel.WorkBrowseViewModel
import org.koin.compose.viewmodel.koinViewModel

fun workBrowseNavigationEntryProvider(
    content: @Composable (
        WorkBrowseViewModel,
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
        scope.entry<WorkBrowseDestination> {
            content(koinViewModel(), navigate, onBack)
        }
    }
}
