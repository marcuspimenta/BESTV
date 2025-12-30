# Clean Architecture

This document describes the Clean Architecture implementation used in the BESTV project.

## Overview

Clean Architecture is a software design philosophy that separates the elements of a design into ring levels. The main rule of Clean Architecture is the Dependency Rule: source code dependencies can only point inwards. Nothing in an inner circle can know anything about something in an outer circle.

```
┌─────────────────────────────────────────────────────────────────┐
│                        Presentation Layer                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐  │
│  │  Activities │  │  ViewModels │  │  Compose UI (Screens)   │  │
│  └─────────────┘  └─────────────┘  └─────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                         Domain Layer                             │
│  ┌─────────────────────────┐  ┌───────────────────────────────┐ │
│  │       Use Cases         │  │       Domain Models           │ │
│  │  (Business Logic)       │  │   (Pure Kotlin classes)       │ │
│  └─────────────────────────┘  └───────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                          Data Layer                              │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐  │
│  │ Repositories│  │ DataSources │  │    Data Models          │  │
│  │             │  │ (Remote/    │  │ (Response/DbModel)      │  │
│  │             │  │  Local)     │  │                         │  │
│  └─────────────┘  └─────────────┘  └─────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

## Layer Responsibilities

### Presentation Layer

The outermost layer responsible for UI and user interaction.

**Components:**
- **Activities**: Entry points that host Compose screens
- **ViewModels**: Manage UI state using MVI pattern ([see MVI documentation](MVI_ARCHITECTURE.md))
- **Compose Screens**: Declarative UI components
- **Presentation Models**: UI-specific data models (ViewModels like `WorkViewModel`, `CastViewModel`)

**Location:** `feature_*/src/main/java/.../presentation/`

### Domain Layer

The innermost layer containing business logic. This layer is independent of any framework.

**Components:**
- **Use Cases**: Single-responsibility classes that encapsulate business logic
- **Domain Models**: Pure Kotlin data classes representing business entities

**Location:** `feature_*/src/main/java/.../domain/`

### Data Layer

Responsible for data operations from various sources (network, database, cache).

**Components:**
- **Repositories**: Coordinate data from multiple sources
- **Data Sources**: Abstract data retrieval (Remote/Local)
- **Data Models**: Source-specific models (Response, DbModel)
- **Mappers**: Transform data between layers

**Location:** `feature_*/src/main/java/.../data/`

## Project Structure

### Feature Module Structure

Each feature module follows a consistent structure:

```
feature_search/
├── data/
│   ├── remote/
│   │   ├── api/
│   │   │   └── SearchMovieTmdbApi.kt      # Retrofit API interface
│   │   └── datasource/
│   │       └── MovieRemoteDataSource.kt   # Remote data operations
│   └── repository/
│       └── MovieRepository.kt             # Data coordination
├── domain/
│   ├── SearchMoviesByQueryUseCase.kt      # Business logic
│   ├── SearchTvShowsByQueryUseCase.kt
│   └── SearchWorksByQueryUseCase.kt
├── presentation/
│   ├── model/
│   │   ├── SearchState.kt                 # UI State
│   │   ├── SearchEvent.kt                 # User intents
│   │   └── SearchEffect.kt                # Side effects
│   ├── viewmodel/
│   │   └── SearchViewModel.kt             # State management
│   └── ui/
│       ├── activity/
│       │   └── SearchActivity.kt          # Entry point
│       └── compose/
│           └── SearchScreen.kt            # Composable UI
└── di/
    └── SearchModule.kt                    # Koin DI module
```

### Shared Modules

```
model_shared/
├── data/
│   ├── local/
│   │   ├── MovieDbModel.kt               # Room entity
│   │   └── TvShowDbModel.kt
│   ├── remote/
│   │   ├── MovieResponse.kt              # API response model
│   │   ├── TvShowResponse.kt
│   │   ├── WorkResponse.kt               # Base response
│   │   └── PageResponse.kt
│   └── mapper/
│       ├── WorkResponseMapper.kt         # Response → Domain
│       └── PageResponseMapper.kt
├── domain/
│   ├── WorkDomainModel.kt                # Domain entity
│   ├── CastDomainModel.kt
│   └── PageDomainModel.kt
└── presentation/
    ├── model/
    │   ├── WorkViewModel.kt              # Presentation model
    │   ├── CastViewModel.kt
    │   └── PageViewModel.kt
    └── mapper/
        ├── WorkViewModelMapper.kt        # Domain → Presentation
        └── CastViewModelMapper.kt

data_shared/
├── local/
│   ├── database/
│   │   └── MediaDb.kt                    # Room database
│   ├── dao/
│   │   ├── MovieDao.kt
│   │   └── TvShowDao.kt
│   └── datasource/
│       ├── MovieLocalDataSource.kt
│       └── TvShowLocalDataSource.kt
└── di/
    ├── NetworkModule.kt                  # Retrofit setup
    └── DatabaseModule.kt                 # Room setup
