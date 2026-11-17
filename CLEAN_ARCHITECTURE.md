# Clean Architecture in BESTV

This guide describes the architecture implemented by the current BESTV packages. Feature code is organized into `presentation`, `domain`, `data`, `platform`, and `di` packages. Shared Android code, shared TMDb response DTOs, and Ktor client configuration live in `common`; the KMP `data_shared` module owns the SQLDelight schema and portable local data sources. Each feature owns its typed TMDb API client.

![Clean Architecture package dependency diagram](docs/clean-architecture-package-dependencies.svg)

## Dependency rule

Source dependencies point inward toward domain policy:

- **Presentation depends on domain.** ViewModels invoke use cases and map domain values into presentation state.
- **Data depends on domain contracts.** Repository implementations implement interfaces owned by domain and return domain values. Data code also uses its outward details, such as Ktor, SQLDelight, and shared data models.
- **Domain depends on neither presentation nor data.** It owns use cases, domain models, and repository interfaces. It does not import Android, SQLDelight, Ktor, WorkManager, or UI models.
- **Platform adapters depend inward on domain contracts.** They wrap Android services and implement domain output ports where appropriate.
- **Dependency injection is the composition edge.** App and feature Koin modules know the concrete types and connect interfaces to implementations.

The dependency arrows describe compile-time references. At runtime, a use case calls a domain repository interface; the injected data or platform implementation performs the operation and returns domain values.

## Feature package layout

Feature folders follow this shape; individual features include only the packages they need:

```text
feature_search/
├── presentation/
│   ├── model/                 # Screen state, events, and effects
│   ├── viewmodel/
│   └── ui/                    # Activities and Compose screens
├── domain/
│   ├── *UseCase.kt
│   └── repository/            # Interfaces owned by domain
├── data/
│   ├── remote/api/
│   ├── remote/datasource/
│   └── repository/            # Implements domain interfaces
└── di/                        # Koin composition for this feature
```

Recommendation also has a `platform/` package for its boot receiver, WorkManager worker and scheduler, and Android TV providers. Keep platform-specific implementations at this outer edge rather than in domain.

## Shared packages and models

BESTV separates portable local persistence from Android-specific shared infrastructure. Existing Kotlin package names remain grouped by responsibility under `com.pimenta.bestv`:

```text
common/src/main/java/com/pimenta/bestv/
├── data/                      # Network, API models, and data mappers
├── domain/                    # Domain models and coroutine helpers
├── di/                        # Shared presentation DI
└── presentation/
    ├── mapper/                # Shared domain-to-UI mappers and image URL helpers
    ├── model/                 # WorkViewModel, CastViewModel, PageViewModel, WorkType
    ├── platform/              # Resource and device capability abstractions
    ├── route/                 # Deep links, navigation keys, and route DI
    ├── ui/                    # Shared Compose UI
    └── viewmodel/             # BaseViewModel

data_shared/src/commonMain/
├── sqldelight/                 # Portable SQLite schema and typed queries
└── kotlin/.../data/local/      # Portable persistence models and data sources

feature_<capability>/data/local/
└── mapper/                    # Persistence model to domain mapping
```

The `common` module has one generated resource namespace (`com.pimenta.bestv.common.R`) and BuildConfig (`com.pimenta.bestv.common.BuildConfig`). It owns shared serializable TMDb response DTOs, the Android Ktor client configuration with the OkHttp engine, and Android composition. Typed endpoint clients and feature-specific response models stay in their feature's `data/remote` package. The KMP `data_shared` module owns SQLDelight-generated query APIs, portable persistence models, and local database operations; Android creates the SQLDelight driver in `common`'s database DI module.

## Mapping rules

### API and persistence models to domain

Data mappers return resource-free domain values. Keep API response models and shared API mappers under `common/data/model`; persistence model mappers stay in the feature's `data/local/mapper`. For work and cast values, the data mapper assigns `ContentSource.TMDB`; it does not read Android strings or import `common/presentation`.

```kotlin
// common/src/main/java/com/pimenta/bestv/data/model/mapper/WorkResponseMapper.kt
fun WorkResponse.toDomainModel(): WorkDomainModel = WorkDomainModel(
    id = id,
    source = ContentSource.TMDB,
    // Map API fields and determine the domain work type.
)
```

