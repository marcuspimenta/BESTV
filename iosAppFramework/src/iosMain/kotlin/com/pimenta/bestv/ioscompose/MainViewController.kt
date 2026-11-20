package com.pimenta.bestv.ioscompose

import androidx.compose.ui.window.ComposeUIViewController
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.pimenta.bestv.appcomposition.di.appCompositionModule
import com.pimenta.bestv.ioscompose.navigation.navigationModule
import com.pimenta.bestv.ioscompose.di.platformModule
import com.pimenta.bestv.presentation.route.WorkBrowseDestination
import com.pimenta.bestv.presentation.route.bestvDestinationFromUri
import kotlinx.coroutines.Dispatchers
import org.koin.core.context.startKoin
import platform.UIKit.UIViewController

private var koinStarted = false

fun MainViewController(
    initialUrl: String?,
    accessToken: String,
    onOpenExternalUrl: (String) -> Unit,
    onExit: () -> Unit,
): UIViewController {
    if (!koinStarted) {
        startKoin {
            modules(
                appCompositionModule(
                    platformModule = platformModule(accessToken),
                    navigationModule = navigationModule,
                    dispatcher = Dispatchers.Default,
                ),
            )
        }
        koinStarted = true
    }

    val destination = initialUrl?.let(::bestvDestinationFromUri) ?: WorkBrowseDestination
    return ComposeUIViewController {
        setSingletonImageLoaderFactory { context ->
            ImageLoader
                .Builder(context)
                .components { add(KtorNetworkFetcherFactory()) }
                .build()
        }
        BestvApp(
            initialDestination = destination,
            onOpenExternalUrl = onOpenExternalUrl,
            onExit = onExit,
        )
    }
}
