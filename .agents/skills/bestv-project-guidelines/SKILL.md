---
name: bestv-project-guidelines
description: Apply BESTV project conventions when implementing, refactoring, testing, or reviewing code, or explaining its architecture. Covers the repository module layout, naming, TV/mobile UI, dependency wiring, and build checks. Use for BESTV-specific work; use android-feature-architecture for reusable Android design guidance.
---

# BESTV Project Guidelines

Use this skill for coding and reviews in BESTV. BESTV discovers movies and TV shows through TMDb, with Android TV and mobile interfaces sharing data, domain operations, and navigation. Follow the established Clean Architecture and MVI structure, adapting only what the requested change needs.

Use these repository conventions over generic examples, especially for model and screen names. For deeper design decisions, consult the relevant references in [Android Feature Architecture](../android-feature-architecture/SKILL.md). Verify the touched area against current source and build files; documented legacy exceptions are not patterns to extend. Keep this skill aligned when an in-scope change alters a convention or module boundary.

Repository paths below are relative to the root. Read the relevant parts of [README.md](../../../README.md), [CLEAN_ARCHITECTURE.md](../../../CLEAN_ARCHITECTURE.md), [MVI_ARCHITECTURE.md](../../../MVI_ARCHITECTURE.md), and [KOIN_DI.md](../../../KOIN_DI.md) for detail. Some documentation snippets are simplified or older than the implementation; verify signatures, imports, and behavior in the affected source. Use [settings.gradle](../../../settings.gradle) and module build files for the actual dependency graph.

## Module ownership

| Module | Responsibility |
|---|---|
| `app` | Application startup, Koin composition, device capability implementation, image-loader initialization, and app packaging. |
| `feature_workbrowse` | Browse categories, genres, and favorites. |
| `feature_search` | Search queries, results, and pagination. |
| `feature_workdetail`, `feature_castdetail` | Work details/favorites and cast details/credits, respectively. |
| `feature_recommendation` | TV home recommendations, boot handling, background work, and platform provider integration. |
| `model_shared` | Shared API/persistence, domain, and presentation models plus data mappers, separated by packages. This is not a domain-only module; package boundaries are not independent Gradle boundaries. |
| `data_shared` | Retrofit/OkHttp configuration and Room database, DAOs, and local data sources. |
| `presentation_shared` | Shared domain-to-UI mappers, `Resource`, `BaseViewModel`, theme, TV/mobile components, image loading, pagination state, and presentation platform abstractions. |
| `route_shared` | Activity intent builders, argument readers, deep-link contracts/resources, and route DI. |

The app depends on the features and shared modules. Features depend on the shared modules they use; there are no direct feature-to-feature Gradle dependencies. `data_shared`, `presentation_shared`, and `route_shared` each depend on `model_shared`. Preserve this direction and route between features through shared contracts. Layers currently live in packages inside feature modules; a new operation does not require new Gradle modules.

## Layers and data flow

Use packages under `com.pimenta.bestv.<feature>`:

- `data/remote/api`, `data/remote/datasource`, `data/repository`, and feature-specific data models/mappers as needed.
- `domain` for use cases, `domain/model` for feature-owned values, and `domain/repository` for repository interfaces.
- `presentation/model`, `presentation/mapper`, `presentation/viewmodel`, and `presentation/ui/{activity,compose}`.
- `platform` for Android workers, receivers, schedulers, and providers when a feature integrates with platform services.
- `di` for the feature's Koin registrations.

Keep dependencies pointed inward: presentation uses domain; data implements domain-owned repository interfaces and may use infrastructure such as Retrofit, Room, and `data_shared`; domain imports neither presentation nor data. Koin in `di` is the composition edge. Keep the runtime flow explicit: UI event → ViewModel → use case → domain repository interface → injected data/platform implementation → source adapter. Map API/persistence models to domain values in data and domain values to renderable models in presentation. Shared work, cast, and page UI mappers are in `presentation_shared/presentation/mapper`; source mappers assign resource-free `ContentSource.TMDB`, and presentation resolves its “TMDb” label through `Resource`. Use cases typically expose `suspend operator fun invoke(...)`; inject collaborators through constructors. Keep domain contracts free of Android resources, UI models, Retrofit responses, and Room entities.

Follow [SearchMoviesByQueryUseCase](../../../feature_search/src/main/java/com/pimenta/bestv/search/domain/SearchMoviesByQueryUseCase.kt) and its [domain repository](../../../feature_search/src/main/java/com/pimenta/bestv/search/domain/repository/MovieRepository.kt) for the interface boundary. For resource-free section models and presentation mapping, inspect [BrowseSectionViewModelMapper](../../../feature_workbrowse/src/main/java/com/pimenta/bestv/workbrowse/presentation/mapper/BrowseSectionViewModelMapper.kt) and its domain inputs.

Keep raw search text unchanged through use cases and repositories; Retrofit `@Query` handles transport encoding. Room access goes through the local data sources/DAOs in `data_shared`. Preserve cancellation and define partial-failure, pagination, and repeated-write behavior where those operations change; consult [data operations and persistence](../android-feature-architecture/references/data-operations-and-persistence.md) for those cases.

