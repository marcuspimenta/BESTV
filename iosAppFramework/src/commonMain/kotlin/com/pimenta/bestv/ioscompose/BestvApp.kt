package com.pimenta.bestv.ioscompose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.pimenta.bestv.ioscompose.navigation.iosNavigationSerializers
import com.pimenta.bestv.presentation.route.navigation.NavigationEntryProvider
import com.pimenta.bestv.presentation.route.WorkBrowseDestination
import com.pimenta.bestv.presentation.route.bestvDestinationFromUri
import com.pimenta.bestv.presentation.theme.BESTVTheme
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import org.koin.compose.koinInject

private val incomingDestinations = Channel<NavKey>(Channel.BUFFERED)

private val navigationStateConfiguration = SavedStateConfiguration {
    serializersModule = iosNavigationSerializers
}

@Composable
fun BestvApp(
    initialDestination: NavKey = WorkBrowseDestination,
    onOpenExternalUrl: (String) -> Unit,
    onExit: () -> Unit,
) {
    val navigationEntryProviders = koinInject<List<NavigationEntryProvider>>()
    val backStack = rememberNavBackStack(navigationStateConfiguration, initialDestination)
    val navigate: (NavKey) -> Unit = { destination -> backStack.add(destination) }
    val onBack: () -> Unit = {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        } else {
            onExit()
        }
    }

    LaunchedEffect(backStack) {
        incomingDestinations.receiveAsFlow().collect { destination ->
            backStack.add(destination)
        }
    }

    BESTVTheme {
        NavDisplay(
            backStack = backStack,
            onBack = onBack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = entryProvider {
                navigationEntryProviders.forEach { provider ->
                    provider.install(this, navigate, onBack, onOpenExternalUrl)
                }
            },
        )
    }
}

/** Sends an incoming BESTV deep link to the active iOS Compose navigation host. */
fun handleBestvDeepLink(uri: String) {
    bestvDestinationFromUri(uri)?.let(incomingDestinations::trySend)
}

fun bestvDestinationFromInitialUrl(url: String?): NavKey =
    url?.let(::bestvDestinationFromUri) ?: WorkBrowseDestination
