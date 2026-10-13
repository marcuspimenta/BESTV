# Gradle Dependency Management

Use this reference when adding, removing, or changing an external library, Gradle plugin, or repository in a Kotlin Android project. Dependency scopes and plugin application mechanisms vary, so inspect the active build before editing.

## Use the version catalog as the source of truth

Every external library and Gradle plugin coordinate and version must be managed through the Gradle version catalog in `gradle/libs.versions.toml`. If the project does not have a catalog, create one. Do not put dependency pins in `buildSrc`, convention plugins, root build files, or module build files. If existing dependencies are managed through `buildSrc` constants or inline versions, migrate those declarations into the catalog rather than extending the split setup. `buildSrc` and convention plugins may contain reusable build logic, but they are not dependency-version stores.

Inspect the surrounding build files to learn how catalog aliases are applied:

- `settings.gradle` or `settings.gradle.kts` for plugin management, dependency repositories, included builds, and module inclusion.
- `gradle/libs.versions.toml` for version entries and library/plugin aliases. Keep external dependency coordinates and version pins here.
- Root build files for plugin application and legacy `buildscript` classpaths. Reference catalog aliases for classpath coordinates and versions.
- The consuming module's build file to see the configuration (`implementation`, `api`, `testImplementation`, `androidTestImplementation`, `ksp`, and similar) and any existing dependency grouping conventions.

The plugin application mechanism and repository declarations can vary by project, but dependency coordinates and versions still belong in the catalog. Keep SDK and toolchain settings there when that is the project's established convention.

## Add a library through the version catalog

1. Confirm which module needs the library and whether an existing dependency already provides the required API. Add it only to modules that use it.
2. Check the library's official setup instructions, compatible Android/Kotlin/Gradle constraints, and required repository. Follow the project's compatibility policy; do not treat an old pin as a recommendation.
3. Add or update the version entry and library alias in `gradle/libs.versions.toml`; do not add dependency pins to `buildSrc` or build files. If the catalog is missing or existing pins are split across sources, migrate them into the catalog as part of the change. Avoid repeating literal versions in module build files.
4. Add the alias to the narrowest correct Gradle configuration in each consumer: production runtime, unit tests, instrumentation tests, annotation/code generation, or compiler/plugin classpath as appropriate.
5. If a BOM/platform manages versions, declare the platform in the intended scope and omit individual versions only for artifacts actually managed by that platform. Do not assume every artifact from a vendor belongs to its BOM.
6. Sync/resolve the Gradle graph and run the smallest relevant compile or test task. Inspect dependency insight or dependency reports if version conflicts, unexpected transitive dependencies, or duplicate artifacts appear.

Declare versions and coordinates in the catalog:

```toml
[versions]
example = "<selected-version>"

[libraries]
example-android = { module = "com.example:example-android", version.ref = "example" }
example-test-support = { module = "com.example:example-test-support", version.ref = "example" }
```

Consume the catalog aliases in the module:

```groovy
dependencies {
    implementation libs.example.android
    testImplementation libs.example.test.support
}
```

## Plugins, project modules, and external libraries are different

- A **plugin** changes the build and is declared through the project's plugin-management or buildscript/classpath mechanism. Its version must be declared in the version catalog and referenced through the applicable plugin or classpath mechanism, not pinned inline in a build file.
- An **external library** is a Maven artifact used by production or test source and is declared in a module's `dependencies` block through a configuration appropriate to its use.
- A **project dependency** links another Gradle module and uses the build's project/module notation; it is not a Maven coordinate. Check that the edge is intended and does not create a cycle.
- A **repository** is a source from which Gradle resolves plugins or artifacts. Add one only when the dependency's official coordinates require it and follow repository policy; prefer centralized repository declarations and avoid broad, duplicate repository lists.

Use `api` only when a module intentionally exposes a dependency's types as part of its public compile-time surface. Prefer `implementation` for internal use so consumers do not inherit unnecessary compile dependencies. Keep test-only libraries in test configurations and instrumentation-only libraries out of production runtime. For processors and compiler plugins, use the project's configured KSP/KAPT/compiler-plugin mechanism rather than runtime configurations.

## Review after the change

- Is the version declared once in the version catalog?
- Is the dependency added only to actual consumers and the correct source set/configuration?
- Is a BOM or platform already managing the artifact's version?
- Does the chosen plugin/library version fit the project's Gradle, Kotlin, Android Gradle Plugin, SDK, and JVM constraints?
- Did a new repository or transitive dependency enter the graph? Is it required?
- Do targeted compilation/tests and dependency reports resolve the intended version?

Automated update proposals and dependency reports can identify newer versions, but they do not prove compatibility or stability. Verify release status and compatibility from primary project documentation and release notes before changing version pins. BESTV uses weekly Dependabot updates for Gradle and GitHub Actions, with Gradle patch/minor updates grouped together. Security update PRs are separate and require the dependency graph, Dependabot alerts, and Dependabot security updates to be enabled in GitHub settings. The repository workflow submits the Gradle dependency graph on pushes to `master` and on manual dispatch.

## BESTV current dependency setup

The following inventory lists the main external libraries and build plugins used by BESTV. It does not include every transitive artifact in the resolved dependency graph. Use the entries that fit the target project, and select compatible versions for that project's Gradle, Android Gradle Plugin, Kotlin, SDK, and device requirements.