Work Details favorite operations use an ID, `WorkDomainModel.Type`, and desired favorite state; data repositories create Room entities. Recommendation workers, the boot receiver, scheduler, and TV providers are under `feature_recommendation/platform`. The scheduler owns the unique hourly WorkManager request and legacy-tag cleanup. The recommendation provider implements the domain output port directly; its Koin binding is in the feature composition module. Read [background work and platform adapters](../android-feature-architecture/references/background-work-and-platform-adapters.md) when changing recommendations, workers, or receivers.

## Naming and Kotlin conventions

- Modules use `feature_<capability>` and `<responsibility>_shared`; packages use lowercase feature names such as `workbrowse` and `workdetail`.
- Keep the established suffixes: `*Response`, `*DbModel`, `*DomainModel`, `*RemoteDataSource`, `*LocalDataSource`, and `*UseCase`. TMDb Retrofit interfaces use `*TmdbApi`.
- BESTV already uses `WorkViewModel`, `CastViewModel`, and `PageViewModel` for presentation data in `model_shared/.../presentation/model`. Screen state holders such as `SearchViewModel` live in feature `presentation/viewmodel` packages. Preserve this distinction and neighboring naming rather than mechanically renaming existing models to `UiModel`.
- Shared work/cast/page UI mappers are in `presentation_shared/.../presentation/mapper`; keep `model_shared/presentation` for presentation model types and image URL helpers, not persistence conversion.
- Domain interfaces and data implementations can share names such as `MovieRepository`; use explicit import aliases when both occur together, as the feature DI modules do.
- Mappers are usually top-level extensions such as `toDomainModel()` and `toViewModel()`, grouped in `*Mapper.kt` files. Match the existing direction and package ownership.
- Keep `<Feature>State`, `<Feature>Event`, and `<Feature>Effect` parallel. Use `<Feature>WrapperScreen`, `TV<Feature>Screen`, and `Mobile<Feature>Screen` where the feature has separate TV/mobile rendering. DI values use lower camel case, such as `searchModule`, in `SearchModule.kt`.
- Follow [.editorconfig](../../../.editorconfig): four-space Kotlin indentation, 124-character maximum line length, and the configured ktlint rules. Do not replace its explicit rule choices with formatter defaults. For formatting work, use the existing [format-kotlin skill](../format-kotlin/SKILL.md).

## MVI and TV/mobile UI

Screen ViewModels extend [BaseViewModel](../../../presentation_shared/src/main/java/com/pimenta/bestv/presentation/presenter/BaseViewModel.kt), which exposes `StateFlow` state and channel-backed effects. Handle intents through `handleEvent`, update state with `updateState { current -> current.copy(...) }`, and send one-time actions through `emitEffect`. Keep navigation effects separate from durable screen state. Match the feature's state shape; screens do not all have identical loading/content variants.

Activities host Compose and supply platform actions. Wrappers apply `BESTVTheme` and choose TV/mobile screens from device capabilities; see [SearchWrapperScreen](../../../feature_search/src/main/java/com/pimenta/bestv/search/presentation/ui/compose/SearchWrapperScreen.kt). Share the ViewModel, domain logic, and models across both layouts. Collect state with `collectAsStateWithLifecycle()` and handle effects from a composition/lifecycle effect, with one intended consumer. Keep reusable components driven by data and callbacks.

Use components and themes from `presentation_shared`. Preserve remote focus, selection, D-pad/back behavior, and scroll restoration on TV; preserve touch, keyboard, and back behavior on mobile. Put user-visible strings in Android resources and keep resource IDs out of domain models. Reuse `DeviceCapabilities` and its app implementation rather than spreading device detection through features. Read [Compose UI layer](../android-feature-architecture/references/compose-ui-layer.md) and [ViewModel and UI state](../android-feature-architecture/references/viewmodel-ui-state.md) when changing these behaviors.

## Navigation and dependency injection

Internal screen navigation uses Jetpack Navigation 3 destination keys declared in `route_shared`, not Activity intents or imports of another feature's screen. The single [NavigationActivity](../../../app/src/main/java/com/pimenta/bestv/application/presentation/ui/activity/NavigationActivity.kt) resolves startup/external input and hosts [NavigationHost](../../../app/src/main/java/com/pimenta/bestv/application/presentation/ui/compose/NavigationHost.kt). The host keeps a `rememberNavBackStack`, installs the saveable-state and ViewModel-store entry decorators, and composes the feature `NavigationEntryProvider`s.

Each feature owns its `NavigationEntryProvider`, typed entry builder, and registration in that feature's Koin module. `appModule` aggregates providers with `getAll<NavigationEntryProvider>()`. Keep destination keys and the provider interface in `route_shared`; do not put feature screen implementations there. Destinations that can be pushed repeatedly carry a unique `entryId` so Navigation 3 can keep their entry state distinct. Pass stable identifiers when appropriate; preserve the existing `WorkViewModel` payload for Work Details until its route contract is deliberately migrated.

