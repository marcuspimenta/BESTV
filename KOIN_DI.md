# Koin Dependency Injection

This document describes the Koin dependency injection setup used in the BESTV project.

## Overview

[Koin](https://insert-koin.io/) is a pragmatic lightweight dependency injection framework for Kotlin. The current dependency pin is managed in `gradle/libs.versions.toml`, with Android and Compose extensions used across the app.

## Project Structure

```
app/
└── di/
    └── AppModule.kt          # Root module that includes all other modules

common/src/main/java/com/pimenta/bestv/
├── data/di/                  # NetworkModule, DatabaseModule, DispatcherModule
├── presentation/di/          # PresentationModule
└── route/di/                 # RouteModule

feature_*/
└── di/
    └── *Module.kt            # Feature-specific dependencies
```

## Initialization

Koin is initialized in the `Application` class:

```kotlin
// app/src/main/java/.../BesTVApplication.kt
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

### Root Module (AppModule)

The `appModule` aggregates all modules using `includes()`:

```kotlin
// app/src/main/java/.../di/AppModule.kt
val appModule = module {
    includes(
        // Shared modules
        networkModule,
        databaseModule,
        presentationModule,
        routeModule,
        // Feature modules
        castDetailModule,
        recommendationModule,
        searchModule,
        workBrowseModule,
        workDetailModule,
    )
}
```

### Network Module

Provides the shared Android Ktor client and API configuration. Shared TMDb response DTOs and client setup live in `common`; each feature owns its typed endpoint clients under `data/remote/api`. The client uses the Android OkHttp engine and its disk cache.

```kotlin
// common/src/main/java/com/pimenta/bestv/data/di/NetworkModule.kt
val networkModule = module {
    single(named("tmdbFilterLanguage")) { BuildConfig.TMDB_FILTER_LANGUAGE }
    single {
        createAndroidTmdbHttpClient(
            config = TmdbHttpClientConfig(
                baseUrl = BuildConfig.TMDB_BASE_URL,
                accessToken = BuildConfig.TMDB_ACCESS_TOKEN,
                debugLogging = BuildConfig.BUILD_TYPE == "debug",
            ),
            cacheDirectory = File(androidApplication().cacheDir, "tmdb-http-cache"),
        )
    }
}
```

### Database Module

Provides the Android SQLDelight driver, generated database, and shared local data sources:

```kotlin
// common/src/main/java/com/pimenta/bestv/data/di/DatabaseModule.kt
val databaseModule = module {
    // Android driver opens the existing database file.
    single {
        val driver = AndroidSqliteDriver(MediaDatabase.Schema, androidApplication(), "bestv.db")
        MediaDatabase(driver)
    }

    // Shared SQLDelight local data sources
    single { MovieLocalDataSource(get()) }
    single { TvShowLocalDataSource(get()) }
}
```

The SQLDelight schema and query implementations live in `data_shared/src/commonMain`. The Android driver uses the same `bestv.db` filename and version-1 `movie` and `tv_show` table layouts as the former Room database, preserving existing favorite and cached-work rows.

### Feature Module Example

A typical feature module provides APIs, data sources, repositories, use cases, and ViewModels:

```kotlin
// feature_search/src/main/java/.../di/SearchModule.kt
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

The feature module keeps the concrete repository registration and the port binding together. Use cases depend on the feature's `domain.repository` interface, while Koin knows the `data.repository` implementation. The same data binding pattern is used by Work Browse, Work Details, and Cast Details. Recommendation binds its output domain port directly to a platform TV provider; its movie input port binds to a data repository. Feature binding smoke tests resolve these ports with test infrastructure.

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
// Definition
single(named("tmdbFilterLanguage")) { BuildConfig.TMDB_FILTER_LANGUAGE }

// Injection
factory {
    MyDataSource(
        language = get(named("tmdbFilterLanguage"))
    )
}
```

### Parameterized Injection

For ViewModels or objects that need runtime parameters:

```kotlin
// Definition with parametersOf
factory { (cast: CastViewModel) ->
    CastDetailsViewModel(
        cast = cast,
        getCastDetailsUseCase = get(),
        workDetailsRoute = get()
    )
}

// Injection in Activity
private val viewModel: CastDetailsViewModel by viewModel {
    parametersOf(intent.getParcelableExtra<CastViewModel>(CAST)!!)
}
```

## Injection in Components

### In Activities

```kotlin
class SearchActivity : ComponentActivity() {
    // Simple ViewModel injection
    private val viewModel: SearchViewModel by viewModel()
}

class CastDetailsActivity : ComponentActivity() {
    // ViewModel with parameters
    private val viewModel: CastDetailsViewModel by viewModel {
        parametersOf(intent.getParcelableExtra<CastViewModel>(CAST)!!)
    }
}
```

### In Composables

ViewModels are passed as parameters to Composable screens:

```kotlin
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    openIntent: (Intent) -> Unit,
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
- **Use includes()**: Aggregate modules in the app module

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

The affected feature modules use this pattern with mocked local dependencies and a mocked `HttpClient`. Each feature's endpoint tests use Ktor's MockEngine; common tests cover shared client configuration and DTO parsing.

### 4. Avoid

- Circular dependencies
- Injecting Context directly (use `androidContext()` or `androidApplication()`)
- Large monolithic modules

## Module Dependency Graph

```
                    ┌─────────────┐
                    │  appModule  │
                    └──────┬──────┘
                           │
           ┌───────────────┼───────────────┐
           │               │               │
           ▼               ▼               ▼
    ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
    │networkModule│ │databaseModule│ │ routeModule │
    └─────────────┘ └─────────────┘ └─────────────┘
           │               │               │
           └───────────────┼───────────────┘
                           │
    ┌──────────────────────┼──────────────────────┐
    │                      │                      │
    ▼                      ▼                      ▼
┌──────────┐        ┌──────────┐          ┌──────────┐
│searchMod.│        │castDetail│          │workBrowse│
└──────────┘        │  Module  │          │  Module  │
                    └──────────┘          └──────────┘
```

## References

- [Koin Documentation](https://insert-koin.io/docs/quickstart/kotlin)
- [Koin Android](https://insert-koin.io/docs/quickstart/android)
- [Koin Compose](https://insert-koin.io/docs/quickstart/android-compose)
