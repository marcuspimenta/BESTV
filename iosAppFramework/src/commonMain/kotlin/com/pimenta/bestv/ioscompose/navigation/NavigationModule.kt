package com.pimenta.bestv.ioscompose.navigation

import androidx.navigation3.runtime.NavKey
import com.pimenta.bestv.castdetail.presentation.ui.compose.mobile.MobileCastDetailsScreen
import com.pimenta.bestv.castdetail.presentation.navigation.castDetailsNavigationEntryProvider
import com.pimenta.bestv.presentation.route.CastDetailsDestination
import com.pimenta.bestv.presentation.route.SearchDestination
import com.pimenta.bestv.presentation.route.WorkBrowseDestination
import com.pimenta.bestv.presentation.route.WorkDetailsDestination
import com.pimenta.bestv.presentation.route.navigation.navigationEntryProviderModule
import com.pimenta.bestv.search.presentation.navigation.searchNavigationEntryProvider
import com.pimenta.bestv.search.presentation.ui.compose.mobile.MobileSearchScreen
import com.pimenta.bestv.workbrowse.presentation.navigation.workBrowseNavigationEntryProvider
import com.pimenta.bestv.workbrowse.presentation.ui.compose.mobile.MobileBrowseScreen
import com.pimenta.bestv.workdetail.presentation.navigation.workDetailsNavigationEntryProvider
import com.pimenta.bestv.workdetail.presentation.ui.compose.mobile.MobileWorkDetailsScreen
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
        MobileBrowseScreen(viewModel, navigate, onBack)
    },
    searchProvider = searchNavigationEntryProvider { viewModel, navigate, onBack ->
        MobileSearchScreen(viewModel, navigate, onBack)
    },
    workDetailsProvider = workDetailsNavigationEntryProvider { viewModel, navigate, onOpenExternalUrl, onBack ->
        MobileWorkDetailsScreen(viewModel, navigate, onOpenExternalUrl, onBack)
    },
    castDetailsProvider = castDetailsNavigationEntryProvider { viewModel, navigate, onBack ->
        MobileCastDetailsScreen(viewModel, navigate, onBack)
    },
)
