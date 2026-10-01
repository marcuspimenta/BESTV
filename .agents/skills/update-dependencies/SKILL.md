---
name: update-dependencies
description: Check Gradle dependency reports and buildscript classpath pins, then update stable dependency versions in Android or other Gradle projects. Use when asked to check for dependency updates or apply stable version bumps.
---

# Update Dependencies

Use this skill for dependency update work in Gradle projects. Match the scope in the request: report available versions when asked to check; edit version pins when asked to update. Do not infer permission to commit from a request to update.

## Workflow

1. Inspect repository instructions, the working tree, Gradle build files, and the version catalog or shared version constants. Preserve unrelated user changes and avoid replacing an already modified file wholesale.
2. For a Gradle project with the versions plugin task, run `./gradlew dependencyUpdates` using the repository wrapper. Otherwise use the project’s configured dependency report task. Read the report for all relevant modules. Also inspect root `build.gradle` (or its equivalent) for `buildscript.dependencies.classpath` pins; the project dependency report may not include buildscript classpaths. Check those plugin coordinates separately against their official release source or plugin portal and include stable updates in the report.
3. Select stable releases only. Treat suffixes such as `alpha`, `beta`, `rc`, `milestone`, `preview`, `snapshot`, and `canary` as prereleases. A report section called “later milestone versions” can contain both stable and prerelease candidates, so classify each candidate by its version. A stable release can replace a currently pinned prerelease when the report offers it.
4. Update only dependencies within the requested scope and with an explicit pinned version in the project. Prefer the existing central version catalog or shared constants, and update every occurrence that must stay aligned, such as OkHttp and its logging interceptor or dependencies managed by one BOM. Do not add direct pins for transitive-only dependencies.
5. Leave prerelease candidates out unless the user asks for them. For buildscript classpath updates, check compatibility among related Android Gradle Plugin, Kotlin, Compose compiler, and Gradle versions before changing pins. Do not change the Gradle wrapper unless the user includes it in scope. When the wrapper is in scope, check the wrapper version against the official compatibility ranges for the selected Android Gradle Plugin and Kotlin Gradle Plugin, then choose a stable version supported by both where possible. Include other build-time processors that consume Kotlin metadata (for example, Room's KAPT processor) in the compatibility check; a version-matrix match alone is not enough if the project build demonstrates processor incompatibility. If no compatible stable set can be verified, keep the conflicting pins unchanged and explain the blocker rather than knowingly leaving the build broken.
6. Review the diff and working tree. Keep changes limited to requested dependency declarations, version catalogs, and (when explicitly in scope) wrapper files; preserve unrelated edits. Report stable updates made plus notable prerelease candidates left out. After applying dependency changes, build the application to confirm it still compiles (for Android, use the appropriate app assemble task, such as `./gradlew assembleDebug`). If a requested compatibility update fails the build, inspect the first relevant failure and either make a compatible in-scope adjustment or restore the incompatible pin; report the exact task and outcome. Do not run tests unless the user asks for them.
7. If the user explicitly asks for a commit, stage only the dependency files changed for this task, inspect the staged file list and diff, then commit. Never include unrelated staged or unstaged work.

## Reporting

State whether the dependency report completed, list the meaningful stable version changes, identify prerelease candidates excluded when useful, and say whether verification was run. For a check-only request, do not change files.
