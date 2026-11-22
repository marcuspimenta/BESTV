package com.pimenta.bestv.ioscompose.navigation

import androidx.navigation3.runtime.NavKey
import com.pimenta.bestv.castdetail.presentation.ui.compose.CastDetailsScreen
import com.pimenta.bestv.castdetail.presentation.navigation.castDetailsNavigationEntryProvider
import com.pimenta.bestv.presentation.route.CastDetailsDestination
import com.pimenta.bestv.presentation.route.SearchDestination
import com.pimenta.bestv.presentation.route.WorkBrowseDestination
import com.pimenta.bestv.presentation.route.WorkDetailsDestination
import com.pimenta.bestv.presentation.route.navigation.navigationEntryProviderModule
import com.pimenta.bestv.search.presentation.navigation.searchNavigationEntryProvider
import com.pimenta.bestv.search.presentation.ui.compose.SearchScreen
import com.pimenta.bestv.workbrowse.presentation.navigation.workBrowseNavigationEntryProvider
import com.pimenta.bestv.workbrowse.presentation.ui.compose.WorkBrowseScreen
import com.pimenta.bestv.workdetail.presentation.navigation.workDetailsNavigationEntryProvider
import com.pimenta.bestv.workdetail.presentation.ui.compose.WorkDetailsScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

internal val iosNavigationSerializers = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(WorkBrowseDestination::class, WorkBrowseDestination.serializer())
        subclass(SearchDestination::class, SearchDestination.serializer())
        subclass(WorkDetailsDestination::class, WorkDetailsDestination.serializer())
        subclass(CastDetailsDestination::class, CastDetailsDestination.serializer())
    }
}

val navigationModule = navigationEntryProviderModule(
    workBrowseProvider = workBrowseNavigationEntryProvider { viewModel, navigate, onBack ->
        WorkBrowseScreen(viewModel, navigate, onBack)
    },
    searchProvider = searchNavigationEntryProvider { viewModel, navigate, onBack ->
        SearchScreen(viewModel, navigate, onBack)
    },
    workDetailsProvider = workDetailsNavigationEntryProvider { viewModel, navigate, onOpenExternalUrl, onBack ->
        WorkDetailsScreen(viewModel, navigate, onOpenExternalUrl, onBack)
    },
    castDetailsProvider = castDetailsNavigationEntryProvider { viewModel, navigate, onBack ->
        CastDetailsScreen(viewModel, navigate, onBack)
    },
)
