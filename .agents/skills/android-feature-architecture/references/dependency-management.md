# Gradle Dependency Management

Use this reference when adding, removing, or changing an external library, Gradle plugin, or repository in a Kotlin Android project. Dependency declaration mechanisms vary, so inspect the active build before editing.

## Find the project's source of truth

Inspect, as applicable:

- `settings.gradle` or `settings.gradle.kts` for plugin management, dependency repositories, included builds, and module inclusion.
- `gradle/libs.versions.toml` for a Gradle version catalog and library/plugin aliases.
- `buildSrc/` or a convention-plugin included build for shared version constants, dependency coordinates, build configuration, and reusable Gradle logic.
- The root build files for legacy `buildscript` classpath declarations or plugin versions.
- The consuming module's build file to see the configuration (`implementation`, `api`, `testImplementation`, `androidTestImplementation`, `ksp`, and similar) and any existing dependency grouping conventions.

Do not assume that a project uses a version catalog, `buildSrc`, or a single version source. Some projects intentionally have separate places for plugin versions, library versions, and SDK/toolchain settings. Record the actual source of truth before changing a dependency, and avoid migrating the project's declaration system just to add one library.

## Add a library through the existing mechanism

1. Confirm which module needs the library and whether an existing dependency already provides the required API. Add it only to modules that use it.
2. Check the library's official setup instructions, compatible Android/Kotlin/Gradle constraints, and required repository. Follow the versions and compatibility policy already used by the project; do not treat an old constant as a recommendation.
3. Add or update one centralized version entry and one library alias/coordinate in the project's current mechanism. Avoid repeating a literal version in multiple module build files.
4. Add the alias to the narrowest correct Gradle configuration in each consumer: production runtime, unit tests, instrumentation tests, annotation/code generation, or compiler/plugin classpath as appropriate.
5. If a BOM/platform manages versions, declare the platform in the intended scope and omit individual versions only for artifacts actually managed by that platform. Do not assume every artifact from a vendor belongs to its BOM.
6. Sync/resolve the Gradle graph and run the smallest relevant compile or test task. Inspect dependency insight or dependency reports if version conflicts, unexpected transitive dependencies, or duplicate artifacts appear.

For a `buildSrc` constants pattern, the shape is commonly:

```kotlin
object Versions {
    const val example = "<selected-version>"
}

object Libs {
    const val example = "com.example:example-android:${Versions.example}"
    const val exampleTestSupport = "com.example:example-test-support:${Versions.example}"
}
```

The module then consumes the centralized alias using the project’s existing DSL style:

```kotlin
dependencies {
    implementation(Libs.example)
    testImplementation(Libs.exampleTestSupport)
}
```

In a version catalog, the equivalent is typically a version entry plus a library alias in `libs.versions.toml`, followed by `implementation(libs.example)` in the module. Prefer the mechanism already established in the repository. Keep aliases descriptive, consistent, and scoped by purpose rather than creating a new catalog convention for one dependency.

## Plugins, project modules, and external libraries are different

- A **plugin** changes the build and is declared through the project's plugin-management or buildscript/classpath mechanism. Its version belongs with the other plugin pins or plugin aliases, not among app runtime libraries unless the project intentionally centralizes them together.
- An **external library** is a Maven artifact used by production or test source and is declared in a module's `dependencies` block through a configuration appropriate to its use.
- A **project dependency** links another Gradle module and uses the build's project/module notation; it is not a Maven coordinate. Check that the edge is intended and does not create a cycle.
- A **repository** is a source from which Gradle resolves plugins or artifacts. Add one only when the dependency's official coordinates require it and follow repository policy; prefer centralized repository declarations and avoid broad, duplicate repository lists.

Use `api` only when a module intentionally exposes a dependency's types as part of its public compile-time surface. Prefer `implementation` for internal use so consumers do not inherit unnecessary compile dependencies. Keep test-only libraries in test configurations and instrumentation-only libraries out of production runtime. For processors and compiler plugins, use the project's configured KSP/KAPT/compiler-plugin mechanism rather than runtime configurations.

## Review after the change

- Is the version declared once in the intended source of truth?
- Is the dependency added only to actual consumers and the correct source set/configuration?
- Is a BOM or platform already managing the artifact's version?
- Does the chosen plugin/library version fit the project's Gradle, Kotlin, Android Gradle Plugin, SDK, and JVM constraints?
- Did a new repository or transitive dependency enter the graph? Is it required?
- Do targeted compilation/tests and dependency reports resolve the intended version?

Dependency update tasks can report newer versions, but they do not prove compatibility or stability. Verify release status and compatibility from primary project documentation and release notes before changing version pins.

## BESTV starter-stack example

The following inventory records external libraries and build plugins directly declared by BESTV. It is a concrete starter-stack example for a Kotlin Android TV application; it does not include every transitive artifact in the resolved dependency graph. Use the entries that fit the target project, and select compatible versions for that project's Gradle, Android Gradle Plugin, Kotlin, SDK, and device requirements.

The inventory was checked against `buildSrc/src/main/kotlin/Dependencies.kt`, the root `build.gradle`, the Gradle wrapper, and module `build.gradle` files. BESTV centralizes most library coordinates and versions in `buildSrc`; module build files select the aliases they actually consume. Keep this distinction when adapting the list: a centralized declaration alone does not mean a module uses a dependency.

### Kotlin, Android, storage, and background work

| Artifact | Role |
| --- | --- | --- |
| `org.jetbrains.kotlin:kotlin-stdlib-jdk7` | Kotlin standard library |
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
| --- | --- | --- |
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
| --- | --- | --- |
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
| --- | --- | --- |
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
| --- | --- | --- |
| Gradle wrapper | Build runtime |
| Android Gradle Plugin (`com.android.tools.build:gradle`) | Android build tasks and variants |
| Kotlin Gradle plugin (`org.jetbrains.kotlin:kotlin-gradle-plugin`) | Kotlin compilation |
| Kotlin Compose compiler Gradle plugin | Compose compiler integration |
| `com.android.legacy-kapt` Gradle plugin | KAPT integration used for Room compiler processing |
| `org.jlleitschuh.gradle:ktlint-gradle` | Kotlin formatting/lint tasks |
| `io.github.ben-manes:gradle-versions-plugin` | Dependency version reporting |
| Kotlin Parcelize plugin | Parcelable generation |
