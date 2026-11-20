package com.pimenta.bestv.workdetail.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.pimenta.bestv.presentation.route.WorkDetailsDestination
import com.pimenta.bestv.presentation.route.navigation.NavigationEntryProvider
import com.pimenta.bestv.workdetail.presentation.viewmodel.WorkDetailsViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

fun workDetailsNavigationEntryProvider(
    content: @Composable (
        WorkDetailsViewModel,
        (NavKey) -> Unit,
        (String) -> Unit,
        () -> Unit,
    ) -> Unit,
): NavigationEntryProvider = object : NavigationEntryProvider {
    override fun install(
        scope: EntryProviderScope<NavKey>,
        navigate: (NavKey) -> Unit,
        onBack: () -> Unit,
        onOpenExternalUrl: (String) -> Unit,
    ) {
        scope.entry<WorkDetailsDestination> { destination ->
            val viewModel = koinViewModel<WorkDetailsViewModel>(
                key = destination.entryId,
                parameters = { parametersOf(destination.work) },
            )
            content(viewModel, navigate, onOpenExternalUrl, onBack)
        }
    }
}
