---
name: android-feature-architecture
description: Use when designing or reviewing Kotlin Android feature architecture, module boundaries, naming, dependency injection, data persistence, background work, Compose ViewModel state, or tests. Inspect the active project and adapt the guidance to its conventions.
---

# Android Feature Architecture

Use this skill to design or change a Kotlin Android feature with clear boundaries, predictable UI state, explicit dependency wiring, and tests at the seams that matter. It is project-agnostic: do not assume a particular app, module graph, UI toolkit, DI framework, or library version.

## Start with the active project

Before recommending or changing architecture:

1. Read the repository's architecture and product documentation, then compare its claims with current source and tests. Treat documentation as intent and source/build configuration as evidence of current behavior.
2. Inspect Gradle settings and module build files to establish the real module graph, plugin setup, SDK/language versions, and dependencies. Do not infer Gradle edges from package names or diagrams.
3. Trace one representative feature from UI events through ViewModel, use case, repository contract, implementation, data source, and back through mappers to rendered state. For a repository-wide review, inventory every module and entry point, including workers/receivers, then inspect distinct read, write, pagination, and platform flows instead of treating one screen as representative of everything.
4. Identify existing package conventions, DI registrations, coroutine/test dispatchers, flow-testing tools, and UI test patterns. Reuse compatible conventions; call out material inconsistencies rather than silently extending them.
5. Keep the requested change local. Do not introduce a module, framework, abstraction, or architecture migration solely to make the code resemble an example.

## Apply the patterns

- Keep business decisions in domain operations and keep UI rendering concerns in presentation.
- Have domain use cases depend on domain-owned repository contracts. Put network/database implementations and source-specific models in data, then bind contracts to implementations at the composition root.
- Map at boundaries: remote/database models to domain types in data; domain results to UI-ready models and resource-backed labels in presentation. Do not let inner domain contracts depend on Android resources, Compose types, Retrofit/Room types, or UI view models.
- Split Gradle modules by cohesive user capability when ownership, reuse, or build boundaries justify it; keep the app as the composition root and keep the graph acyclic. Avoid direct feature-to-feature dependencies when a stable shared contract can express the interaction.
- For Activity navigation, keep route construction/argument contracts separate from feature Activity implementations, and make the destination manifest filter agree with the route URI. Validate incoming arguments at the destination.
- Model the ViewModel as an event-to-state transition owner. Publish immutable renderable state, keep one-time effects distinct from state, and preserve coroutine cancellation and latest-request behavior.
- Make data operations safe to call from the main thread, define persistence and retry behavior, and keep scheduler/platform details behind outer-layer adapters. Repeated user actions or background runs should not corrupt or duplicate stored data.
- Test observable behavior and boundary wiring. Verify the active project’s existing suite before describing coverage; do not imply that data/repository or UI integration is covered when only use cases and ViewModels are tested.

## Focused references

Read only the references relevant to the current task:

- [Layered feature architecture](references/layered-feature-architecture.md): module choices, dependency direction, repository ports, data flow, and model mapping.
- [Data operations and persistence](references/data-operations-and-persistence.md): execution context, transport encoding, failure contracts, local/remote consistency, and safe writes.
- [Background work and platform adapters](references/background-work-and-platform-adapters.md): scheduling ownership, workers/receivers, repeatable platform writes, and capability checks.
- [Feature modularization and navigation](references/feature-modularization-and-navigation.md): splitting Gradle modules by capability and routing between feature Activities with shared deep-link contracts.
- [Naming conventions](references/naming-conventions.md): naming modules, types, mappers, state/events/effects, composables, routes, DI modules, and tests while adapting to the active repository.
- [Compose UI layer](references/compose-ui-layer.md): composable boundaries, screen structure, state collection, accessibility, adaptive layouts, previews, and UI tests.
- [Dependency management](references/dependency-management.md): how to manage external dependency versions through the required Gradle version catalog, apply plugins, and choose module scopes.
- [Dependency injection](references/dependency-injection.md): composition roots, feature registrations, object lifetimes, qualifiers, and DI smoke tests.
- [ViewModel and UI state](references/viewmodel-ui-state.md): state shape, events, effects, Compose collection, asynchronous transitions, and cancellation.
- [Testing strategy](references/testing-strategy.md): a behavior-focused test matrix for use cases, mappers, ViewModels, Compose, and DI.

When work spans several concerns, consult the corresponding references and keep the resulting architecture consistent across the feature.
