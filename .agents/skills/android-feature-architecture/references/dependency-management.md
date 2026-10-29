# Gradle Dependency Management

Use this reference when adding, removing, or changing an external library, Gradle plugin, or repository in a Kotlin Android project. Build conventions vary; inspect the active project before editing and preserve a coherent version-management approach.

## Inspect how the project manages dependencies

Read the relevant settings, version declarations, plugin configuration, and consuming module build files. Establish:

- Where versions and coordinates are declared: a Gradle version catalog, convention/build logic, plugin management, or another established mechanism.
- How aliases or plugins are applied in root and module build files.
- Which module and source set actually needs the dependency.
- Which repositories are configured and whether the dependency's official setup requires another one.
- The project's Gradle, Android Gradle Plugin, Kotlin, SDK, and JVM/toolchain constraints.

Follow the existing approach when it is consistent. A version catalog is useful for centralizing dependency coordinates in multi-module builds, but it is not a universal requirement. Do not introduce a catalog, migrate unrelated declarations, or change build conventions solely to match this example. If the current setup duplicates pins or causes a concrete maintenance problem, propose a focused consolidation that fits the requested scope.

## Add only the dependency that is needed

1. Confirm the consumer and check whether an existing dependency already provides the required API.
2. Read the library or plugin's official setup instructions, compatibility constraints, and required repositories. Treat the active project's compatibility policy as authoritative where it is stricter.
3. Declare the coordinate and version through the project's established mechanism. If it uses a version catalog, add or update the catalog alias and consume it in the module build file rather than repeating literal versions.
4. Use the narrowest correct configuration: runtime, unit test, instrumentation test, annotation/code generation, compiler plugin, or build classpath as appropriate.
5. If a BOM/platform manages versions, omit individual versions only for artifacts that it actually manages.
6. Resolve the Gradle graph and run the smallest relevant compilation or test task. Inspect dependency insight/reports when conflicts, unexpected transitive dependencies, or duplicate artifacts appear.

For example, when a project uses a version catalog:

```toml
[versions]
example = "<selected-version>"

[libraries]
example-android = { module = "com.example:example-android", version.ref = "example" }
example-test-support = { module = "com.example:example-test-support", version.ref = "example" }
```

```groovy
dependencies {
    implementation libs.example.android
    testImplementation libs.example.test.support
}
```

Consult the [Gradle version catalog guide](https://docs.gradle.org/current/userguide/platforms.html) for catalog syntax and the [Android Gradle Plugin compatibility guidance](https://developer.android.com/build/releases/gradle-plugin#updating-gradle) for supported Gradle versions.

## Distinguish plugins, libraries, modules, and repositories

- A **plugin** changes build behavior and is declared through plugin management, a convention plugin, or a buildscript mechanism supported by the project.
- An **external library** is an artifact consumed by production or test source and is declared in a module's dependency block.
- A **project dependency** links another Gradle module; it is not an external Maven coordinate. Check that the edge is intended and does not create a cycle.
- A **repository** is a source from which Gradle resolves plugins or artifacts. Add one only when required by the dependency's official coordinates and the project's repository policy.

Use `api` when a module intentionally exposes dependency types through its public compile-time surface. Prefer `implementation` for internal use so consumers do not inherit unnecessary compile dependencies. Keep test-only and instrumentation-only libraries out of production runtime. Use the configured KSP, KAPT, or compiler-plugin mechanism for processors and compiler plugins.

## Review after the change

- Is the version declared consistently through the project's existing mechanism?
- Is the dependency limited to actual consumers and the correct source set/configuration?
- Is a BOM or platform managing the artifact's version?
- Does the selected plugin/library version fit the project's Gradle, Kotlin, Android Gradle Plugin, SDK, and JVM constraints?
- Did a new repository, project edge, or transitive dependency enter the graph? Is it needed?
- Do targeted resolution and compilation checks succeed?

Dependency reports and automated update proposals identify candidates but do not prove compatibility or stability. Verify release status and compatibility against official project documentation and release notes. Classify prereleases explicitly; labels such as “later milestone” can include both stable and prerelease candidates. Check plugin coordinates against their official release source because app dependency reports may omit build-time plugins.
