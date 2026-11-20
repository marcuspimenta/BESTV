package com.pimenta.bestv.castdetail.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.pimenta.bestv.castdetail.presentation.viewmodel.CastDetailsViewModel
import com.pimenta.bestv.presentation.route.CastDetailsDestination
import com.pimenta.bestv.presentation.route.navigation.NavigationEntryProvider
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

fun castDetailsNavigationEntryProvider(
    content: @Composable (
        CastDetailsViewModel,
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
        scope.entry<CastDetailsDestination> { destination ->
            val viewModel = koinViewModel<CastDetailsViewModel>(
                key = destination.entryId,
                parameters = { parametersOf(destination.castId) },
            )
            content(viewModel, navigate, onBack)
        }
    }
}
