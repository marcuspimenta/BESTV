---
name: bestv-project-guidelines
description: Apply BESTV project conventions when implementing, refactoring, testing, or reviewing code, or explaining its architecture. Covers the repository module layout, naming, TV/mobile UI, dependency wiring, and build checks. Use for BESTV-specific work; use android-feature-architecture for reusable Android design guidance.
---

# BESTV Project Guidelines

Use this skill for coding and reviews in BESTV. BESTV discovers movies and TV shows through TMDb, with Kotlin Multiplatform shared data and domain logic, Compose Multiplatform mobile screens for Android and iOS, and a dedicated Android TV interface. Follow the established Clean Architecture and MVI structure, adapting only what the requested change needs.

Use these repository conventions over generic examples, especially for model and screen names. For deeper design decisions, consult the relevant references in [Android Feature Architecture](../android-feature-architecture/SKILL.md). Verify the touched area against current source and build files; documented legacy exceptions are not patterns to extend. Keep this skill aligned when an in-scope change alters a convention or module boundary.

Repository paths below are relative to the root. Read the relevant parts of [README.md](../../../README.md), [CLEAN_ARCHITECTURE.md](../../../CLEAN_ARCHITECTURE.md), [MVI_ARCHITECTURE.md](../../../MVI_ARCHITECTURE.md), and [KOIN_DI.md](../../../KOIN_DI.md) for detail. Some documentation snippets are simplified or older than the implementation; verify signatures, imports, and behavior in the affected source. Use [settings.gradle](../../../settings.gradle) and module build files for the actual dependency graph.

## Module ownership

| Module | Responsibility |
|---|---|
| `androidApp` | Android application startup, Koin composition, network configuration, device capability implementation, image-loader initialization, and app packaging. |
| `iosAppFramework` | iOS Compose entry point, iOS platform bindings, and the umbrella framework consumed by the `iosApp` Xcode target. |
| `common` | KMP portable config, response/domain/presentation models, mappers, Ktor setup, SQLDelight schema/generated queries/persistence/local data sources, route contracts, shared Compose primitives/theme, and shared Koin bindings in `commonMain`; Android resources, Intent adapters, TV Compose primitives/theme, platform Koin bindings, the SQLDelight Android driver, image-loader setup, and OkHttp engine in `androidMain`. |
| `feature_workbrowse` | Browse categories, genres, and favorites. |
| `feature_search` | Search queries, results, and pagination. |
| `feature_workdetail`, `feature_castdetail` | Work details/favorites and cast details/credits, respectively. |
| `feature_recommendation` | TV home recommendations, boot handling, background work, and platform provider integration. |

`androidApp` depends on the features and shared modules. The Xcode target is `iosApp`; its `iosAppFramework` Gradle module assembles the iOS entry point and feature modules into the framework consumed by Xcode. Features depend on the shared modules they use; there are no direct feature-to-feature Gradle dependencies. The `common` module and mobile features are Kotlin Multiplatform modules with Android and iOS targets. `common` owns the SQLDelight schema, generated queries, persistence models, and platform-neutral local data sources. Common database bindings consume `SqlDriver`; Android's driver is bound in `common`'s `androidMain`, while iOS provides its native driver in `iosAppFramework`. Keep platform-only APIs in platform source sets and route between features through shared contracts. Layers currently live in packages inside feature modules; a new operation does not require new Gradle modules.

## Layers and data flow

Use packages under `com.pimenta.bestv.<feature>`:

- `data/remote/api`, `data/remote/datasource`, `data/repository`, and feature-specific data models/mappers as needed.
- `domain` for use cases, `domain/model` for feature-owned values, and `domain/repository` for repository interfaces.
- `presentation/model`, `presentation/mapper`, `presentation/viewmodel`, and `presentation/ui/compose/{mobile,tv}`. Android app Activities and the app-level Navigation 3 host live in `androidApp`.
- `platform` for Android workers, receivers, schedulers, and providers when a feature integrates with platform services.
- `di` for the feature's Koin registrations.