BESTV centralizes dependency coordinates and versions, including build plugin classpaths, in `gradle/libs.versions.toml`. The same catalog holds the Android SDK and Java/JVM settings. The root `build.gradle` consumes plugin aliases from the catalog, and module build files consume library aliases. Check the consuming module before assuming that a catalog entry is in use. Dependabot updates the Gradle declarations in this catalog as part of its weekly version update checks. BESTV does not provide a local dependency report task; use official release information for manual checks.

### Kotlin, Android, storage, and background work

| Artifact | Role |
| --- | --- |
| `org.jetbrains.kotlin:kotlin-stdlib-jdk7` | Kotlin standard library |
| `org.jetbrains.kotlin:kotlin-metadata-jvm` | Kotlin metadata support consumed through KAPT |
| `org.jetbrains.kotlinx:kotlinx-coroutines-core` | Coroutine primitives used by production code |
| `org.jetbrains.kotlinx:kotlinx-coroutines-android` | Android coroutine dispatchers/integration |
| `androidx.core:core-ktx` | AndroidX Core Kotlin extensions |
| `androidx.lifecycle:lifecycle-viewmodel` | ViewModel support |
| `androidx.lifecycle:lifecycle-runtime-ktx` | Lifecycle runtime Kotlin extensions |
| `androidx.room:room-runtime` | Room database runtime |
| `androidx.room:room-ktx` | Room coroutine and Kotlin extensions |
| `androidx.room:room-compiler` | Room code generation; consumed through KAPT in the data module |
| `androidx.work:work-runtime-ktx` | Background work scheduling |
| `androidx.recommendation:recommendation` | Android TV recommendation integration |
| `androidx.tvprovider:tvprovider` | Android TV home-screen channel/program integration |

### Compose, TV UI, and images

| Artifact | Role |
| --- | --- |
| `androidx.compose:compose-bom` | Aligns the Compose artifacts below |
| `androidx.compose.ui:ui` | Compose UI runtime and primitives |
| `androidx.compose.ui:ui-tooling` | Debug UI inspection tooling |
| `androidx.compose.ui:ui-tooling-preview` | Preview annotations and support |
| `androidx.compose.foundation:foundation` | Foundation layouts, gestures, and scrolling |
| `androidx.compose.material3:material3` | Material 3 components |
| `androidx.compose.ui:ui-test-junit4` | Compose UI instrumentation test APIs |
| `androidx.compose.ui:ui-test-manifest` | Debug/test manifest support for Compose tests |
| `androidx.tv:tv-foundation` | TV-specific focus, scrolling, and foundation APIs |
| `androidx.tv:tv-material` | TV-oriented Material components |
| `androidx.activity:activity-ktx` | Activity Kotlin extensions |
| `androidx.activity:activity-compose` | Compose integration for Activity |
| `io.coil-kt.coil3:coil-compose` | Image loading for Compose |
| `io.coil-kt.coil3:coil-network-okhttp` | OkHttp network fetcher for Coil |
| `com.airbnb.android:lottie-compose` | Lottie animations in Compose |

The Compose BOM does not manage TV Foundation, TV Material, Coil, Activity, or Lottie; those dependencies need their own version declarations.

### Networking, serialization, logging, and dependency injection

| Artifact | Role |
| --- | --- |
| `com.squareup.retrofit2:retrofit` | HTTP API client |
| `com.squareup.retrofit2:converter-gson` | Retrofit JSON conversion |
| `com.squareup.okhttp3:okhttp` | HTTP transport |
| `com.squareup.okhttp3:logging-interceptor` | HTTP request/response logging |
| `com.google.code.gson:gson` | JSON serialization/deserialization |
| `com.jakewharton.timber:timber` | Application logging |
| `io.insert-koin:koin-core` | Dependency injection core |
| `io.insert-koin:koin-android` | Android integration and ViewModel resolution |
| `io.insert-koin:koin-androidx-compose` | Compose integration |
| `io.insert-koin:koin-androidx-workmanager` | WorkManager integration |

### Unit and UI test libraries

These are declared in test or instrumentation configurations, not production runtime configurations. Individual modules use only the subset needed by their tests.

| Artifact | Role |
| --- | --- |
| `junit:junit` | JVM unit-test framework |
| `org.mockito:mockito-core` | Mocking framework |
| `org.mockito:mockito-inline` | Inline mock maker used by Mockito tests |
| `org.mockito.kotlin:mockito-kotlin` | Kotlin-friendly Mockito helpers |
| `org.jetbrains.kotlinx:kotlinx-coroutines-test` | Coroutine test dispatchers and schedulers |
| `org.jetbrains.kotlin:kotlin-test-junit` | Kotlin test assertions/JUnit integration |
| `app.cash.turbine:turbine` | Flow testing |
| `org.robolectric:robolectric` | Android framework behavior in local JVM tests |
| `androidx.test.ext:junit` | AndroidX instrumentation test runner integration |

### Build plugins and tooling

These are build-time tools, not app libraries. BESTV declares them through root `build.gradle` buildscript classpaths, except the Kotlin Parcelize plugin which is applied alongside the Kotlin plugin.

| Plugin/tool | Role |
| --- | --- |
| Gradle wrapper | Build runtime |
| Android Gradle Plugin (`com.android.tools.build:gradle`) | Android build tasks and variants |
| Kotlin Gradle plugin (`org.jetbrains.kotlin:kotlin-gradle-plugin`) | Kotlin compilation |
| Kotlin Compose compiler Gradle plugin | Compose compiler integration |
| `com.android.legacy-kapt` Gradle plugin | KAPT integration used for Room compiler processing |
| `org.jlleitschuh.gradle:ktlint-gradle` | Kotlin formatting/lint tasks |
| Kotlin Parcelize plugin | Parcelable generation |