```

## Data Flow

### Search Example Flow

```
┌──────────────────────────────────────────────────────────────────────────┐
│                           USER ACTION                                     │
│                    User types "Batman" in search                          │
└────────────────────────────────┬─────────────────────────────────────────┘
                                 │
                                 ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                         PRESENTATION LAYER                                │
│                                                                          │
│  SearchScreen.kt                    SearchViewModel.kt                   │
│  ┌─────────────────┐               ┌─────────────────────────────────┐  │
│  │ onQueryChange() │──Event───────▶│ handleEvent(SearchQueryChanged) │  │
│  └─────────────────┘               │         │                       │  │
│                                    │         ▼                       │  │
│                                    │ searchWorksByQueryUseCase()     │  │
│                                    └─────────────────────────────────┘  │
└────────────────────────────────────┬─────────────────────────────────────┘
                                     │
                                     ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                           DOMAIN LAYER                                    │
│                                                                          │
│  SearchWorksByQueryUseCase.kt                                            │
│  ┌───────────────────────────────────────────────────────────────────┐  │
│  │ suspend operator fun invoke(query: String) = coroutineScope {     │  │
│  │     val movies = async { searchMoviesByQueryUseCase(query, 1) }   │  │
│  │     val tvShows = async { searchTvShowsByQueryUseCase(query, 1) } │  │
│  │     awaitAll(movies, tvShows)                                     │  │
│  │ }                                                                 │  │
│  └───────────────────────────────────────────────────────────────────┘  │
│                          │                                               │
│                          ▼                                               │
│  SearchMoviesByQueryUseCase.kt                                          │
│  ┌───────────────────────────────────────────────────────────────────┐  │
│  │ suspend operator fun invoke(query, page) =                        │  │
│  │     movieRepository.searchMoviesByQuery(query, page)              │  │
│  └───────────────────────────────────────────────────────────────────┘  │
└────────────────────────────────────┬─────────────────────────────────────┘
                                     │
                                     ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                            DATA LAYER                                     │
│                                                                          │
│  MovieRepository.kt                                                      │
│  ┌───────────────────────────────────────────────────────────────────┐  │
│  │ suspend fun searchMoviesByQuery(query, page) =                    │  │
│  │     movieRemoteDataSource.searchMoviesByQuery(query, page)        │  │
│  │         .toDomainModel(source)  // Mapper: Response → Domain      │  │
│  └───────────────────────────────────────────────────────────────────┘  │
│                          │                                               │
│                          ▼                                               │
│  MovieRemoteDataSource.kt                                               │
│  ┌───────────────────────────────────────────────────────────────────┐  │
│  │ suspend fun searchMoviesByQuery(query, page) =                    │  │
│  │     searchMovieTmdbApi.searchMoviesByQuery(apiKey, query, ...)    │  │
│  └───────────────────────────────────────────────────────────────────┘  │
│                          │                                               │
│                          ▼                                               │
│  SearchMovieTmdbApi.kt (Retrofit)                                       │
│  ┌───────────────────────────────────────────────────────────────────┐  │
│  │ @GET("search/movie")                                              │  │
│  │ suspend fun searchMoviesByQuery(...): PageResponse<MovieResponse> │  │
│  └───────────────────────────────────────────────────────────────────┘  │
└────────────────────────────────────┬─────────────────────────────────────┘
                                     │
                                     ▼
                              TMDb REST API
