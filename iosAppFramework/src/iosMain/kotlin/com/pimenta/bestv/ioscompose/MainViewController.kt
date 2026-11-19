package com.pimenta.bestv.ioscompose

import androidx.compose.ui.window.ComposeUIViewController
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.pimenta.bestv.castdetail.di.castDetailModule
import com.pimenta.bestv.data.local.database.MediaDatabase
import com.pimenta.bestv.data.remote.api.TmdbHttpClientConfig
import com.pimenta.bestv.data.remote.api.configureTmdbClient
import com.pimenta.bestv.di.databaseModule
import com.pimenta.bestv.di.dispatcherModule
import com.pimenta.bestv.di.tmdbNetworkModule
import com.pimenta.bestv.domain.model.ContentSource
import com.pimenta.bestv.ioscompose.navigation.iosNavigationModule
import com.pimenta.bestv.presentation.platform.ContentSourceLabelProvider
import com.pimenta.bestv.presentation.platform.DeviceCapabilities
import com.pimenta.bestv.presentation.route.WorkBrowseDestination
import com.pimenta.bestv.presentation.route.bestvDestinationFromUri
import com.pimenta.bestv.presentation.route.di.sharedRouteModule
import com.pimenta.bestv.search.di.searchModule
import com.pimenta.bestv.workbrowse.di.workBrowseModule
import com.pimenta.bestv.workdetail.di.workDetailModule
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import kotlinx.coroutines.Dispatchers
import org.koin.core.context.startKoin
import org.koin.dsl.module
import platform.UIKit.UIViewController

private var koinStarted = false

private fun iosPlatformModule() = module {
    single<DeviceCapabilities> {
        object : DeviceCapabilities {
            override val isMobileDevice = true
        }
    }
    single<ContentSourceLabelProvider> {
        ContentSourceLabelProvider { source ->
            when (source) {
                ContentSource.TMDB -> "TMDb"
            }
        }
    }
    single<SqlDriver> { NativeSqliteDriver(MediaDatabase.Schema, "bestv.db") }
}

private const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"

fun MainViewController(
    initialUrl: String?,
    accessToken: String,
    onOpenExternalUrl: (String) -> Unit,
    onExit: () -> Unit,
): UIViewController {
    if (!koinStarted) {
        startKoin {
            modules(
                iosPlatformModule(),
                tmdbNetworkModule {
                    HttpClient(Darwin) {
                        configureTmdbClient(
                            TmdbHttpClientConfig(
                                baseUrl = TMDB_BASE_URL,
                                accessToken = accessToken,
                                debugLogging = false,
                            ),
                        )
                    }
                },
                databaseModule,
                dispatcherModule(Dispatchers.Default),
                iosNavigationModule,
                sharedRouteModule,
                castDetailModule,
                searchModule,
                workBrowseModule,
                workDetailModule,
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