Keep dependencies pointed inward: presentation uses domain; data implements domain-owned repository interfaces and may use infrastructure such as Ktor and SQLDelight; domain imports neither presentation nor data. Koin in `di` is the composition edge. Keep the runtime flow explicit: UI event → ViewModel → use case → domain repository interface → injected data/platform implementation → source adapter. Map API/persistence models to domain values in data and domain values to renderable models in presentation. Shared work, cast, and page models and mappers are in `common/src/commonMain/kotlin/com/pimenta/bestv/presentation`; their mappers use `ContentSourceLabelProvider`, implemented by Android's `Resource` adapter and the iOS platform binding. Use cases typically expose `suspend operator fun invoke(...)`; inject collaborators through constructors. Keep domain contracts free of Android resources, UI models, Ktor response DTOs, and SQLDelight types.

Follow [SearchMoviesByQueryUseCase](../../../feature_search/src/commonMain/kotlin/com/pimenta/bestv/search/domain/SearchMoviesByQueryUseCase.kt) and its [domain repository](../../../feature_search/src/commonMain/kotlin/com/pimenta/bestv/search/domain/repository/MovieRepository.kt) for the interface boundary. For resource-free section models and presentation mapping, inspect [BrowseSectionViewModelMapper](../../../feature_workbrowse/src/commonMain/kotlin/com/pimenta/bestv/workbrowse/presentation/mapper/BrowseSectionViewModelMapper.kt) and its domain inputs.

Keep raw search text unchanged through use cases and repositories; Ktor handles transport encoding. SQLDelight schemas, query implementations, persistence models, and reusable local data sources belong to `common`; feature-specific mapping from persistence models to domain values stays in `feature/data/local/mapper`. Preserve cancellation and define partial-failure, pagination, and repeated-write behavior where those operations change; consult [data operations and persistence](../android-feature-architecture/references/data-operations-and-persistence.md) for those cases.

Work Details favorite operations use an ID, `WorkDomainModel.Type`, and desired favorite state; data repositories create SQLDelight persistence models and delegate writes to shared local data sources. Recommendation workers, the boot receiver, scheduler, and TV providers are under `feature_recommendation/platform`. The scheduler owns the unique hourly WorkManager request and legacy-tag cleanup. The recommendation provider implements the domain output port directly; its Koin binding is in the feature composition module. Read [background work and platform adapters](../android-feature-architecture/references/background-work-and-platform-adapters.md) when changing recommendations, workers, or receivers.

## Naming and Kotlin conventions

- App entry points use platform-first names: `androidApp` for the Android Gradle application and `iosApp` for the Xcode application. The iOS-only Kotlin umbrella framework module is `iosAppFramework`. Feature modules use `feature_<capability>`; packages use lowercase feature names such as `workbrowse` and `workdetail`.
- Keep the established suffixes: `*Response`, `*DbModel`, `*DomainModel`, `*RemoteDataSource`, `*LocalDataSource`, and `*UseCase`. TMDb Ktor API interfaces use `*TmdbApi`.
- BESTV uses serializable `WorkViewModel`, `CastViewModel`, and `PageViewModel` presentation models in `commonMain/presentation/model`. Screen state holders such as `SearchViewModel` live in feature `presentation/viewmodel` packages. Preserve this distinction and neighboring naming rather than mechanically renaming existing models to `UiModel`.
- Shared work/cast/page UI mappers and image URL helpers live in `commonMain/presentation/mapper`; keep persistence conversion in data-layer mappers.
- Domain interfaces and data implementations can share names such as `MovieRepository`; use explicit import aliases when both occur together, as the feature DI modules do.
- Mappers are usually top-level extensions such as `toDomainModel()` and `toViewModel()`, grouped in `*Mapper.kt` files. Match the existing direction and package ownership.
- Keep `<Feature>State`, `<Feature>Event`, and `<Feature>Effect` parallel. Use `<Feature>WrapperScreen`, `TV<Feature>Screen`, and `Mobile<Feature>Screen` where the feature has separate TV/mobile rendering. DI values use lower camel case, such as `searchModule`, in `SearchModule.kt`.
- Follow [.editorconfig](../../../.editorconfig): four-space Kotlin indentation, 124-character maximum line length, and the configured ktlint rules. Do not replace its explicit rule choices with formatter defaults. For formatting work, use the existing [format-kotlin skill](../format-kotlin/SKILL.md).

## MVI and TV/mobile UI

