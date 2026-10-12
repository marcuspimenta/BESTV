# Kotlin Android Naming Conventions

Use this reference when naming feature modules, types, files, UI events, routes, or tests. First inspect the active repository and preserve its established conventions where they are clear. These patterns are a consistent starting point, not a requirement to rename existing code.

## Kotlin basics

- Use `PascalCase` for classes, interfaces, objects, enums, and sealed variants.
- Use `camelCase` for packages' declarations, properties, functions, and dependency-injection module values.
- Use `UPPER_SNAKE_CASE` for compile-time constants. Keep constants as narrow in scope as possible, often `private const val` in the file that uses them.
- Use lowercase package names and make the package path reflect the owning feature and layer.
- Name a Kotlin source file after its primary top-level type or cohesive extension group. Avoid putting unrelated declarations in a file just because they share a layer.

## Name types by role and direction

| Role | Suggested shape | Example |
|---|---|---|
| Feature module | `feature_<capability>` | `feature_catalog` |
| Use case | `<Verb><Noun>UseCase` | `SearchItemsUseCase` |
| Domain repository contract | `<Capability>Repository` | `SearchRepository` |
| Data implementation | `Default<Capability>Repository` or a source-specific name | `DefaultSearchRepository` |
| Remote/local adapter | `<Capability>RemoteDataSource` or `<Capability>LocalDataSource` | `CatalogRemoteDataSource` |
| HTTP API interface | `<Capability>Api` or `<Resource>Service` | `CatalogApi` |
| Network DTO | `<Resource>Response` or `<Resource>Dto` | `CatalogResponse` |
| Persistence entity | `<Resource>Entity` or the established database-model suffix | `CatalogEntity` |
| Domain type | The domain concept, optionally suffixed `DomainModel` when needed for clarity | `CatalogItem` |
| Presentation data | `<Resource>UiModel` or `<Resource>ScreenModel` | `CatalogItemUiModel` |
| Boundary mapper | Directional function or mapper name | `toDomainModel()`, `toUiModel()`, `toEntity()` |

Use case names should describe the operation or business intent, not the implementation detail. The repository pattern commonly uses `operator fun invoke(...)` for a single operation, but names such as `search(...)` or `loadNextPage(...)` are clearer when a class owns multiple operations.

Use a distinct suffix for each model layer when those types coexist. Avoid using `ViewModel` for an immutable UI data class: reserve that name for lifecycle-aware state holders, and use `UiModel`, `ScreenModel`, or the project's established equivalent for render data. Keep API field names and database annotations in their own model types rather than carrying those names into domain types by default.

Repository ports and implementations may share a simple name when their packages make the distinction clear, but imports can become ambiguous and require aliases. Prefer a descriptive implementation name when that improves navigation and code review; do not rename an established codebase mechanically just to match this preference.

## Presentation state and Compose names

- Keep related state, event, and effect types parallel: `<Feature>State`, `<Feature>Event`, and `<Feature>Effect`.
- Name events after user intent or an interaction (`QueryChanged`, `ItemSelected`, `RetryRequested`, `LoadMoreRequested`), not after how state is mutated (`SetLoadingTrue`).
- Name effects as outcomes or commands (`NavigateToDetails`, `ShowMessage`, `OpenExternalLink`).
- Give screen-state variants semantic names such as `Initial`, `Loading`, `Empty`, `Loaded`, and `Error`; name section variants after their content role.
- Name lifecycle-aware state holders `<Feature>ViewModel` and use `handleEvent`/`onEvent` consistently with local convention.
- Prefer `FeatureRoute` for the stateful ViewModel/platform adapter and `FeatureScreen` or `FeatureContent` for rendering from state and callbacks. Use `Wrapper` only when it describes a real composition/selection role.
- Name reusable composables by what they render or do (`ItemCard`, `ResultsList`, `ErrorBanner`). Add a platform prefix such as `Tv` or `Mobile` only when separate implementations are justified by materially different interaction or layout behavior.

## Data flow, navigation, DI, and test names

- Use `toDomainModel`, `toUiModel`, and `toEntity` (or clearly equivalent names) to show mapper direction. Avoid multiple unqualified `toViewModel()` extensions in one call site when imports become ambiguous; name or alias the source direction explicitly.
- Name navigation contracts for the destination (`DetailsRoute`) and keep route-builder/parser method names explicit (`createIntent`, `parseArguments`, `getRequiredId`). Keep URI and argument names stable and centralized.
- Name a feature DI declaration `<feature>Module` and keep the value in lower camel case (`catalogModule`). Shared infrastructure modules should be named for the responsibility they provide.
- Name test files and classes after the production type plus `Test` (`SearchItemsUseCaseTest`). Use descriptive scenario names, including Kotlin backtick names where the repository uses them, to make the condition and expected result visible.

## Adapt rather than impose

Before adding names, sample neighboring production files and tests. Follow local spelling choices, acronyms, singular/plural conventions, and existing suffixes. Check that the name distinguishes the layer and responsibility, agrees with the type's actual behavior, and is searchable from the feature name. Call out ambiguous legacy conventions when they affect a new boundary, but avoid broad renames outside the requested change.
