---
name: verify-clean-architecture
description: Review BESTV feature packages and Gradle dependencies for Clean Architecture boundaries, then report concrete evidence and focused fixes. Use when asked to audit, verify, or improve feature architecture.
---

# Verify Clean Architecture in BESTV

Use this skill to review existing BESTV packages and dependencies, identify boundary violations, and suggest the smallest changes that restore the intended dependency direction. When the user asks for implementation, apply the accepted changes and verify them. Otherwise, report findings and recommendations without editing source.

## Review workflow

1. Read repository instructions, inspect `git status`, and preserve unrelated edits.
2. Read [BESTV project guidelines](../bestv-project-guidelines/SKILL.md), [Clean Architecture documentation](../../../CLEAN_ARCHITECTURE.md), and [Layered Feature Architecture](../android-feature-architecture/references/layered-feature-architecture.md). Treat them as intended conventions, then verify all claims against current source and build files.
3. Inspect `settings.gradle` and the relevant module `build.gradle` files to establish actual Gradle dependencies. Package names alone do not prove a module boundary.
4. Inventory the feature's `data`, `domain`, `presentation`, `platform`, and `di` packages, plus shared modules involved in its flow.
5. Trace representative read and write flows end to end: presentation event/ViewModel → use case → domain repository contract → implementation/data source → mapper → presentation state. Include worker, receiver, scheduler, provider, or navigation entry points when the feature uses them.
6. Inspect imports and public method signatures at each boundary. Run `./gradlew verifyArchitecture` when the environment supports Gradle, and inspect the checker implementation at `gradle/verify-architecture.gradle` to understand exactly what it enforces.
7. Report each finding with file/package evidence, the dependency direction, why it crosses a boundary, and a concrete minimal fix. Separate confirmed violations from design choices or risks the automated checker does not cover.

## BESTV dependency rules

- Presentation may depend on domain use cases and domain values. It owns screen state, UI mapping, formatting, and Android resources.
- Domain owns business rules, domain models, use cases, and repository/output-port interfaces. It must not depend on presentation, data, Android/framework APIs, Retrofit, Room, WorkManager, or Koin.
- Data implements domain-owned repository interfaces. It owns API and persistence models, source adapters, repository implementations, and source-to-domain mapping. Data must not import presentation models, resources, or UI mappers.
- Platform adapters belong at the outer edge. They may implement domain output ports and use Android/WorkManager APIs, but platform types must not leak into domain contracts.
- Feature DI and app composition are wiring edges. Bind domain contracts to data or platform implementations there; keep direct feature-to-feature imports out of production feature code.
- Mapping belongs at the boundary it translates: source models to domain values in data; domain values to rendered UI models and localized labels in presentation. Keep Room entities out of use cases and presentation mappers.
- A `data → domain` compile-time dependency is correct when a data implementation imports and implements a domain-owned contract. At runtime, domain calls the contract and DI supplies the implementation.
- BESTV keeps layers as packages within current feature modules. `model_shared` contains multiple model concerns, so inspect imports inside it rather than assuming its packages are independently enforced Gradle layers.

## Automated check coverage

The root `verifyArchitecture` task checks production Kotlin imports under module `src/main/java`. It currently rejects:

- Domain imports of presentation, data, Android/AndroidX, Retrofit, and Koin.
- Data imports of presentation.
- `model_shared/presentation` imports of data.
- Direct package imports from another `feature_*` module.

The task is a useful guard, not a complete architecture proof. A clean result does not verify Gradle dependency declarations, type leakage through unqualified or generated APIs, ownership of business rules, correct mapper placement beyond the listed package rule, dependency inversion in call graphs, DI binding correctness, or runtime behavior. Check those manually and recommend targeted improvements to the task when an important boundary can be enforced reliably.

## How to suggest fixes

Prefer small, flow-preserving changes. For each violation:

1. Name the dependency that crosses the boundary and cite the relevant production file or Gradle declaration.
2. Identify the owning layer for the leaked type or behavior.
3. Propose a concrete move or replacement, such as introducing a domain value/interface, moving source mapping into data, moving UI mapping/resource lookup into presentation, or moving Android scheduling into a platform adapter.
4. Describe required call-site and Koin binding updates, and note any behavior/schema/route compatibility constraints.
5. Recommend only meaningful validation: `verifyArchitecture`, affected module unit tests and `ktlintCheck`, and app build/lint where integration or resources changed. Do not claim checks that were not run.

Do not recommend new Gradle modules, broad renames, or abstractions solely to match a diagram. Preserve existing TV/mobile behavior, navigation contracts, persistence schema, and background-work semantics unless the requested fix requires an explicit change.

## Review output

Start with a short overall assessment. Then list findings from highest impact to lowest, with evidence and a suggested fix for each. Close with any confirmed strengths, checker blind spots relevant to the review, and the verification commands/results. If no violations are found, say what was inspected and distinguish that result from proof of runtime correctness.