Repository implementations call API mappers after fetching responses. Feature repositories use shared SQLDelight local data sources and feature mappers to convert persistence models into domain values. Keep the SQLite schema, generated queries, persistence models, and shared local data sources in `data_shared`; keep domain mapping with the feature that owns the domain type.

### Domain values to UI models

Shared UI mappers live in `common/presentation/mapper` and accept `Resource` when the mapping needs a localized label. The mapper resolves `ContentSource.TMDB` to the existing “TMDb” string. Feature-specific UI mappings stay in that feature's `presentation/mapper` package.

```kotlin
// common/src/main/java/com/pimenta/bestv/presentation/mapper/WorkViewModelMapper.kt
fun WorkDomainModel.toViewModel(resource: Resource): WorkViewModel?
```

Do not put UI-to-database mappers in presentation. For Work Details favorites, the domain operation receives a work ID, `WorkDomainModel.Type`, and desired favorite state. The data repository creates a `MovieDbModel` or `TvShowDbModel` inside the data boundary.

## Repository ports and dependency injection

Each feature owns repository interfaces under `domain/repository`. Use cases depend on those interfaces, not on implementation classes. The feature's Koin module binds the port to the data implementation:

```text
feature_search/domain/repository/MovieRepository
                 ▲
                 │ implemented by
feature_search/data/repository/MovieRepository
```

Koin modules may import domain, data, presentation, and platform types because they compose the application. Keep these cross-package references in `di` rather than moving them into domain or presentation business code.

Most features bind their domain repository ports to repositories in `data`. Recommendation has two different ports:

- Its movie input port is implemented in `data`.
- Its recommendation output port is implemented directly by an Android TV provider in `platform`.

The Work Details, Browse, Search, and Cast Details modules follow the regular domain-port-to-data-repository pattern. See [KOIN_DI.md](KOIN_DI.md) for module registration and binding details.

## Example request flow

Search illustrates the runtime flow and the compile-time boundaries:

```text
Search Compose UI
    → SearchViewModel
    → Search use case
    → domain MovieRepository interface
    → data MovieRepository implementation
    → remote data source / Ktor API client
    → TMDb

TMDb response
    → data mapper
    → WorkDomainModel / PageDomainModel
    → SearchViewModel
    → common/presentation mapper + Resource
    → UI state and screen
```

The arrows in this request flow show calls and returned data. They do not mean that domain imports data: the use case calls an interface declared in domain, and Koin supplies the concrete implementation at runtime.

## Recommendation platform work

The boot receiver calls `RecommendationScheduler` in `platform`. The scheduler enqueues a unique hourly periodic request with `ExistingPeriodicWorkPolicy.KEEP`, so repeated boot broadcasts retain an existing schedule. During migration it cancels requests carrying the legacy `RECOMMENDATION` tag; the new request does not use that tag.

`RecommendationWorker` invokes the domain use case. It propagates coroutine cancellation, retries recoverable I/O errors and HTTP 429/5xx responses, and returns failure for other errors. The TV provider updates a saved preview program using its stored program ID and inserts a replacement when that ID is stale.

## Architecture checks

Run the root Gradle task:

```shell
./gradlew verifyArchitecture
```

`:app:preBuild` depends on this task. It scans production Kotlin imports and currently rejects:

- Domain imports from data, presentation, Android/AndroidX, Ktor, or Koin.
- Data imports from presentation.
- Data imports from common presentation packages and presentation imports from data.
- Direct imports from another feature package.

Platform adapters and feature DI are allowed to form the composition edge. The check is a source import guard, not a substitute for Gradle module boundaries or code review.

## Practices for new code

- Define a domain repository interface when a use case needs an external capability; implement it in data or a platform adapter.
- Keep domain models independent of API, database, Android resource, and UI types.
- Map API and persistence types into domain in the outer data layer.
- Map domain values into screen-specific models in presentation.
- Express commands as requested outcomes where practical, such as setting a favorite state to `true` or `false`.
- Keep framework scheduling, receivers, providers, and service APIs in platform adapters.
- Wire implementations in Koin modules at the composition edge.
- Add or update tests at the layer whose behavior changed, including repository bindings and adapter behavior where relevant.
