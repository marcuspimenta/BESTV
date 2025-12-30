# Koin Dependency Injection

This document describes the Koin dependency injection setup used in the BESTV project.

## Overview

[Koin](https://insert-koin.io/) is a pragmatic lightweight dependency injection framework for Kotlin. This project uses Koin 4.0 with Android and Compose extensions to manage dependencies across all modules.

## Project Structure

```
app/
└── di/
    └── AppModule.kt          # Root module that includes all other modules

data_shared/
└── di/
    ├── NetworkModule.kt      # Retrofit, OkHttp, API configuration
    └── DatabaseModule.kt     # Room database setup

presentation_shared/
└── di/
    └── PresentationModule.kt # Shared presentation utilities

route_shared/
└── di/
    └── RouteModule.kt        # Navigation routes

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

Provides Retrofit, OkHttp, and API configuration:

```kotlin
// data_shared/src/main/java/.../di/NetworkModule.kt
val networkModule = module {
    // Named qualifiers for configuration values
    single(named("tmdbApiKey")) { BuildConfig.TMDB_API_KEY }
    single(named("tmdbFilterLanguage")) { BuildConfig.TMDB_FILTER_LANGUAGE }

    // OkHttp client as singleton
    single {
        OkHttpClient.Builder().apply {
            if (BuildConfig.BUILD_TYPE == "debug") {
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                })
            }
            readTimeout(60L, TimeUnit.SECONDS)
            writeTimeout(60L, TimeUnit.SECONDS)
            connectTimeout(60L, TimeUnit.SECONDS)
        }.build()
    }

    // Retrofit as singleton
    single {
        Retrofit.Builder()
            .baseUrl(BuildConfig.TMDB_BASE_URL)
            .client(get())  // Injects OkHttpClient
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
    }
}
```

### Database Module

Provides Room database and DAOs:

```kotlin
// data_shared/src/main/java/.../di/DatabaseModule.kt
val databaseModule = module {
    // Room database as singleton
    single {
        Room.databaseBuilder(androidApplication(), MediaDb::class.java, "bestv.db")
            .build()
    }

    // DAOs as singletons
    single { get<MediaDb>().movieDao() }
    single { get<MediaDb>().tvShowDao() }

    // Local data sources
    single { MovieLocalDataSource(get()) }
    single { TvShowLocalDataSource(get()) }
}
```

### Feature Module Example

A typical feature module provides APIs, data sources, repositories, use cases, and ViewModels:

```kotlin
// feature_search/src/main/java/.../di/SearchModule.kt
val searchModule = module {
    // APIs - created from Retrofit
    single { get<Retrofit>().create(SearchMovieTmdbApi::class.java) }
    single { get<Retrofit>().create(SearchTvShowTmdbApi::class.java) }

    // DataSources - factory with named parameters
    factory {
        MovieRemoteDataSource(
            tmdbApiKey = get(named("tmdbApiKey")),
            tmdbFilterLanguage = get(named("tmdbFilterLanguage")),
            searchMovieTmdbApi = get()
        )
    }

    // Repositories - using factoryOf DSL
    factoryOf(::MovieRepository)
    factoryOf(::TvShowRepository)

    // UseCases - using factoryOf DSL
    factoryOf(::SearchMoviesByQueryUseCase)
    factoryOf(::SearchTvShowsByQueryUseCase)
    factoryOf(::SearchWorksByQueryUseCase)

    // ViewModel - using viewModelOf DSL
    viewModelOf(::SearchViewModel)
}
```

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
single(named("tmdbApiKey")) { BuildConfig.TMDB_API_KEY }
single(named("tmdbFilterLanguage")) { BuildConfig.TMDB_FILTER_LANGUAGE }

// Injection
factory {
    MyDataSource(
        apiKey = get(named("tmdbApiKey")),
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

For unit tests, Koin provides test utilities:

```kotlin
class MyViewModelTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(testModule)
    }

    private val viewModel: MyViewModel by inject()
}
```

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
