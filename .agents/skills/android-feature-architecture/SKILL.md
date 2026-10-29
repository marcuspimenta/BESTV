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
3. Trace one representative feature from UI events through its state holder and operation to the repository/data source, then follow results through mapping back to rendered state. Include a use case and inner repository contract when the project uses them. For a repository-wide review, inventory modules and entry points, including workers/receivers, then inspect distinct read, write, pagination, and platform flows instead of treating one screen as representative.
4. Identify existing package conventions, DI registrations, coroutine/test dispatchers, flow-testing tools, and UI test patterns. Reuse compatible conventions; call out material inconsistencies rather than silently extending them.
5. Keep the requested change local. Do not introduce a module, framework, abstraction, or architecture migration solely to make the code resemble an example.

## Apply the patterns

- Keep UI rendering concerns in presentation and business decisions in the layer that owns them. Add a domain/use-case layer when it improves reuse, complexity management, or enforces an existing architecture; do not require trivial forwarding classes by default.
- Keep source access behind repositories. When the project uses Clean Architecture or explicit inner-layer ports, let domain operations depend on domain-owned contracts and bind them to data/platform implementations at the composition root.
- Map at meaningful boundaries: source-specific models to app/domain values near data access, then to UI-ready values and localized labels in presentation. Keep framework and UI types out of inner contracts when the project maintains those boundaries.
- Split Gradle modules by cohesive user capability when ownership, reuse, or build boundaries justify it; keep the dependency graph acyclic. Avoid direct feature-to-feature dependencies when a stable shared contract can express the interaction.
- Use the navigation system already chosen by the project. If it uses Navigation 3, consult the Navigation 3 reference for modular entry registration, saved back stacks, platform entry points, and route validation.
- Model screen state holders, commonly ViewModels on Android, as event-to-state transition owners. Publish immutable renderable state, define one-time action delivery intentionally, and preserve coroutine cancellation and latest-request behavior.
- Make data operations safe to call from the main thread, define persistence and retry behavior, and keep scheduler/platform details behind outer-layer adapters. Repeated user actions or background runs should not corrupt or duplicate stored data.
- Test observable behavior and boundary wiring. Inspect the active project’s suite before describing coverage; do not imply that repository/data or UI integration is covered when only business operations and state holders are tested.

## Focused references

Read only the references relevant to the current task:

- [Layered feature architecture](references/layered-feature-architecture.md): module choices, dependency direction, repository ports, data flow, and model mapping.
- [Data operations and persistence](references/data-operations-and-persistence.md): execution context, transport encoding, failure contracts, local/remote consistency, and safe writes.
- [Background work and platform adapters](references/background-work-and-platform-adapters.md): scheduling ownership, workers/receivers, repeatable platform writes, and capability checks.
- [Feature modularization and navigation](references/feature-modularization-and-navigation.md): capability-based module boundaries and Navigation 3 entry registration, saved state, and platform links.
- [Naming conventions](references/naming-conventions.md): naming modules, types, mappers, state/events/effects, composables, routes, DI modules, and tests while adapting to the active repository.
- [Compose UI layer](references/compose-ui-layer.md): composable boundaries, screen structure, state collection, accessibility, adaptive layouts, previews, and UI tests.
- [Dependency management](references/dependency-management.md): how to manage external dependency versions with the active project's conventions, apply plugins, and choose module scopes.
- [Dependency injection](references/dependency-injection.md): composition roots, feature registrations, object lifetimes, qualifiers, and DI smoke tests.
- [ViewModel and UI state](references/viewmodel-ui-state.md): state shape, events, effects, Compose collection, asynchronous transitions, and cancellation.
- [Testing strategy](references/testing-strategy.md): a behavior-focused test matrix for use cases, mappers, ViewModels, Compose, and DI.

When work spans several concerns, consult the corresponding references and keep the resulting architecture consistent across the feature.