Feature ViewModels extend the multiplatform [BaseViewModel](../../../common/src/commonMain/kotlin/com/pimenta/bestv/presentation/viewmodel/BaseViewModel.kt), which exposes `StateFlow` state and channel-backed effects. Handle events through `handleEvent`, update state with `updateState { current -> current.copy(...) }`, and send one-time actions through `emitEffect`. Keep navigation effects separate from durable screen state. Shared effects carry destination keys or semantic values such as URLs, never Android `Intent` objects. Match the feature's state shape; screens do not all have identical loading/content variants.

Activities and app hosts supply platform actions. Mobile feature screens in `commonMain` are shared by Android and iOS; Android wrappers apply `BESTVTheme` and choose TV/mobile layouts from device capabilities; see [SearchWrapperScreen](../../../feature_search/src/androidMain/kotlin/com/pimenta/bestv/search/presentation/ui/compose/SearchWrapperScreen.kt). Share the ViewModel, domain logic, models, and mobile composables across platforms. Collect state with `collectAsStateWithLifecycle()` and handle the consuming effect flow from a composition/lifecycle effect with one intended collector. Keep reusable components driven by data and callbacks.

Use shared mobile components and `BESTVTheme` from `common/src/commonMain/kotlin/com/pimenta/bestv/presentation`; keep TV components and theme in `common/src/androidMain`. Shared mobile strings and images use Compose resources under feature/common `src/commonMain/composeResources`; place shared fonts there if introduced. Android-only TV strings and resources remain in Android resources. Preserve remote focus, selection, D-pad/back behavior, and scroll restoration on TV; preserve touch, keyboard, and back behavior on mobile. Keep resource IDs out of domain models. Reuse `DeviceCapabilities` and the platform bindings rather than spreading device detection through features. Read [Compose UI layer](../android-feature-architecture/references/compose-ui-layer.md) and [ViewModel and UI state](../android-feature-architecture/references/viewmodel-ui-state.md) when changing these behaviors.

## Navigation and dependency injection

Shared destination keys, route contracts, and URI parsing live in `common/src/commonMain/kotlin/com/pimenta/bestv/presentation/route`. Android Intent builders/parsers and Android route DI live in `common/src/androidMain`; the single [NavigationActivity](../../../androidApp/src/main/java/com/pimenta/bestv/presentation/ui/activity/MainActivity.kt) resolves Android startup/external input and hosts [NavigationHost](../../../androidApp/src/main/java/com/pimenta/bestv/presentation/ui/compose/BestvApp.kt). The host keeps a `rememberNavBackStack`, installs the saveable-state and ViewModel-store entry decorators, and composes the feature `NavigationEntryProvider`s. The iOS renderer and its deep-link handling live in `iosAppFramework`.

Each Android feature owns its `NavigationEntryProvider`, typed entry builder, and registration in that feature's Koin module. `appModule` aggregates providers with `getAll<NavigationEntryProvider>()`. Shared destination keys and the `NavigationEntryProvider` contract live in `commonMain`; Android Navigation 3 entry implementations stay with their feature's `androidMain`. Do not put feature screen implementations in `common`. Destinations that can be pushed repeatedly carry a unique `entryId` so Navigation 3 can keep their entry state distinct. Pass stable identifiers when appropriate.

Android intents remain at the Android app boundary for launcher and external entry points. [WorkDetailsRoute](../../../common/src/androidMain/kotlin/com/pimenta/bestv/presentation/route/workdetail/WorkDetailsRoute.kt) builds a package-scoped `ACTION_VIEW` intent for `bestv://workdetail/` with a Kotlin Serialization JSON extra. [Intent.toBestvDestination](../../../common/src/androidMain/kotlin/com/pimenta/bestv/presentation/route/IntentExt.kt) delegates supported URI parsing to the common [`BestvDestination`](../../../common/src/commonMain/kotlin/com/pimenta/bestv/presentation/route/BestvDestination.kt) contract. Keep the route URI, argument builder/reader, and manifest filter consistent. Validate destination arguments and pass required ViewModel parameters through the existing Koin parameter mechanism.