```

## Model Types

### Data Models

**Remote (API Response)**
```kotlin
// model_shared/.../data/remote/MovieResponse.kt
data class MovieResponse(
    @SerializedName("title") override var title: String? = null,
    @SerializedName("original_title") override var originalTitle: String? = null,
    @SerializedName("release_date") var releaseDateString: String? = null
) : WorkResponse()
```

**Local (Room Entity)**
```kotlin
// model_shared/.../data/local/MovieDbModel.kt
@Entity(tableName = "movie")
data class MovieDbModel(
    @PrimaryKey val id: Int = 0
)
```

### Domain Models

```kotlin
// model_shared/.../domain/WorkDomainModel.kt
data class WorkDomainModel(
    val id: Int = 0,
    val title: String? = null,
    val originalTitle: String? = null,
    val releaseDate: String? = null,
    val overview: String? = null,
    val type: Type = Type.MOVIE,
    var isFavorite: Boolean = false
) {
    enum class Type { TV_SHOW, MOVIE }
}
```

### Presentation Models

```kotlin
// model_shared/.../presentation/model/WorkViewModel.kt
@Parcelize
data class WorkViewModel(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String,
    val releaseDate: String,
    val type: WorkType,
    val isFavorite: Boolean = false,
) : Parcelable
```

## Mappers

Mappers transform data between layers using extension functions:

### Response → Domain

```kotlin
// model_shared/.../data/mapper/WorkResponseMapper.kt
fun WorkResponse.toDomainModel(source: String) = WorkDomainModel(
    id = id,
    title = title,
    originalTitle = originalTitle,
    releaseDate = releaseDate,
    overview = overview,
    source = source,
    type = WorkDomainModel.Type.TV_SHOW.takeIf { this is TvShowResponse }
        ?: WorkDomainModel.Type.MOVIE
)
```

### Domain → Presentation

```kotlin
// model_shared/.../presentation/mapper/WorkViewModelMapper.kt
fun WorkDomainModel.toViewModel(): WorkViewModel? {
    // Validate required fields
    if (title == null || overview == null) return null

    return WorkViewModel(
        id = id,
        title = title,
        overview = overview,
        posterUrl = String.format(BASE_URL, posterPath),
        releaseDate = formatDate(releaseDate),
        type = WorkType.TV_SHOW.takeIf { type == Type.TV_SHOW } ?: WorkType.MOVIE,
        isFavorite = isFavorite,
    )
}
```

### Presentation → Local

```kotlin
// model_shared/.../presentation/mapper/WorkViewModelMapper.kt
fun WorkViewModel.toMovieDbModel() = MovieDbModel(id = id)
fun WorkViewModel.toTvShowDbModel() = TvShowDbModel(id = id)
```

## Use Cases

Use Cases encapsulate single business operations:

### Simple Use Case

```kotlin
// feature_search/.../domain/SearchMoviesByQueryUseCase.kt
class SearchMoviesByQueryUseCase(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(query: String, page: Int) =
        movieRepository.searchMoviesByQuery(query, page)
}
```

### Composite Use Case

```kotlin
// feature_search/.../domain/SearchWorksByQueryUseCase.kt
class SearchWorksByQueryUseCase(
    private val urlEncoderTextUseCase: UrlEncoderTextUseCase,
    private val searchMoviesByQueryUseCase: SearchMoviesByQueryUseCase,
    private val searchTvShowsByQueryUseCase: SearchTvShowsByQueryUseCase
) {
    suspend operator fun invoke(query: String) = coroutineScope {
        val urlEncoder = async { urlEncoderTextUseCase(query) }.await()
        val movies = async { searchMoviesByQueryUseCase(urlEncoder, 1) }
        val tvShows = async { searchTvShowsByQueryUseCase(urlEncoder, 1) }
        awaitAll(movies, tvShows)
        movies.await() to tvShows.await()
    }
}
```

## Repositories

Repositories coordinate data from multiple sources:

```kotlin
// feature_search/.../data/repository/MovieRepository.kt
class MovieRepository(
    private val resource: Resource,
    private val movieRemoteDataSource: MovieRemoteDataSource
) {
    suspend fun searchMoviesByQuery(query: String, page: Int) =
        movieRemoteDataSource.searchMoviesByQuery(query, page).run {
            val source = resource.getStringResource(R.string.source_tmdb)
            toDomainModel(source)  // Transform to domain model
        }
}
```

## Data Sources

### Remote Data Source

```kotlin
// feature_search/.../data/remote/datasource/MovieRemoteDataSource.kt
class MovieRemoteDataSource(
    private val tmdbApiKey: String,
    private val tmdbFilterLanguage: String,
    private val searchMovieTmdbApi: SearchMovieTmdbApi
) {
    suspend fun searchMoviesByQuery(query: String, page: Int) =
        searchMovieTmdbApi.searchMoviesByQuery(tmdbApiKey, query, tmdbFilterLanguage, page)
}
```

### Local Data Source

```kotlin
// data_shared/.../local/datasource/MovieLocalDataSource.kt
class MovieLocalDataSource(
    private val movieDao: MovieDao
) {
    suspend fun saveFavoriteMovie(movieDbModel: MovieDbModel) =
        movieDao.create(movieDbModel)

    suspend fun deleteFavoriteMovie(movieDbModel: MovieDbModel) =
        movieDao.delete(movieDbModel)

    suspend fun getMovies() = movieDao.getAll()

    suspend fun getById(movieDbModel: MovieDbModel) =
        movieDao.getById(movieDbModel.id)
}
```

## Best Practices

### 1. Dependency Rule

- Inner layers should not know about outer layers
- Domain layer has no Android dependencies
- Data layer depends only on Domain layer
- Presentation layer can depend on both Domain and Data layers

### 2. Use Cases

- One use case per business operation
- Use `operator fun invoke()` for clean syntax
- Compose use cases for complex operations
- Keep use cases focused and testable

### 3. Mappers

- Use extension functions for clean mapping
- Place mappers in the target layer's package
- Handle null safety in mappers
- Validate data during transformation

### 4. Repositories

- Abstract data source implementation details
- Coordinate between multiple data sources
- Transform data to domain models
- Handle caching strategies here

### 5. Data Sources

- One responsibility per data source
- Remote: API calls only
- Local: Database operations only
- Keep framework-specific code here

## Testing

Each layer can be tested independently:

- **Use Cases**: Unit tests with mocked repositories
- **Repositories**: Unit tests with mocked data sources
- **Data Sources**: Integration tests with mock servers/databases
- **ViewModels**: Unit tests with mocked use cases and Turbine for Flow testing

## References

- [Clean Architecture by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Android Clean Architecture Guide](https://developer.android.com/topic/architecture)
- [Why you need Use Cases/Interactors](https://proandroiddev.com/why-you-need-use-cases-interactors-142e8a6fe576)
