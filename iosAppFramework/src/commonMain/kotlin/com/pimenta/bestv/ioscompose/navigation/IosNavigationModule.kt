package com.pimenta.bestv.ioscompose.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.pimenta.bestv.castdetail.presentation.ui.compose.mobile.MobileCastDetailsScreen
import com.pimenta.bestv.castdetail.presentation.viewmodel.CastDetailsViewModel
import com.pimenta.bestv.presentation.route.CastDetailsDestination
import com.pimenta.bestv.presentation.route.SearchDestination
import com.pimenta.bestv.presentation.route.WorkBrowseDestination
import com.pimenta.bestv.presentation.route.WorkDetailsDestination
import com.pimenta.bestv.presentation.route.navigation.NavigationEntryProvider
import com.pimenta.bestv.search.presentation.ui.compose.mobile.MobileSearchScreen
import com.pimenta.bestv.search.presentation.viewmodel.SearchViewModel
import com.pimenta.bestv.workbrowse.presentation.ui.compose.mobile.MobileBrowseScreen
import com.pimenta.bestv.workbrowse.presentation.viewmodel.WorkBrowseViewModel
import com.pimenta.bestv.workdetail.presentation.ui.compose.mobile.MobileWorkDetailsScreen
import com.pimenta.bestv.workdetail.presentation.viewmodel.WorkDetailsViewModel
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val iosNavigationSerializers = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(WorkBrowseDestination::class, WorkBrowseDestination.serializer())
        subclass(SearchDestination::class, SearchDestination.serializer())
        subclass(WorkDetailsDestination::class, WorkDetailsDestination.serializer())
        subclass(CastDetailsDestination::class, CastDetailsDestination.serializer())
    }
}

val iosNavigationModule = module {
    single<NavigationEntryProvider>(named("workBrowseEntryProvider")) { IosWorkBrowseEntryProvider() }
    single<NavigationEntryProvider>(named("searchEntryProvider")) { IosSearchEntryProvider() }
    single<NavigationEntryProvider>(named("workDetailsEntryProvider")) { IosWorkDetailsEntryProvider() }
    single<NavigationEntryProvider>(named("castDetailsEntryProvider")) { IosCastDetailsEntryProvider() }
    single { getAll<NavigationEntryProvider>() }
}

private class IosWorkBrowseEntryProvider : NavigationEntryProvider {
    override fun install(
        scope: EntryProviderScope<NavKey>,
        navigate: (NavKey) -> Unit,
        onBack: () -> Unit,
        @Suppress("UNUSED_PARAMETER") onOpenExternalUrl: (String) -> Unit,
    ) {
        scope.workBrowseEntry(navigate, onBack)
    }
}

private fun EntryProviderScope<NavKey>.workBrowseEntry(
    navigate: (NavKey) -> Unit,
    onBack: () -> Unit,
) {
    entry<WorkBrowseDestination> {
        val viewModel = koinViewModel<WorkBrowseViewModel>()
        MobileBrowseScreen(viewModel, navigate, onBack)
    }
}

private class IosSearchEntryProvider : NavigationEntryProvider {
    override fun install(
        scope: EntryProviderScope<NavKey>,
        navigate: (NavKey) -> Unit,
        onBack: () -> Unit,
        @Suppress("UNUSED_PARAMETER") onOpenExternalUrl: (String) -> Unit,
    ) {
        scope.searchEntry(navigate, onBack)
    }
}

private fun EntryProviderScope<NavKey>.searchEntry(
    navigate: (NavKey) -> Unit,
    onBack: () -> Unit,
) {
    entry<SearchDestination> {
        val viewModel = koinViewModel<SearchViewModel>()
        MobileSearchScreen(viewModel, navigate, onBack)
    }
}

private class IosWorkDetailsEntryProvider : NavigationEntryProvider {
    override fun install(
        scope: EntryProviderScope<NavKey>,
        navigate: (NavKey) -> Unit,
        onBack: () -> Unit,
        onOpenExternalUrl: (String) -> Unit,
    ) {
        scope.workDetailsEntry(navigate, onBack, onOpenExternalUrl)
    }
}

private fun EntryProviderScope<NavKey>.workDetailsEntry(
    navigate: (NavKey) -> Unit,
    onBack: () -> Unit,
    onOpenExternalUrl: (String) -> Unit,
) {
    entry<WorkDetailsDestination> { destination ->
        val viewModel = koinViewModel<WorkDetailsViewModel>(
            key = destination.entryId,
            parameters = { parametersOf(destination.work) },
        )
        MobileWorkDetailsScreen(viewModel, navigate, onOpenExternalUrl, onBack)
    }
}

private class IosCastDetailsEntryProvider : NavigationEntryProvider {
    override fun install(
        scope: EntryProviderScope<NavKey>,
        navigate: (NavKey) -> Unit,
        onBack: () -> Unit,
        @Suppress("UNUSED_PARAMETER") onOpenExternalUrl: (String) -> Unit,
    ) {
        scope.castDetailsEntry(navigate, onBack)
    }
}

private fun EntryProviderScope<NavKey>.castDetailsEntry(
    navigate: (NavKey) -> Unit,
    onBack: () -> Unit,
) {
    entry<CastDetailsDestination> { destination ->
        val viewModel = koinViewModel<CastDetailsViewModel>(
            key = destination.entryId,
            parameters = { parametersOf(destination.castId) },
        )
        MobileCastDetailsScreen(viewModel, navigate, onBack)
    }
}