`BesTVApplication` starts Koin with [appModule](../../../androidApp/src/main/java/com/pimenta/bestv/di/AppModule.kt), which includes the shared and feature modules. Extend the owning feature module and include any new module at this composition root. Follow [SearchModule](../../../feature_search/src/commonMain/kotlin/com/pimenta/bestv/search/di/SearchModule.kt): `single` for shared infrastructure/APIs, `factory`/`factoryOf` for repositories, data sources, and use cases, and `viewModel`/`viewModelOf` for lifecycle ViewModels. Bind domain repository interfaces to their actual outer implementation: usually data repositories, and the Recommendation output port to its platform TV provider. Preserve qualifiers such as `tmdbApiKey` and `tmdbFilterLanguage`; constructor injection is the default outside platform entry points.

When changing destination registration, verify every key resolves and there are no missing or duplicate registrations. Run `./gradlew :androidApp:verifyNavigationEntries`; `check` and Android app assemble/bundle tasks depend on this test-backed check. For other Navigation 3 design decisions, use the reusable [Feature Modularization and Navigation 3](../android-feature-architecture/references/feature-modularization-and-navigation.md) reference.

## Build and dependencies

Use the checked-in `./gradlew` wrapper and Groovy `build.gradle` files. [gradle/libs.versions.toml](../../../gradle/libs.versions.toml) owns external library/plugin coordinates and versions plus SDK and Java/JVM target settings. Root `build.gradle` consumes catalog buildscript classpaths and applies shared Android settings. Add dependencies through catalog aliases in the consuming module; do not copy version numbers into this guide or introduce a second version source. Compilation targets and the JDK needed to run Gradle are separate settings; check the active toolchain before diagnosing build failures.

For version checks or bumps, use the [update-dependencies skill](../update-dependencies/SKILL.md) and [dependency management reference](../android-feature-architecture/references/dependency-management.md). The repository uses Dependabot for scheduled updates and has no dedicated dependency-update report task.

Local TMDb configuration is described in the README and implemented in `androidApp/build.gradle`: a nonblank `TMDB_ACCESS_TOKEN` environment value takes precedence over the ignored root `local.properties` value. The Android app owns the token in its `BuildConfig`; shared URL and language constants live in `commonMain`. Builds can complete without a token, but authenticated requests cannot. Keep keys out of tracked files and test fixtures.

## Verification

Choose checks for the changed behavior and modules. Run commands from the repository root; `feature_search` below is an example target to replace with the affected module.

| Change | Appropriate check |
|---|---|
| Kotlin formatting | `./gradlew :feature_search:ktlintCheck`; the root `./gradlew formatKotlin` formats all modules when that scope is requested. |
| Domain, mapper, ViewModel, or DI behavior | For KMP features, `./gradlew :feature_search:testAndroidHostTest`; for the Android-only recommendation module, use its `testDebugUnitTest` task. Add affected module tasks for shared changes. |
| App integration, resources, manifests, or build setup | `./gradlew :androidApp:assembleDebug` and, when relevant, `./gradlew :androidApp:lintDebug`. |
| Package dependency boundaries | `./gradlew verifyArchitecture`; `:androidApp:preBuild` also runs this check. It guards domain imports, data-to-presentation imports, shared persistence boundaries, and direct feature-to-feature imports. |
| Compose instrumentation | Feature device tests live under `feature_*/src/androidDeviceTest`; run the owning feature's Android device-test task with an attached device. Exercise TV and mobile behavior when UI changes. |
| Documentation/skill-only edits | Check links and `git diff --check`; validate changed skills with the skill-creator validator when available. No Android build is needed solely for prose. |

In the KMP `common` module, Android host tests live in `src/androidHostTest` and instrumentation tests live in `src/androidDeviceTest`. Android-only modules continue to use `src/test/java` and `src/androidTest/java`. Tests generally mirror production packages and use JUnit 4, Mockito Kotlin, and coroutine-test. Some modules also declare Turbine or Robolectric; inspect their build files before assuming those libraries are available everywhere. Follow neighboring `*Test` names, use virtual coroutine time, reset a replaced main dispatcher, and assert public state/effects or domain behavior.

For KMP feature bindings, follow [RepositoryBindingsTest](../../../feature_search/src/androidHostTest/kotlin/com/pimenta/bestv/search/di/RepositoryBindingsTest.kt): load the production module into an isolated Koin application with substituted infrastructure, resolve domain interfaces, and close the container. This verifies construction, not network or database behavior. Do not infer complete UI or data integration coverage from the unit suite.

Report which checks actually ran and their outcomes. If setup or a pre-existing failure blocks a check, identify that limitation instead of claiming verification or changing unrelated tooling.