Android intents remain for launcher and external entry points. [WorkDetailsRoute](../../../route_shared/src/main/java/com/pimenta/bestv/route/workdetail/WorkDetailsRoute.kt), for example, builds a package-scoped `ACTION_VIEW` intent for `bestv://workdetail/` with a parcelable argument, and [Intent.toBestvDestination](../../../route_shared/src/main/java/com/pimenta/bestv/route/BestvDestination.kt) maps supported URIs to the initial Navigation 3 key. Keep the route URI, argument builder/reader, and manifest filter consistent. Validate destination arguments and pass required ViewModel parameters through the existing Koin parameter mechanism.

`BesTVApplication` starts Koin with [appModule](../../../app/src/main/java/com/pimenta/bestv/application/di/AppModule.kt), which includes the shared and feature modules. Extend the owning feature module and include any new module at this composition root. Follow [SearchModule](../../../feature_search/src/main/java/com/pimenta/bestv/search/di/SearchModule.kt): `single` for shared infrastructure/APIs, `factory`/`factoryOf` for repositories, data sources, and use cases, and `viewModel`/`viewModelOf` for lifecycle ViewModels. Bind domain repository interfaces to their actual outer implementation: usually data repositories, and the Recommendation output port to its platform TV provider. Preserve qualifiers such as `tmdbApiKey` and `tmdbFilterLanguage`; constructor injection is the default outside platform entry points.

When changing destination registration, verify every key resolves and there are no missing or duplicate registrations. Run `./gradlew :app:verifyNavigationEntries`; `check` and app assemble/bundle tasks depend on this test-backed check. For other Navigation 3 design decisions, use the reusable [Feature Modularization and Navigation 3](../android-feature-architecture/references/feature-modularization-and-navigation.md) reference.

## Build and dependencies

Use the checked-in `./gradlew` wrapper and Groovy `build.gradle` files. [gradle/libs.versions.toml](../../../gradle/libs.versions.toml) owns external library/plugin coordinates and versions plus SDK and Java/JVM target settings. Root `build.gradle` consumes catalog buildscript classpaths and applies shared Android settings. Add dependencies through catalog aliases in the consuming module; do not copy version numbers into this guide or introduce a second version source. Compilation targets and the JDK needed to run Gradle are separate settings; check the active toolchain before diagnosing build failures.

For version checks or bumps, use the [update-dependencies skill](../update-dependencies/SKILL.md) and [dependency management reference](../android-feature-architecture/references/dependency-management.md). The repository uses Dependabot for scheduled updates and has no dedicated dependency-update report task.

Local TMDb configuration is described in the README and implemented in `data_shared/build.gradle`: a nonblank `TMDB_API_KEY` environment value takes precedence over the ignored root `local.properties` value. Builds can complete without a key, but authenticated requests cannot. Keep keys out of tracked files and test fixtures.

## Verification

Choose checks for the changed behavior and modules. Run commands from the repository root; `feature_search` below is an example target to replace with the affected module.

| Change | Appropriate check |
|---|---|
| Kotlin formatting | `./gradlew :feature_search:ktlintCheck`; the root `./gradlew formatKotlin` formats all modules when that scope is requested. |
| Domain, mapper, ViewModel, or DI behavior | `./gradlew :feature_search:testDebugUnitTest`; add the other affected module tasks for shared changes. |
| App integration, resources, manifests, or build setup | `./gradlew :app:assembleDebug` and, when relevant, `./gradlew :app:lintDebug`. |
| Package dependency boundaries | `./gradlew verifyArchitecture`; `:app:preBuild` also runs this check. It guards domain imports, data-to-presentation imports, `model_shared/presentation` persistence imports, and direct feature-to-feature imports. |
| Shared Compose instrumentation | `./gradlew :presentation_shared:connectedDebugAndroidTest` with an appropriate device/emulator. Exercise both TV and mobile when shared UI behavior changes. |
| Documentation/skill-only edits | Check links and `git diff --check`; validate changed skills with the skill-creator validator when available. No Android build is needed solely for prose. |

Unit tests live in `src/test`, generally mirror the production packages, and use JUnit 4, Mockito Kotlin, and coroutine-test. Some modules also declare Turbine or Robolectric; inspect their build files before assuming those libraries are available everywhere. Follow neighboring `*Test` names, use virtual coroutine time, reset a replaced main dispatcher, and assert public state/effects or domain behavior.

For bindings, follow [RepositoryBindingsTest](../../../feature_search/src/test/java/com/pimenta/bestv/search/di/RepositoryBindingsTest.kt): load the production module into an isolated Koin application with substituted infrastructure, resolve domain interfaces, and close the container. This verifies construction, not network or database behavior. Compose instrumentation currently lives in `presentation_shared/src/androidTest`; do not infer complete UI or data integration coverage from the unit suite.

Report which checks actually ran and their outcomes. If setup or a pre-existing failure blocks a check, identify that limitation instead of claiming verification or changing unrelated tooling.
