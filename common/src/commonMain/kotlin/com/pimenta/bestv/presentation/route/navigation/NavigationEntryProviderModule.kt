package com.pimenta.bestv.presentation.route.navigation

import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun navigationEntryProviderModule(
    workBrowseProvider: NavigationEntryProvider,
    searchProvider: NavigationEntryProvider,
    workDetailsProvider: NavigationEntryProvider,
    castDetailsProvider: NavigationEntryProvider,
): Module = module {
    single<NavigationEntryProvider>(named("workBrowseEntryProvider")) { workBrowseProvider }
    single<NavigationEntryProvider>(named("searchEntryProvider")) { searchProvider }
    single<NavigationEntryProvider>(named("workDetailsEntryProvider")) { workDetailsProvider }
    single<NavigationEntryProvider>(named("castDetailsEntryProvider")) { castDetailsProvider }
    single { getAll<NavigationEntryProvider>() }
}
