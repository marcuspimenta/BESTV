# Koin Dependency Injection

This document describes the Koin dependency injection setup used in the BESTV project.

## Overview

[Koin](https://insert-koin.io/) is a pragmatic lightweight dependency injection framework for Kotlin. The current dependency pin is managed in `gradle/libs.versions.toml`, with Android and Compose Multiplatform integrations used across the apps.

## Project Structure

```
androidApp/src/main/java/com/pimenta/bestv/
└── di/
    ├── AppModule.kt             # Android root composition
    ├── PlatformModule.kt        # Android-only bindings
    ├── NavigationModule.kt      # Android navigation entry providers
    └── NetworkModule.kt         # Android network engine and app configuration

appComposition/src/commonMain/kotlin/com/pimenta/bestv/appcomposition/di/
└── AppCompositionModule.kt   # Shared app-level DI composition

common/src/commonMain/kotlin/com/pimenta/bestv/
├── data/remote/api/          # Shared Ktor request configuration
├── di/                       # DatabaseModule, DispatcherModule, TmdbNetworkModule
└── presentation/route/di/    # SharedRouteModule

common/src/androidMain/kotlin/com/pimenta/bestv/
├── data/remote/api/          # Android OkHttp engine
├── di/                       # AndroidDatabaseDriverModule, PresentationModule
└── presentation/route/di/    # AndroidRouteModule

iosAppFramework/src/commonMain/kotlin/com/pimenta/bestv/ioscompose/navigation/
└── NavigationModule.kt      # iOS navigation entry providers

iosAppFramework/src/iosMain/kotlin/com/pimenta/bestv/ioscompose/
├── di/PlatformModule.kt     # iOS-only bindings
└── MainViewController.kt   # iOS Koin startup and Compose entry point

feature_*/src/commonMain/kotlin/.../di/
└── *Module.kt                # Shared feature dependencies
```

## Initialization

On Android, Koin is initialized in the `Application` class:

```kotlin
// androidApp/src/main/java/.../BesTVApplication.kt
class BesTVApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    private fun initKoin() {
        startKoin {
            androidLogger(if (BuildConfig.BUILD_TYPE == "debug") Level.DEBUG else Level.ERROR)
            androidContext(this@BesTVApplication)
            modules(appModule)
        }
    }
}
```

## Module Organization

### Shared App Composition

`appCompositionModule` is the shared composition layer used by both app hosts. It includes the common database and dispatcher bindings, shared routes, and the Browse, Search, Work Details, and Cast Details feature modules. Each host supplies its `platformModule` and `navigationModule`. Android and iOS use the same file and binding names for these platform-only roles; their implementations live in separate Gradle modules.

The `common` module does not aggregate feature modules because features already depend on `common`; putting that composition there would create a dependency cycle.

### Android Root Module (AppModule)

The Android root adds its platform module and Android-only recommendation module to the shared composition:

```kotlin
// androidApp/src/main/java/.../di/AppModule.kt
val appModule = module {
    includes(
        appCompositionModule(
            platformModule = platformModule,
            navigationModule = navigationModule,
            dispatcher = Dispatchers.IO,
        ),
        recommendationModule,
    )
}
```

Both `androidMobile` and `androidTV` currently include `recommendationModule`; its TV-oriented provider and boot receiver have not been restricted to the TV flavor. Feature screen selection does not use Koin device detection: each flavor compiles its own wrapper implementation, and `androidApp`'s navigation module calls that wrapper. `BuildConfig.IS_MOBILE` controls the navigation transitions in `BestvApp`.

### Dispatcher Module

The Koin module lives in `commonMain` and accepts a `CoroutineDispatcher` from the app. Android supplies `Dispatchers.IO`; an iOS entry point can supply its platform-appropriate dispatcher without putting JVM-only `Dispatchers.IO` in shared code.

### Network Module

The Android app reads `BuildConfig`, creates the Android cache, and supplies an OkHttp-backed client. `TmdbNetworkModule` in `commonMain` registers that client and the shared filter-language value. Shared TMDb response DTOs and Ktor request setup also live in `commonMain`; each feature owns its typed endpoint clients under `data/remote/api`.

```kotlin
// androidApp/src/main/java/com/pimenta/bestv/di/NetworkModule.kt
import com.pimenta.bestv.BuildConfig

val networkModule = tmdbNetworkModule {
    createAndroidTmdbHttpClient(
        config = TmdbHttpClientConfig(
        baseUrl = TMDB_API_BASE_URL,
            accessToken = BuildConfig.TMDB_ACCESS_TOKEN,
            debugLogging = BuildConfig.BUILD_TYPE == "debug",
        ),
        cacheDirectory = File(androidApplication().cacheDir, "tmdb-http-cache"),
    )
}
```

### iOS Composition Root

The SwiftUI app in `iosApp` calls [`MainViewController`](iosAppFramework/src/iosMain/kotlin/com/pimenta/bestv/ioscompose/MainViewController.kt). That iOS entry point starts Koin once with `appCompositionModule`, supplying its `platformModule`, `navigationModule`, and `Dispatchers.Default`. The iOS `PlatformModule.kt` provides a Darwin-backed Ktor client, `ContentSourceLabelProvider`, and `NativeSqliteDriver`; the shared TMDb API base URL lives in `commonMain`.

Keep each app entry point responsible for platform construction. The feature Koin modules and common database/network contracts are shared; Android and iOS provide their own `HttpClient`, `SqlDriver`, dispatcher, and platform presentation bindings. Android additionally includes the recommendation module and its TV provider.

### Database Module

The common module binds the generated database and shared local data sources using the multiplatform `SqlDriver` interface:

```kotlin
// common/src/commonMain/kotlin/com/pimenta/bestv/di/DatabaseModule.kt
val databaseModule = module {
    single { MediaDatabase(get<SqlDriver>()) }

    single { MovieLocalDataSource(database = get(), ioDispatcher = get()) }
    single { TvShowLocalDataSource(database = get(), ioDispatcher = get()) }
}
```

`AndroidDatabaseDriverModule` in `common/src/androidMain` supplies `SqlDriver` with `AndroidSqliteDriver`. The iOS platform module in `iosAppFramework` supplies `NativeSqliteDriver`; both reuse the common bindings. The SQLDelight schema and query implementations live in `common/src/commonMain/sqldelight`. Both platforms use the existing `bestv.db` filename and version-1 `movie` and `tv_show` table layouts, preserving favorite and cached-work rows.

### Feature Module Example

A typical feature module provides APIs, data sources, repositories, use cases, and ViewModels:

```kotlin
// feature_search/src/commonMain/kotlin/.../di/SearchModule.kt
val searchModule = module {
    // Typed Ktor API clients share the singleton HttpClient.
    single { SearchMovieTmdbApi(get()) }
    single { SearchTvShowTmdbApi(get()) }

    // DataSources - factory with named parameters
    factory {
        MovieRemoteDataSource(
            tmdbFilterLanguage = get(named("tmdbFilterLanguage")),
            searchMovieTmdbApi = get()
        )
    }

    // Repositories - using factoryOf DSL
    factoryOf(::MovieRepository)
    factoryOf(::TvShowRepository)

    // Domain ports resolve to their concrete data implementations
    factory<com.pimenta.bestv.search.domain.repository.MovieRepository> { get<com.pimenta.bestv.search.data.repository.MovieRepository>() }
    factory<com.pimenta.bestv.search.domain.repository.TvShowRepository> { get<com.pimenta.bestv.search.data.repository.TvShowRepository>() }

    // UseCases - using factoryOf DSL
    factoryOf(::SearchMoviesByQueryUseCase)
    factoryOf(::SearchTvShowsByQueryUseCase)
    factoryOf(::SearchWorksByQueryUseCase)

    // ViewModel - using viewModelOf DSL
    viewModelOf(::SearchViewModel)
}
```

The feature module keeps the concrete repository registration and the port binding together. Use cases depend on the feature's `domain.repository` interface, while Koin knows the `data.repository` implementation. The same data binding pattern is used by Work Browse, Work Details, and Cast Details. Recommendation binds its output domain port directly to a platform TV provider; its movie input port binds to a data repository. KMP feature binding tests live in `androidUnitTest` and resolve these ports with test infrastructure.

`GetBrowseDataUseCase` loads the Browse catalog from the network. Favorite sections are observed separately through `ObserveFavoritesUseCase`, which combines the movie and TV favorite flows backed by SQLDelight.

## DSL Reference

### Scope Functions

| Function | Scope | Description |
|----------|-------|-------------|
| `single` | Singleton | Single instance for the entire app lifetime |
| `factory` | New instance | New instance created on each injection |
| `viewModel` | ViewModel | Lifecycle-aware ViewModel instance |

### Constructor DSL

Koin provides shorthand DSL functions that automatically resolve constructor parameters:

```kotlin
// Long form
factory { MyRepository(get(), get()) }

// Short form using factoryOf
factoryOf(::MyRepository)

// For singletons
singleOf(::MySingleton)

// For ViewModels
viewModelOf(::MyViewModel)
```

### Named Qualifiers

Use `named()` to distinguish between multiple instances of the same type:

```kotlin
// TmdbNetworkModule defines the shared language value.
// Feature modules request it by qualifier:
factory {
    MyDataSource(
        language = get(named("tmdbFilterLanguage"))
    )
}
```

### Parameterized Injection

KMP feature modules register ViewModels with `viewModelOf`; Compose navigation supplies destination-specific parameters when resolving them:

```kotlin
// feature_castdetail/src/commonMain/.../di/CastDetailModule.kt
viewModelOf(::CastDetailsViewModel)

// Resolve from a Navigation 3 entry or the iOS Compose renderer.
val viewModel = koinViewModel<CastDetailsViewModel>(
    parameters = { parametersOf(destination.castId) },
)
```

## Injection in Components

### In Compose navigation entries

```kotlin
entry<WorkDetailsDestination> { destination ->
    val viewModel = koinViewModel<WorkDetailsViewModel>(
        parameters = { parametersOf(destination.work) },
    )
    WorkDetailsWrapperScreen(viewModel = viewModel, ...)
}
```

The feature entry providers live in `commonMain` and resolve shared ViewModels through Koin Compose. The Android navigation module supplies the flavor-specific wrappers; the iOS navigation module supplies the shared mobile screens. Platform intents are handled by Android route adapters; they carry serializable presentation models as Kotlin Serialization JSON string extras.

### In Composables

ViewModels are passed as parameters to Composable screens:

```kotlin
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    navigate: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    // ...
}
```

### Direct Injection

For non-Android classes, use `get()` within the Koin scope:

```kotlin
// In a module definition
factory {
    MyClass(
        dependency1 = get(),
        dependency2 = get()
    )
}
```

## Best Practices

### 1. Module Organization

- **One module per feature**: Keep dependencies scoped to their feature
- **Shared modules**: Extract common dependencies to shared modules
- **Use includes()**: Aggregate modules in the `androidApp` module

### 2. Scope Selection

- **single**: Network clients, databases, shared resources
- **factory**: Use cases, repositories, data sources
- **viewModel**: All ViewModel classes

### 3. Testing

For a module smoke test, create an isolated Koin application with the production feature module and substitute infrastructure definitions, then resolve the domain port:

```kotlin
val testApplication = koinApplication {
    modules(searchModule, testInfrastructureModule)
}

val repository = testApplication.koin.get<com.pimenta.bestv.search.domain.repository.MovieRepository>()
assertTrue(repository is com.pimenta.bestv.search.data.repository.MovieRepository)
testApplication.close()
```

The affected feature modules use this pattern with mocked local dependencies and a mocked `HttpClient`. KMP feature endpoint tests use Ktor's MockEngine in `androidUnitTest`; the `common` module's host tests cover shared client configuration and DTO parsing. Run the relevant flavor tasks, for example `./gradlew :feature_search:testAndroidMobileDebugUnitTest :feature_search:testAndroidTVDebugUnitTest`.

### 4. Avoid

- Circular dependencies
- Injecting Context directly (use `androidContext()` or `androidApplication()`)
- Large monolithic modules

## Module Dependency Graph

The shared `appComposition` module is used by both app roots. Each root provides its platform bindings and navigation entry providers; Android also includes the recommendation module.

```
Android appModule ──────┐
                        ├── appCompositionModule
iOS MainViewController ─┘       ├── common database, dispatcher, and routes
                                ├── Browse, Search, Work Details, Cast Details
                                └── host-supplied platform and navigation modules

Android also includes recommendationModule.
```

## References

- [Koin Documentation](https://insert-koin.io/docs/quickstart/kotlin)
- [Koin Android](https://insert-koin.io/docs/quickstart/android)
- [Koin Compose](https://insert-koin.io/docs/quickstart/android-compose)
