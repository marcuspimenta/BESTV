---
name: dependabot-pr-reviewer
description: Review BESTV Dependabot dependency update pull requests, check platform build results, approve clean updates, and let the guarded auto-merge workflow merge them.
---

# Dependabot PR reviewer

Use this skill for Dependabot dependency update PRs in BESTV. The repository's `Dependabot platform builds` workflow builds Android mobile, Android TV, and the iOS simulator app. A separate workflow performs a squash merge only after a clean human approval and successful platform builds for the current PR commit.

## Review

1. Confirm the PR is open, targets `master`, and is authored by `dependabot[bot]`. Read the PR description and inspect the complete diff.
2. Identify every dependency and version being changed. BESTV keeps Gradle dependency and plugin versions in `gradle/libs.versions.toml`; the Gradle wrapper version is in `gradle/wrapper/gradle-wrapper.properties`.
3. Check that the diff is limited to the dependency update and generated metadata. Treat source, workflow, permission, or unrelated build-logic changes as a blocker unless the PR clearly explains them and they receive a separate review.
4. For major updates, build plugins, or updates with compatibility concerns, check the upstream release notes and the current Gradle/Android/Kotlin compatibility requirements. Report concrete compatibility concerns rather than guessing from the version number.
5. Record any findings and wait for the required platform build checks. If the update is safe and all builds pass on the current head commit, submit `gh pr review <number> --approve`. If there is a concrete blocker, request changes with the finding and do not approve.

## Build and merge gate

The PR checks must belong to the PR's latest head commit. The CI workflow runs:

```sh
./gradlew :androidApp:assembleAndroidMobileDebug :androidApp:assembleAndroidTVDebug
xcodebuild -project iosApp/iosApp.xcodeproj -target iosApp -configuration Debug -sdk iphonesimulator -derivedDataPath "$RUNNER_TEMP/BESTVDerivedData" CODE_SIGNING_ALLOWED=NO build
```

The Xcode target also assembles the iOS Kotlin XCFramework. The builds do not need a TMDb token.

Before approving, confirm the Android mobile and TV build job and the iOS simulator build job both succeeded on the current head SHA. If a check is pending, wait for it; if it failed, inspect the logs and report the failure. Re-review after any new commit.

The `Dependabot auto-merge` workflow watches for a successful build run and an approval. Once both are present for the same open Dependabot PR commit, it attempts a squash merge. Do not bypass repository protections or use administrative merge options. If repository protections or GitHub permissions prevent the merge, report that instead of trying to bypass the restriction.

In the final report, include the PR, dependency changes, any compatibility findings, the head SHA whose Android and iOS builds passed, and whether the merge completed or is waiting on another repository requirement.
