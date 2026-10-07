---
name: format-kotlin
description: Format Kotlin source in the BESTV repository with its Gradle ktlint setup. Use when asked to format or clean up Kotlin code formatting.
---

# Format Kotlin

Use the repository's `.editorconfig` and Gradle ktlint tasks as the source of formatting rules. For this repository, the root `formatKotlin` task formats Kotlin source across modules.

## Workflow

1. Check repository instructions and `git status`. Preserve unrelated changes and do not reset or replace modified files.
2. Run `./gradlew formatKotlin` from the repository root.
3. Review the diff for unexpected edits and run `git diff --check`. Confirm that assignment expressions begin on the same line as `=`, for example `val title = when (...)`, `state = WorkBrowseState(...)`, and `modifier = Modifier`; multiline argument and modifier chains should be indented consistently.
4. Do not run tests or commit unless the user asks.

Report the formatter task result and any remaining formatting issue or unrelated working-tree change.
